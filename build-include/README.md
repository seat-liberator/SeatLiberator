# Build Include

`build-include` 는 SeatLiberator 의 애플리케이션 모듈이 공통으로 사용하는 Gradle convention plugin 을 모아둔 build logic 모듈입니다.

이 모듈의 목적은 Spring Boot 애플리케이션 부트스트랩에 필요한 반복 의존성 선언을 공통화하고, 애플리케이션 타입별 차이만 얇게 드러내는 것입니다.

현재는 다음 두 가지 애플리케이션 타입을 지원합니다.

- `resource server` 계열 애플리케이션
- `identity` 계열 애플리케이션

## 왜 필요한가

기존에는 `reservation`, `board`, `notification`, `identity` 애플리케이션 모듈이 각각 비슷한 `build.gradle.kts` 구성을 직접 선언하고 있었습니다.

이 방식은 다음 문제를 만들었습니다.

- 웹, 보안, JPA, 이벤트, 테스트 의존성이 여러 모듈에 반복 선언된다.
- 새 애플리케이션 모듈을 추가할 때 복사-수정 비용이 크다.
- 공통 의존성 정책 변경 시 여러 모듈을 함께 수정해야 한다.
- resource server 계열과 identity 계열의 차이가 코드보다 선언 중복에 묻힌다.

`build-include` 는 이 문제를 build logic 수준에서 해결합니다.

## 구조

루트 프로젝트는 [settings.gradle.kts](/home/lilamaris/IdeaProjects/SeatLiberator/settings.gradle.kts) 에서 `includeBuild("build-include")` 로 이 모듈을 포함합니다.

이후 애플리케이션 모듈은 직접 의존성을 길게 적는 대신 convention plugin 하나를 선택해서 적용합니다.

## Convention Plugins

### `seatliberator.spring-application-base`

모든 애플리케이션 모듈이 공통으로 사용하는 기본 플러그인입니다.

이 플러그인은 아래 구성을 제공합니다.

- `java`
- `org.springframework.boot`
- `io.spring.dependency-management`

공통 의존성은 다음과 같습니다.

| 분류 | 제공 항목 |
| --- | --- |
| Web | `spring-boot-starter-webmvc`, `spring-boot-starter-validation` |
| Security | `spring-boot-starter-security` |
| Persistence | `spring-boot-starter-data-jpa`, `postgresql`, `h2` |
| Event | `spring-boot-starter-kafka` |
| Lombok | `lombok`, `annotationProcessor` |
| Test | `spring-boot-starter-test`, `spring-boot-starter-webmvc-test`, `spring-security-test`, `testcontainers-postgresql`, `testcontainers-jdbc` |

또한 `test` task 에 `useJUnitPlatform()` 을 적용합니다.

이 플러그인은 "모든 애플리케이션이 공통으로 가져가는 부트스트랩 의존성" 까지만 책임집니다.

도메인 API 의존성, 외부 모듈 의존성, 타입별 보안 의존성은 각 하위 플러그인 또는 애플리케이션 모듈에서 선언합니다.

### `seatliberator.spring-resource-server-application`

JWT 기반 resource server 애플리케이션용 플러그인입니다.

이 플러그인은 `seatliberator.spring-application-base` 위에 다음 의존성을 추가합니다.

| 분류 | 제공 항목 |
| --- | --- |
| Security | `identity-client`, `spring-boot-starter-oauth2-resource-server` |
| Event | `event-relay-support-jpa`, `event-relay-support-kafka` |

현재 이 타입을 사용하는 모듈은 다음과 같습니다.

- `board:board-application`
- `reservation:reservation-application`
- `notification:notification-application`

### `seatliberator.spring-identity-application`

`identity` 애플리케이션처럼 OAuth2 client 기반 인증 흐름을 가지는 애플리케이션용 플러그인입니다.

이 플러그인은 `seatliberator.spring-application-base` 위에 다음 의존성을 추가합니다.

| 분류 | 제공 항목 |
| --- | --- |
| Security | `spring-boot-starter-oauth2-client` |
| Event | `event-relay-support-jpa`, `event-relay-support-kafka` |

현재 이 타입을 사용하는 모듈은 다음과 같습니다.

- `identity:identity-application`

## 의존성 경계

공통화 이후 애플리케이션 모듈은 아래처럼 역할을 나눕니다.

### convention plugin 이 책임지는 것

- Spring Boot 애플리케이션 공통 플러그인 적용
- 웹, 보안, JPA, Kafka, 테스트 관련 공통 의존성
- 애플리케이션 타입별 보안/이벤트 보조 의존성

### 각 애플리케이션 모듈이 직접 선언하는 것

- `:reservation:reservation-api`, `:board:board-api` 같은 자기 API 모듈
- 다른 도메인과의 명시적 API 의존성
- 특정 애플리케이션만 필요한 외부 라이브러리

즉, convention plugin 은 "부트스트랩 공통성" 을 다루고, 애플리케이션 모듈은 "도메인 책임과 통합 지점" 을 직접 표현합니다.

## 새 애플리케이션 모듈 추가 방법

새 애플리케이션을 추가할 때는 먼저 애플리케이션 타입을 결정합니다.

### resource server 타입인 경우

```kotlin
plugins {
    id("seatliberator.spring-resource-server-application")
}

dependencies {
    implementation(project(":your-domain:your-domain-api"))
}
```

### identity 타입인 경우

```kotlin
plugins {
    id("seatliberator.spring-identity-application")
}

dependencies {
    implementation(project(":your-domain:your-domain-api"))
}
```

이후 필요한 API 또는 외부 의존성만 모듈에서 추가로 선언합니다.

## 확장 방향

이 구조는 단순한 의존성 축약이 아니라, 이후 공통 애플리케이션 정책을 수용하기 위한 기반입니다.

향후에는 다음 항목을 같은 방식으로 확장할 수 있습니다.

- security bootstrap 기본 설정
- actuator / observability 구성
- 운영 공통 profile 및 application 기본값
- 공통 test bootstrap

단, 공통화 계층이 각 애플리케이션의 도메인 책임까지 침범하지 않도록 build logic 의 책임은 부트스트랩 범위 안에 유지해야 합니다.
