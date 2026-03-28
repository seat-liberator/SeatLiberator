# NamespaceRole 기반 capability 확장 적용 가이드

## 목표
- `identity-application`이 발급하는 `namespace:ROLE` 스코프를 소비자 서비스에서 capability authority로 확장한다.
- 소비자 서비스는 역할 이름(`board:USER`) 대신 실제 권한 이름(`post.create`, `category.manage`)으로 인가 규칙을 작성한다.
- JWT에 일반 scope가 섞여 있어도 인증 흐름이 깨지지 않도록 한다.

## 대상
- `identity-client`를 사용하는 Spring Security Resource Server
- 서비스별 기본 grant를 `DefaultNamespaceRoleGrantProvider`로 발급받는 컨슈머 서비스

## 전제
- `identity-client` 의존성이 추가되어 있어야 한다.
- `identity-core`의 `NamespaceRoleFormatter`/`NamespaceRoleDeserializer` 자동 구성을 사용한다.
- 토큰의 `scopes` 또는 `scope` claim에 `namespace:ROLE` 형식의 문자열이 포함될 수 있다.

## 적용 순서
1. 서비스 namespace를 정의한다.
2. 서비스가 사용하는 capability를 정의한다.
3. `Role -> Set<Capability>` 매핑을 빈으로 등록한다.
4. JWT 인증 변환기에 `NamespaceRoleCapabilitiesRegistry`를 연결한다.
5. 인가 규칙을 role 문자열이 아닌 capability scope 기준으로 작성한다.
6. `namespace:ROLE` 스코프를 입력으로 하는 E2E 테스트를 추가한다.

## 1. namespace 등록
서비스는 자신이 사용할 namespace를 `NamespaceProvider` 빈으로 등록해야 한다.

```java
@Configuration
public class BoardRoleCapabilityConfiguration {
    @Bean
    NamespaceProvider namespaceProvider() {
        return new NamespaceProvider("board");
    }
}
```

- namespace는 identity 서버가 발급하는 grant의 namespace와 반드시 같아야 한다.
- namespace가 다르면 `board:USER` 같은 스코프가 capability로 확장되지 않는다.

## 2. capability 정의
서비스가 실제 인가에 사용할 capability를 `Capability` 구현으로 정의한다.

```java
@RequiredArgsConstructor
public enum BoardCapability implements Capability {
    POST_CREATE("post.create", "게시글 작성"),
    CATEGORY_MANAGE("category.manage", "카테고리 관리");

    private final String scope;
    private final String description;

    @Override
    public String scope() {
        return scope;
    }

    @Override
    public String description() {
        return description;
    }
}
```

- `scope()` 값이 최종 `GrantedAuthority`가 된다.
- capability 이름은 서비스 안에서 일관되게 유지해야 한다.
- 기존 authority 문자열을 유지하고 싶다면 capability `scope()`도 그 문자열과 동일해야 한다.

## 3. Role -> Capability 매핑 등록
서비스는 `RoleCapabilities` 빈을 통해 각 role이 어떤 capability를 가지는지 명시한다.

```java
@Configuration
public class BoardRoleCapabilityConfiguration {
    @Bean
    RoleCapabilities userRoleCapabilities() {
        return new RoleCapabilities(Role.USER, Set.of(
                BoardCapability.POST_CREATE
        ));
    }

    @Bean
    RoleCapabilities maintainerRoleCapabilities() {
        return new RoleCapabilities(Role.MAINTAINER, Set.of(
                BoardCapability.POST_CREATE,
                BoardCapability.CATEGORY_MANAGE
        ));
    }
}
```

- 동일한 role을 중복 등록하면 `NamespaceRoleCapabilitiesRegistry` 생성 시 예외가 발생한다.
- 관리자 role이 하위 role capability를 모두 포함해야 한다면 명시적으로 상위집합으로 등록해야 한다.

