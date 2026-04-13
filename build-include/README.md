# Build Include

`build-include` 는 SeatLiberator 애플리케이션 모듈이 공통으로 사용하는 Gradle convention plugin 모음입니다.

목표는 두 가지입니다.

- 애플리케이션 모듈에서 반복되던 Spring Boot 부트스트랩 선언을 줄인다.
- 도메인 API 의존성과 부트스트랩 의존성의 경계를 분리한다.

루트 프로젝트는 [settings.gradle.kts](/home/lilamaris/IdeaProjects/SeatLiberator/settings.gradle.kts) 에서
`includeBuild("build-include")` 로 이 모듈을 포함합니다.

## 제공 플러그인

### `base.seatliberator.spring-application-base`

모든 애플리케이션 모듈이 공통으로 사용하는 가장 얇은 base plugin 입니다.

적용 항목:

- `java`
- `org.springframework.boot`
- `io.spring.dependency-management`
- Lombok `compileOnly` / `annotationProcessor`
- `test.useJUnitPlatform()`

이 플러그인은 "Spring Boot 애플리케이션으로 동작하기 위한 최소 공통성"만 제공합니다.

### `seatliberator.web-application`

일반 웹 애플리케이션용 convention plugin 입니다.

현재 추가되는 의존성:

- `implementation(project(":bootstrap:web-application-starter"))`
- `testImplementation("org.springframework.boot:spring-boot-starter-test")`
- `testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")`
- `testImplementation("org.testcontainers:postgresql")`
- `testImplementation("org.testcontainers:jdbc")`
- `annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")`

현재 이 플러그인을 직접 사용하는 모듈:

- `identity:identity-application`

### `seatliberator.resource-application`

JWT resource server 계열 애플리케이션용 convention plugin 입니다.

현재 추가되는 의존성:

- `implementation(project(":bootstrap:resource-application-starter"))`
- `testImplementation("org.springframework.security:spring-security-test")`

이 플러그인은 내부적으로 `seatliberator.web-application` 위에 올라갑니다.

현재 이 플러그인을 사용하는 모듈:

- `reservation:reservation-application`
- `board:board-application`
- `notification:notification-application`

## bootstrap 모듈과의 관계

convention plugin 은 bootstrap starter를 "다시 export" 하지 않습니다.

- 앱 모듈은 plugin 으로 bootstrap을 가져온다.
- bootstrap 모듈은 필요한 SPI만 `api` 로 연다.
- 도메인 API 모듈은 실제 public signature에 드러나는 의존성만 `api` 로 연다.

즉, 의존성 경계 판단은 app module 이 아니라 starter / api module 에서 일어납니다.

## bootstrap starter 역할

### `bootstrap:web-application-starter`

공통 웹 부트스트랩을 담당합니다.

- `spring-boot-starter-webmvc`
- `spring-boot-starter-validation`
- `spring-boot-starter-data-jpa`
- `event-relay:event-relay-core`
- `Clock` 기본 빈
- `seatliberator.application.*` 설정 바인딩

### `bootstrap:resource-application-starter`

resource server 보안 부트스트랩을 담당합니다.

- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`
- `identity:identity-client`
- 기본 `JwtDecoder`
- 기본 `SecurityFilterChain`
- actor context binding과 actor-aware authentication converter
- `ResourceServerHttpSecurityCustomizer`
- `ResourceServerOAuth2Customizer`
- `ResourceServerAuthorizeRequestMatcherCustomizer`
- `seatliberator.resource-server.security.*`
- `seatliberator.resource-server.security.authorize.*`

현재 auto-configuration 책임은 아래처럼 나뉩니다.

- `ResourceServerSecurityAutoConfiguration`:
  stateless resource server filter chain 생성과 공통 `HttpSecurity` 조립
- `ResourceServerAuthorizeAutoConfiguration`:
  JWT decoder, OAuth2 resource server wiring, actor context binding, authorize rule 조립

## 앱 모듈이 직접 선언하는 것

convention plugin 을 적용해도 아래 항목은 각 앱 모듈이 직접 선언합니다.

- 자기 도메인 API 의존성
- 다른 도메인과의 명시적 API 의존성
- 앱 전용 외부 라이브러리
- 타입별 추가 보안 라이브러리

예:

```kotlin
plugins {
    id("seatliberator.resource-application")
}

dependencies {
    implementation(project(":board:board-api"))
}
```

```kotlin
plugins {
    id("seatliberator.web-application")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation(project(":identity:identity-api"))
}
```

## 정리

- build-include 는 부트스트랩 공통성을 묶는 레이어입니다.
- bootstrap starter 는 실행 시점 SPI와 auto-configuration 을 제공합니다.
- 도메인 의미가 있는 의존성은 각 애플리케이션과 API 모듈이 직접 표현합니다.
