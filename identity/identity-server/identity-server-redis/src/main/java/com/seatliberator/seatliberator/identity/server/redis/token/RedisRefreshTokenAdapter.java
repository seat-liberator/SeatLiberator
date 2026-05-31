package com.seatliberator.seatliberator.identity.server.redis.token;

import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenRotationResult;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenRotator;
import com.seatliberator.seatliberator.identity.server.application.token.port.out.RefreshTokenStore;
import com.seatliberator.seatliberator.identity.server.domain.token.RefreshToken;
import com.seatliberator.seatliberator.identity.server.redis.token.entry.RefreshTokenRedisEntry;
import com.seatliberator.seatliberator.identity.server.redis.token.entry.RevokedRefreshTokenRedisEntry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class RedisRefreshTokenAdapter implements RefreshTokenStore, RefreshTokenRotator {
    private final StringRedisTemplate redisTemplate;
    private final RefreshTokenRedisKeyManager keyManager;
    private final ObjectMapper objectMapper;
    private final RedisScript<Long> rotationScript;

    public RedisRefreshTokenAdapter(
            StringRedisTemplate redisTemplate,
            RefreshTokenRedisKeyManager keyManager,
            ObjectMapper objectMapper,
            @Qualifier("refreshTokenRotationScript") RedisScript<Long> rotationScript
    ) {
        this.redisTemplate = redisTemplate;
        this.keyManager = keyManager;
        this.objectMapper = objectMapper;
        this.rotationScript = rotationScript;
    }

    @Override
    public boolean existsRevokedByTokenHash(String tokenHash) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(keyManager.revokedKey(tokenHash)));
    }

    @Override
    public Optional<RefreshToken> findActiveByTokenHash(String tokenHash) {
        var value = redisTemplate.opsForValue().get(keyManager.activeKey(tokenHash));

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(deserialize(value, RefreshTokenRedisEntry.class).toDomain());
    }

    @Override
    public void save(RefreshToken refreshToken, Instant now) {
        var ttl = refreshToken.remainingTtlAt(now);

        if (ttl.isZero() || ttl.isNegative()) return;

        redisTemplate.opsForValue().set(
                keyManager.activeKey(refreshToken.getTokenHash()),
                serialize(refreshToken)
        );
    }

    @Override
    public void revoke(RefreshToken refreshToken, Instant revokedAt) {
        var tokenHash = refreshToken.getTokenHash();
        var sessionExpiresAt = refreshToken.getSessionExpiresAt();

        var activeKey = keyManager.activeKey(tokenHash);
        var revokedKey = keyManager.revokedKey(tokenHash);
        var revokedTtl = Duration.between(revokedAt, sessionExpiresAt);

        redisTemplate.delete(activeKey);

        if (revokedTtl.isZero() || revokedTtl.isNegative()) return;

        var revokedEntry = RevokedRefreshTokenRedisEntry.from(refreshToken, revokedAt);

        redisTemplate.opsForValue().set(
                revokedKey,
                serialize(revokedEntry),
                revokedTtl
        );
    }

    @Override
    public RefreshTokenRotationResult rotate(RefreshToken oldToken, RefreshToken newToken, Instant rotatedAt) {
        var revokedTtl = Duration.between(rotatedAt, oldToken.getSessionExpiresAt());
        var newTokenTtl = newToken.remainingTtlAt(rotatedAt);

        if (newTokenTtl.isZero() || newTokenTtl.isNegative()) {
            return RefreshTokenRotationResult.OLD_TOKEN_NOT_FOUND;
        }

        var revokedEntry = RevokedRefreshTokenRedisEntry.from(oldToken, rotatedAt);

        var result = redisTemplate.execute(
                rotationScript,
                List.of(
                        keyManager.activeKey(oldToken.getTokenHash()),
                        keyManager.revokedKey(oldToken.getTokenHash()),
                        keyManager.activeKey(newToken.getTokenHash())
                ),
                serialize(revokedEntry),
                String.valueOf(Math.max(1L, revokedTtl.toMillis())),
                serialize(RefreshTokenRedisEntry.from(newToken)),
                String.valueOf(newTokenTtl.toMillis())
        );

        if (result == null) {
            throw new IllegalStateException("Refresh token rotation script returned null.");
        }

        return switch (result.intValue()) {
            case 1 -> RefreshTokenRotationResult.SUCCESS;
            case 0 -> RefreshTokenRotationResult.OLD_TOKEN_NOT_FOUND;
            case -1 -> RefreshTokenRotationResult.NEW_TOKEN_CONFLICT;
            default -> throw new IllegalStateException("Unknown refresh token rotation result: " + result);
        };
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize Redis value.", e);
        }
    }

    private <T> T deserialize(String value, Class<T> type) {
        try {
            return objectMapper.readValue(value, type);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to deserialize Redis value.", e);
        }
    }
}
