# Board Application

`board:board-application` 은 게시판, 카테고리, 게시글 관리와 board 네임스페이스 권한 정책을 담당하는 Spring Boot 애플리케이션입니다.

## 빌드 구성

- plugin: `seatliberator.resource-application`
- 직접 의존성: `:board:board-api`

즉, 웹/JPA/resource-server 보안 부트스트랩은 convention plugin 과 bootstrap starter가 제공하고, 이 모듈은 board 도메인과 board 전용 권한 규칙만 표현합니다.

## 패키지 구조

- `board.application`: 유스케이스, 포트, 커맨드
- `board.domain`: `Board`, `Category`, `Post`
- `board.infrastructure.persistence`: JPA 저장소 구현
- `board.infrastructure.web`: REST 컨트롤러와 요청 DTO
- `board.infrastructure.security`: board capability 와 보안 규칙

## 실행 설정

기본 프로필 구조:

- [application.yml](/home/lilamaris/IdeaProjects/SeatLiberator/board/board-application/src/main/resources/application.yml):
  기본 포트와 활성 프로필
- [application-local.yml](/home/lilamaris/IdeaProjects/SeatLiberator/board/board-application/src/main/resources/application-local.yml):
  로컬 DB, H2, JWK Set URI
- `application-dev.yml`: 개발 환경용 DB / JWK Set 설정

resource server 실행에 필요한 핵심 속성:

- `seatliberator.bootstrap.resource-server.security.jwk-set-uri`
- `spring.datasource.*`
- `spring.jpa.*`

## API

### Board

- `GET /board`
- `GET /board/{boardId}`
- `POST /board`
- `PATCH /board/{boardId}`
- `DELETE /board/{boardId}`

### Category

- `GET /board/{boardId}/categories`
- `GET /board/{boardId}/categories/{categoryId}`
- `POST /board/{boardId}/categories`
- `PATCH /board/{boardId}/categories/{categoryId}`
- `DELETE /board/{boardId}/categories/{categoryId}`

### Post

- `GET /board/{boardId}/posts`
- `GET /board/{boardId}/posts/{postId}`
- `POST /board/{boardId}/posts`
- `PATCH /board/{boardId}/posts/{postId}`
- `DELETE /board/{boardId}/posts/{postId}`

## 보안

이 모듈은 bootstrap starter가 만드는 기본 `SecurityFilterChain`
위에 [SecurityConfiguration.java](/home/lilamaris/IdeaProjects/SeatLiberator/board/board-application/src/main/java/com/seatliberator/seatliberator/board/infrastructure/security/SecurityConfiguration.java)
의 `ResourceServerSecurityCustomizer` 로 board 전용 권한 규칙을 추가합니다.

주요 권한 규칙:

- `POST /board/{boardId}/categories`: `category.manage`
- `PATCH /board/{boardId}/categories/{categoryId}`: `category.manage`
- `DELETE /board/{boardId}/categories/{categoryId}`: `category.manage`
- `POST /board/{boardId}/posts`: `post.create`
- 그 외 요청: 인증 필요

## 테스트

보안 통합 테스트는 `MockMvc` 기반으로 동작하며, resource-server bootstrap 경계가 깨지지
않는지 [BoardAuthorizationE2ETest.java](/home/lilamaris/IdeaProjects/SeatLiberator/board/board-application/src/test/java/com/seatliberator/seatliberator/board/infrastructure/web/BoardAuthorizationE2ETest.java)
에서 검증합니다.
