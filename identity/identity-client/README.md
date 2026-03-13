# identity-client README

`identity-client`는 소비자 서비스가 `identity-application`의 공개키 목록과 원격 introspection을 재사용할 수 있도록 감싼 클라이언트 모듈입니다.

이 모듈이 제공하는 기본 기능은 두 가지입니다.

- `identity.validate.jwt.*` 설정을 기반으로 `JwtDecoder`를 자동 등록합니다.
- `identity.validate.introspection.web.*` 설정을 기반으로 원격 `Introspector`를 자동 등록합니다.

## 1. 의존성 추가

소비자 모듈 `build.gradle.kts` 예시입니다.

```kotlin
dependencies {
    implementation(project(":identity:identity-client"))

    // JWT 기반 Resource Server를 함께 쓸 경우
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
}
```

`identity-client`는 `identity-core`를 함께 가져오므로, 별도 설정이 없더라도 `Introspector` 기본 구현은 제공됩니다. 다만 기본 구현은 `DenyAllIntrospector`이므로 토큰을 항상 비활성으로 판단합니다.

## 2. JWT 검증 사용

JWT 검증을 쓰려면 `JWKS` 엔드포인트 위치를 설정합니다.

```yaml
identity:
  validate:
    jwt:
      enabled: true
      jwks-server:
        base-url: http://localhost:8080
        uri: /.well-known/jwks.json
```

활성화되면 `NimbusJwtDecoder` 기반 `JwtDecoder` 빈이 자동 등록됩니다.

- `enabled=true`일 때 `jwks-server.base-url`, `jwks-server.uri` 둘 다 필요합니다.
- 이미 다른 `JwtDecoder` 빈이 있으면 이 모듈은 덮어쓰지 않습니다.

Spring Security Resource Server에서 사용하는 예시는 다음과 같습니다.

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
```

이 경우 소비자 서비스는 `spring.security.oauth2.resourceserver.jwt.jwk-set-uri`를 직접 적지 않아도 됩니다. `identity-client`가 만든 `JwtDecoder`를 그대로 사용하면 됩니다.

## 3. 원격 Introspection 사용

보안이 중요한 작업에서 원격 검증이 필요하다면 introspection을 활성화합니다.

```yaml
identity:
  validate:
    introspection:
      web:
        enabled: true
        server:
          base-url: http://localhost:8080
          uri: /introspect
```

현재 구현 기준으로 introspection 자동 구성은 애플리케이션 컨텍스트에 `WebClient` 빈이 이미 있을 때만 활성화됩니다. 따라서 소비자 서비스에서 `WebClient` 빈을 하나 제공해야 합니다.

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class IdentityClientWebConfig {
    @Bean
    WebClient identityWebClient() {
        return WebClient.builder().build();
    }
}
```

그다음 `Introspector`를 주입받아 직접 사용하면 됩니다.

```java
package com.example.demo.application;

import com.seatliberator.seatliberator.identity.core.introspect.Introspection;
import com.seatliberator.seatliberator.identity.core.introspect.Introspector;
import org.springframework.stereotype.Service;

@Service
public class SensitiveActionAuthorizer {
    private final Introspector introspector;

    public SensitiveActionAuthorizer(Introspector introspector) {
        this.introspector = introspector;
    }

    public boolean authorize(String token) {
        Introspection result = introspector.introspect(token);
        return result != null && result.active();
    }
}
```

`Introspection`에서 확인할 수 있는 값은 다음과 같습니다.

- `active()`: 토큰 활성 여부
- `expiration()`: 만료 시각 epoch milliseconds
- `actor()`: 식별된 사용자 정보

`actor()`는 다음 값을 가집니다.

- `subject()`: 사용자 식별자
- `scopes()`: 권한 스코프 집합

## 4. 원격 서버와의 계약

현재 클라이언트 구현 기준으로 기대하는 원격 API 계약은 아래와 같습니다.

### JWKS

- `GET /.well-known/jwks.json`
- 표준 JWK Set JSON 응답

### Introspection

- `POST /introspect`
- 요청 바디

```json
{
  "token": "<access-token>"
}
```

- 응답 예시

```json
{
  "active": true,
  "expiration": 1741852800000,
  "actor": {
    "subject": "user-123",
    "scopes": ["reservation:read", "reservation:write"]
  }
}
```

비활성 토큰은 아래처럼 응답되면 됩니다.

```json
{
  "active": false,
  "expiration": null,
  "actor": null
}
```

## 5. 권장 사용 방식

- 일반적인 API 인증은 JWT + JWKS 기반 `JwtDecoder`로 처리합니다.
- 결제, 좌석 확정, 관리자 기능처럼 신뢰 수준이 더 필요한 작업만 `Introspector`로 원격 재검증합니다.
- JWT 검증과 introspection을 동시에 켜도 됩니다. 둘은 용도가 다릅니다.

## 6. 주의할 점

- `identity.validate.jwt.enabled=true`이면 `jwks-server.base-url`, `jwks-server.uri`가 모두 필요합니다.
- `identity.validate.introspection.web.enabled=true`이면 `server.base-url`, `server.uri`가 모두 필요합니다.
- 현재 구현상 `WebClient` 빈이 없으면 원격 `Introspector` 자동 구성이 활성화되지 않습니다.
- introspection을 별도로 활성화하지 않으면 `identity-core`의 기본 `DenyAllIntrospector`가 사용됩니다.
