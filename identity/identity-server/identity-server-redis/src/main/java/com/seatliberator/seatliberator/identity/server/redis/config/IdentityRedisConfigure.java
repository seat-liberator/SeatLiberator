package com.seatliberator.seatliberator.identity.server.redis.config;

import com.seatliberator.seatliberator.identity.server.redis.token.RefreshTokenRedisKeyManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
@EnableConfigurationProperties(IdentityRedisProperties.class)
public class IdentityRedisConfigure {
    @Bean
    RefreshTokenRedisKeyManager refreshTokenRedisKeyManager(IdentityRedisProperties properties) {
        var keyPrefix = properties.keyPrefix();

        return new RefreshTokenRedisKeyManager(keyPrefix);
    }

    @Bean
    DefaultRedisScript<Long> refreshTokenRotationScript() {
        return new DefaultRedisScript<>("""
                local old = redis.call("GET", KEYS[1])
                
                if not old then
                    return 0
                end
                
                if redis.call("EXISTS", KEYS[3]) == 1 then
                    return -1
                end
                
                redis.call("DEL", KEYS[1])
                redis.call("SET", KEYS[2], ARGV[1], "PX", ARGV[2])
                redis.call("SET", KEYS[3], ARGV[3], "PX", ARGV[4])
                
                return 1
                """,
                Long.class
        );
    }
}
