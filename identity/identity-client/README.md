# Identity Client

`identity:identity-client` 는 다른 서비스가 identity 도메인의 actor / role / introspection 기능을 재사용할 수 있게 해주는 클라이언트 모듈입니다.

현재 이 모듈이 자동 구성으로 제공하는 것은 두 가지입니다.

- 웹 기반 token introspection용 `Introspector`
- 현재 애플리케이션 namespace 기준 `NamespaceRoleCapabilitiesRegistry`

중요:

- 이 모듈은 더 이상 `JwtDecoder` 를 자동 구성하지 않습니다.
- resource server JWT 검증은 `bootstrap:resource-application-starter` 의
  `seatliberator.resource-server.security.authorize.jwk-set-uri` 설정이 담당합니다.

## 의존성

```kotlin
dependencies {
    implementation(project(":identity:identity-client"))
}
```

## 제공 타입

- `ActorContextHolder`
- `ThreadLocalActorContextHolder`
- `Capability`
- `RoleCapabilities`
- `NamespaceRoleCapabilitiesRegistry`
- `Introspector`

## Auto Configuration

등록된 auto-configuration:

- `IdentityClientNamespaceRoleAutoConfiguration`
- `WebIntrospectionAutoConfiguration`

### Namespace role registry

`IdentityClientNamespaceRoleAutoConfiguration` 은 현재 애플리케이션 namespace 와 `RoleCapabilities` 목록을 받아
`NamespaceRoleCapabilitiesRegistry` 를 생성합니다.

즉, 소비자 서비스는 자기 namespace 에 대한 capability 매핑만 정의하면 됩니다.
`Role` enum 순서(`GUEST -> USER -> MAINTAINER -> ADMIN`)를 기준으로 상위 role은 하위 role의 capability를 자동으로 포함합니다.

예:

```java
@Bean
RoleCapabilities userRoleCapabilities() {
    return new RoleCapabilities(Role.USER, Set.of(
            new SimpleCapability("post.read"),
            new SimpleCapability("post.create")
    ));
}
```

resource server bootstrap은 이 registry 를 사용해 `namespace:ROLE` authority 를 현재 앱 capability 로 확장할 수 있습니다.

### Web introspection

웹 introspection 자동 구성을 쓰려면 아래 설정을 켭니다.

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

핵심 설정:

- `identity.validate.introspection.web.enabled`
- `identity.validate.introspection.web.server.base-url`
- `identity.validate.introspection.web.server.uri`
- `identity.introspection.expiration-ms`

활성화되면 `WebClient` 기반 `Introspector` 가 등록됩니다.

## 사용 방향

### resource server 앱에서의 권장 조합

- 앱에 `seatliberator.resource-application` plugin 적용
- `seatliberator.resource-server.security.authorize.jwk-set-uri` 설정
- 필요한 `RoleCapabilities` 빈 정의
- 필요 시 `ResourceServerAuthorizeRequestMatcherCustomizer` 로 앱 전용 인가 규칙 추가

즉, JWT 검증은 bootstrap starter가 맡고, `identity-client` 는 actor / role / introspection 지원을 맡습니다.

## 주의점

- `identity-client` 만 추가한다고 resource server 보안 구성이 완성되지는 않습니다.
- `Introspector` 는 `identity.validate.introspection.web.enabled=true` 일 때만 웹 구현이 등록됩니다.
- `NamespaceRoleCapabilitiesRegistry` 는 현재 앱 namespace 기준으로 동작하므로, 각 앱이 자기 capability 매핑을 직접 제공해야 합니다.