## 4. JWT 변환기 연결
소비자 서비스는 `ActorContextJwtAuthenticationConverter`에 registry를 연결해야 한다.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter(
            NamespaceRoleDeserializer namespaceRoleDeserializer,
            NamespaceRoleCapabilitiesRegistry namespaceRoleCapabilitiesRegistry
    ) {
        return new ActorContextJwtAuthenticationConverter(
                namespaceRoleDeserializer,
                namespaceRoleCapabilitiesRegistry
        );
    }
}
```

이 변환기는 다음 순서로 동작한다.

1. JWT의 `scopes`와 `scope` claim을 읽는다.
2. `namespace:ROLE` 형식의 문자열만 `NamespaceRoleDeserializer`로 파싱한다.
3. 파싱에 성공한 role scope를 `NamespaceRoleCapabilitiesRegistry`로 확장한다.
4. 원본 scope와 확장된 capability scope를 함께 authority로 등록한다.

즉 `board:USER`가 들어오면 최종 authority에는 `board:USER`와 `post.create`가 함께 들어갈 수 있다.

## 5. 인가 규칙 작성
인가 규칙은 `namespace:ROLE`가 아니라 capability 기준으로 작성한다.

```java
http.authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.POST, "/board/*/posts")
        .hasAuthority(BoardCapability.POST_CREATE.scope())
        .requestMatchers(HttpMethod.POST, "/board/*/categories")
        .hasAuthority(BoardCapability.CATEGORY_MANAGE.scope())
);
```

- 서비스의 인가 규칙은 실제 행위 단위 capability를 기준으로 두는 편이 명확하다.
- 이후 role 정책이 바뀌어도 request matcher는 수정하지 않고 `RoleCapabilities`만 조정하면 된다.

## 6. 테스트 권장 항목
최소한 아래 케이스는 서비스마다 포함하는 것을 권장한다.

- `namespace:USER` 스코프로 기본 사용자 기능이 허용되는지
- `namespace:USER` 스코프로 관리 기능이 거부되는지
- `namespace:MAINTAINER` 또는 `namespace:ADMIN` 스코프로 관리 기능이 허용되는지
- `namespace:ROLE`과 일반 scope(`openid`, `profile`, 기타 문자열`)가 함께 있어도 인증이 실패하지 않는지
- 잘못된 role scope(`namespace:NOPE`)가 들어와도 500이 아니라 403 또는 일반 인증 실패로 처리되는지

예시:

```java
given(jwtDecoder.decode("test-token")).willReturn(
        Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject("test-user")
                .claim("scopes", List.of("board:USER", "openid"))
                .build()
);
```

## 롤아웃 체크리스트
- 서비스 namespace와 identity 서버 grant namespace가 일치하는지 확인
- 기존 `hasAuthority(...)` 문자열과 capability `scope()` 값이 일치하는지 확인
- 신규 가입자 기본 role이 실제 필요한 capability를 포함하는지 확인
- federated signup 경로도 동일한 기본 grant를 받는지 확인
- 기존 사용자에게 legacy role 백필 또는 fallback 전략이 있는지 확인

## 권장 커밋 단위
- `identity-core`: deserializer/auto-configuration 추가
- `identity-client`: capability registry 및 converter 확장 추가
- 컨슈머 서비스: capability 정의, role 매핑, security configuration 반영
- 컨슈머 서비스 테스트: role scope 기반 E2E 추가

## 주의할 점
- `ActorContextJwtAuthenticationConverter`를 연결하지 않으면 `namespace:ROLE`는 capability authority로 확장되지 않는다.
- 테스트에서 `jwt().authorities(...)`로 authority를 직접 주입하면 converter 경로를 검증하지 못한다.
- capability 이름을 변경하면 기존 인가 규칙과 테스트도 함께 수정해야 한다.
- namespace 없는 capability 문자열을 새 표준으로 사용할지, 기존 접두사 포함 문자열을 유지할지는 서비스별로 먼저 결정해야 한다.
