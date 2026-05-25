# Bootstrap Modules

`bootstrap` 는 SeatLiberator 애플리케이션 타입별 공통 Spring runtime bootstrap 을 담는 모듈 모음입니다.

현재 모듈은 두 개입니다.

- `bootstrap:web-application-starter`
- `bootstrap:resource-application-starter`

## `bootstrap:web-application-starter`

공통 웹/JPA bootstrap 을 제공합니다.

- `spring-boot-starter-webmvc`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- event-relay 공통 의존성
- `Clock` 기본 빈
- `seatliberator.application.*` 설정 바인딩

현재 노출 설정:

- `seatliberator.application.enabled`
- `seatliberator.application.zone-id`

## `bootstrap:resource-application-starter`

resource server 계열 애플리케이션의 보안 bootstrap 을 제공합니다.

- `SecurityFilterChain` 기본 구성
- identity-client가 제공하는 actor context filter 연결
- namespace role 기반 authority 확장 지원
- resource server 보안 확장용 SPI

현재 auto-configuration 경계:

- `ResourceServerSecurityAutoConfiguration`:
  stateless filter chain 생성과 `ResourceServerHttpSecurityCustomizer` 조립
- `ResourceServerAuthorizeAutoConfiguration`:
  OAuth2 resource server wiring, actor context filter 연결, authorize 규칙 조립

현재 설정 prefix:

- `seatliberator.resource-server.security.*`
- `seatliberator.resource-server.security.authorize.*`

현재 확장 SPI:

- `ResourceServerHttpSecurityCustomizer`
- `ResourceServerOAuth2Customizer`
- `ResourceServerAuthorizeRequestMatcherCustomizer`

의도는 공통 기본값은 bootstrap 이 제공하고, 앱 모듈은 matcher 규칙이나 필요한 추가 `HttpSecurity` 확장만 제공하는 것입니다.
