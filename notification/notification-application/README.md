# Notification Application

`notification:notification-application` 은 사용자 알림 저장과 조회, 그리고 외부 도메인에서 발행한 알림 생성 이벤트 소비를 담당하는 Spring Boot 애플리케이션입니다.

## 빌드 구성

- plugin: `seatliberator.resource-application`
- 직접 의존성: `:notification:notification-api`

## 주요 역할

- notification 생성 유스케이스 제공
- 현재 사용자 알림 조회 REST API 제공
- `NOTIFICATION_CREATE_REQUEST` 이벤트 소비

## 패키지 구조

- `notification.application`: 포트와 서비스
- `notification.domain`: `Notification`, `NotificationLevel`
- `notification.infrastructure.persistence`: JPA 저장소 구현
- `notification.infrastructure.event`: event-relay listener
- `notification.infrastructure.web`: 알림 조회 컨트롤러

## 실행 설정

프로필 파일:

- [application.yml](/home/lilamaris/IdeaProjects/SeatLiberator/notification/notification-application/src/main/resources/application.yml)
- [application-local.yml](/home/lilamaris/IdeaProjects/SeatLiberator/notification/notification-application/src/main/resources/application-local.yml)
- [application-dev.yml](/home/lilamaris/IdeaProjects/SeatLiberator/notification/notification-application/src/main/resources/application-dev.yml)

핵심 설정:

- `identity.client.jwk-set-uri`
- `spring.datasource.*`
- `spring.jpa.*`

## API

- `GET /notification`

현재 로그인한 사용자의 알림 목록을 반환합니다. 사용자
식별자는 [NotificationController.java](/home/lilamaris/IdeaProjects/SeatLiberator/notification/notification-application/src/main/java/com/seatliberator/seatliberator/notification/infrastructure/web/controller/NotificationController.java)
에서 `ActorContextHolder` 로 가져옵니다.

이 모듈은 별도 보안 설정 클래스를 두지 않고, bootstrap starter가 제공하는 기본 resource server 보안 구성을 그대로 사용합니다.

## 이벤트 소비

[NotificationEventListener.java](/home/lilamaris/IdeaProjects/SeatLiberator/notification/notification-application/src/main/java/com/seatliberator/seatliberator/notification/infrastructure/event/NotificationEventListener.java)
는 `NOTIFICATION_CREATE_REQUEST` 이벤트를 받아 알림을 저장합니다.

처리 흐름:

1. 이벤트 payload 의 `level` 문자열을 `NotificationLevel` 로 변환합니다.
2. `NotificationRegisterCommand` 를 만듭니다.
3. `NotificationRegistrar` 에 위임해 알림을 저장합니다.

잘못된 level 값은 무시됩니다.
