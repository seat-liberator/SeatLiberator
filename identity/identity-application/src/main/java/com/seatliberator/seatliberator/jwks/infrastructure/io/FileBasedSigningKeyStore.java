package com.seatliberator.seatliberator.jwks.infrastructure.io;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import com.seatliberator.seatliberator.jwks.application.config.JWKSProperties;
import com.seatliberator.seatliberator.jwks.application.port.out.KeyStore;
import com.seatliberator.seatliberator.jwks.domain.KeyStatus;
import com.seatliberator.seatliberator.jwks.domain.SigningKey;
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
public class FileBasedSigningKeyStore implements KeyStore {
    private final String signableKid;
    private final Map<String, SigningKey> keyMap;

    public FileBasedSigningKeyStore(
            JWKSProperties jwksProperties,
            ResourceLoader resourceLoader
    ) {
        this.signableKid = jwksProperties.signableKid();
        this.keyMap = jwksProperties.keys().stream()
                .map(entry -> loadKey(entry, resourceLoader))
                .collect(Collectors.toUnmodifiableMap(
                        SigningKey::kid,
                        Function.identity()
                ));
    }
    
    @Override
    public SigningKey getSignableKey() {
        return Optional.ofNullable(keyMap.get(signableKid))
                .filter(SigningKey::canSign)
                .orElseThrow(() -> new IllegalStateException("Active signable key not found: " + signableKid));
    }

    @Override
    public Optional<SigningKey> getByKid(String kid) {
        return Optional.ofNullable(keyMap.get(kid));
    }

    @Override
    public List<SigningKey> getAllVerifiableKey() {
        return keyMap.values().stream()
                .filter(SigningKey::canVerify)
                .toList();
    }

    private SigningKey loadKey(
            JWKSProperties.KeyEntry entry,
            ResourceLoader resourceLoader
    ) {
        try {
            KeyStatus keyStatus = entry.kid().equals(signableKid)
                    ? KeyStatus.SIGNABLE
                    : KeyStatus.VERIFY_ONLY;

            Resource privateKeyResource = resourceLoader.getResource(entry.privateKey());
            Resource publicKeyResource = resourceLoader.getResource(entry.publicKey());

            RSAPrivateKey privateKey = readPrivateKey(privateKeyResource);
            RSAPublicKey publicKey = readPublicKey(publicKeyResource);

            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(entry.kid())
                    .algorithm(JWSAlgorithm.RS256)
                    .build();
            
            return new SigningKey(
                    entry.kid(),
                    keyStatus,
                    rsaKey
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
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePrivate(new X509EncodedKeySpec(decoded));
    }
}
