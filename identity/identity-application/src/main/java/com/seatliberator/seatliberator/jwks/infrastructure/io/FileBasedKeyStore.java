package com.seatliberator.seatliberator.jwks.infrastructure.io;

import com.seatliberator.seatliberator.jwks.application.config.JwksProperties;
import com.seatliberator.seatliberator.jwks.application.port.out.KeyStore;
import com.seatliberator.seatliberator.jwks.domain.KeyStatus;
import com.seatliberator.seatliberator.jwks.domain.RSASignatureKey;
import com.seatliberator.seatliberator.jwks.domain.RSAVerificationKey;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class FileBasedKeyStore implements KeyStore {
    private final String signableKid;
    private final Map<String, RSASignatureKey> keyMap;

    public FileBasedKeyStore(
            JwksProperties jwksProperties,
            ResourceLoader resourceLoader
    ) {
        if (!jwksProperties.isConfigured()) {
            throw new IllegalStateException("identity.jwks properties are not configured");
        }

        this.signableKid = jwksProperties.signableKid();
        this.keyMap = jwksProperties.keys().stream()
                .map(entry -> loadKey(entry, resourceLoader))
                .collect(Collectors.toUnmodifiableMap(
                        RSASignatureKey::getKid,
                        Function.identity()
                ));
    }

    @Override
    public RSASignatureKey getSignableKey() {
        return Optional.ofNullable(keyMap.get(signableKid))
                .filter(RSASignatureKey::canSign)
                .orElseThrow(() -> new IllegalStateException("Active signable key not found: " + signableKid));
    }

    @Override
    public List<RSAVerificationKey> getAllVerifiableKey() {
        return keyMap.values().stream()
                .filter(RSASignatureKey::canVerify)
                .map(RSAVerificationKey.class::cast)
                .toList();
    }

    private RSASignatureKey loadKey(
            JwksProperties.KeyEntry entry,
            ResourceLoader resourceLoader
    ) {
        try {
            KeyStatus keyStatus = entry.kid().equals(signableKid)
                    ? KeyStatus.SIGNABLE
                    : KeyStatus.VERIFY_ONLY;

            Resource publicKeyResource = resourceLoader.getResource(entry.publicKey());
            Resource privateKeyResource = resourceLoader.getResource(entry.privateKey());

            RSAPublicKey publicKey = readPublicKey(publicKeyResource);
            RSAPrivateKey privateKey = readPrivateKey(privateKeyResource);

            return new RSASignatureKey(
                    entry.kid(),
                    keyStatus,
                    publicKey,
                    privateKey
            );
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load key: " + entry.kid());
        }
    }

    private RSAPrivateKey readPrivateKey(Resource resource) throws Exception {
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        key = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private RSAPublicKey readPublicKey(Resource resource) throws Exception {
        String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        key = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
    }
}
