# Board Application

`board:board-application` 모듈은 게시판, 카테고리, 게시글 관리와 게시판 네임스페이스 권한 정책을 담당하는 Spring Boot 기반 애플리케이션입니다.

이 모듈은 `Board`, `Category`, `Post` 도메인을 중심으로 application, domain, infrastructure 계층으로 구성되어 있습니다.

## 개요

| 기능 | 설명 | 외부 노출 방식 |
| --- | --- | --- |
| `board` | 게시판 생성/조회/수정/삭제 | REST API |
| `category` | 게시판별 카테고리 생성/조회/수정/삭제 | REST API |
| `post` | 게시판별 게시글 생성/조회/수정/삭제 | REST API |
| `security` | JWT 기반 인증과 board 네임스페이스 권한 매핑 | Spring Security 설정 |

## 패키지 구성

| 패키지 | 역할 |
| --- | --- |
| `com.seatliberator.seatliberator.board.application` | 게시판/카테고리/게시글 유스케이스, 포트, 엔트리, 예외 |
| `com.seatliberator.seatliberator.board.domain` | `Board`, `Category`, `Post` 도메인 모델 |
| `com.seatliberator.seatliberator.board.infrastructure.persistence` | JPA 기반 저장소 구현 |
| `com.seatliberator.seatliberator.board.infrastructure.web` | REST 컨트롤러, 요청 DTO, 예외 응답 매핑 |
| `com.seatliberator.seatliberator.board.infrastructure.security` | JWT 인증 변환, 권한 규칙, board 네임스페이스 capability 구성 |

## 핵심 정책

- 모든 API 요청은 인증이 필요합니다.
- 카테고리 생성/수정/삭제는 `category.manage` 권한이 필요합니다.
- 게시글 생성은 `post.create` 권한이 필요합니다.
- 게시판, 카테고리, 게시글 조회/수정/삭제 시 대상 리소스가 없으면 `404 ProblemDetail` 을 반환합니다.
- 게시판 생성 시 이름은 필수이며, null 이면 예외가 발생합니다.
- 카테고리 생성 시 이름은 공백을 trim 한 뒤 검증하며, 비어 있으면 예외가 발생합니다.
- 게시글 생성 시 `categoryId`, `title`, `content` 는 필수입니다.
- 게시글 수정은 부분 수정으로 동작하며, `null` 로 전달된 필드는 기존 값을 유지합니다.
- 카테고리에 게시글이 하나라도 있으면 해당 카테고리는 삭제할 수 없습니다.
- 게시글은 반드시 같은 게시판에 속한 카테고리에만 연결할 수 있습니다.

## API

이 모듈은 `BoardControllerAdvice` 를 통해 예외를 `ProblemDetail` 로 변환합니다.

- `BoardNotFoundException`, `CategoryNotFoundException`, `PostNotFoundException` -> `404 Not Found`
- `MethodArgumentNotValidException`, `IllegalArgumentException` -> `400 Bad Request`

### Board API

| Method | Path | 설명 |
| --- | --- | --- |
| `GET` | `/board` | 게시판 목록 조회 |
| `GET` | `/board/{boardId}` | 게시판 단건 조회 |
| `POST` | `/board` | 게시판 생성 |
| `PATCH` | `/board/{boardId}` | 게시판 수정 |
| `DELETE` | `/board/{boardId}` | 게시판 삭제 |

**`POST /board`**

```json
{
  "name": "notice",
  "description": "공지 게시판"
}
```

성공 응답:

```json
{
  "boardId": "7b9f80de-7f2e-44f5-a269-0da0fb8a2f63",
  "name": "notice",
  "description": "공지 게시판"
}
```

**`PATCH /board/{boardId}`**

```json
{
  "name": "news",
  "description": "운영 소식"
}
```

설명:

- `PATCH` 는 부분 수정 semantics 로 동작하며 `null` 필드는 기존 값을 유지합니다.
- 삭제 성공 시 `204 No Content` 를 반환합니다.

### Category API

| Method | Path | 설명 |
| --- | --- | --- |
| `GET` | `/board/{boardId}/categories` | 카테고리 목록 조회 |
| `GET` | `/board/{boardId}/categories/{categoryId}` | 카테고리 단건 조회 |
| `POST` | `/board/{boardId}/categories` | 카테고리 생성 |
| `PATCH` | `/board/{boardId}/categories/{categoryId}` | 카테고리 수정 |
| `DELETE` | `/board/{boardId}/categories/{categoryId}` | 카테고리 삭제 |

**`POST /board/{boardId}/categories`**

```json
{
  "name": "general",
  "description": "기본 카테고리"
}
```

성공 응답:

```json
{
  "categoryId": "4a1e54a7-6fc7-4551-beb0-26ae7ec084f8",
  "boardId": "7b9f80de-7f2e-44f5-a269-0da0fb8a2f63",
  "name": "general",
  "description": "기본 카테고리"
}
```

설명:

- 생성 성공 시 `201 Created` 와 `Location` 헤더를 반환합니다.
- 카테고리 이름은 trim 후 비어 있으면 안 됩니다.
- 게시글이 연결된 카테고리는 삭제할 수 없습니다.

### Post API

| Method | Path | 설명 |
| --- | --- | --- |
| `GET` | `/board/{boardId}/posts` | 게시글 목록 조회 |
| `GET` | `/board/{boardId}/posts/{postId}` | 게시글 단건 조회 |
| `POST` | `/board/{boardId}/posts` | 게시글 생성 |
| `PATCH` | `/board/{boardId}/posts/{postId}` | 게시글 수정 |
| `DELETE` | `/board/{boardId}/posts/{postId}` | 게시글 삭제 |

**`POST /board/{boardId}/posts`**

```json
{
  "categoryId": "4a1e54a7-6fc7-4551-beb0-26ae7ec084f8",
  "title": "첫 글",
  "content": "안녕하세요."
}
```

성공 응답:

```json
{
  "postId": "f64a0bb4-8d25-4d1b-b9e6-0b6b1588f4d6",
  "title": "첫 글",
  "content": "안녕하세요.",
  "categoryId": "4a1e54a7-6fc7-4551-beb0-26ae7ec084f8"
}
```

**`PATCH /board/{boardId}/posts/{postId}`**

```json
{
  "categoryId": "4a1e54a7-6fc7-4551-beb0-26ae7ec084f8",
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

설명:

- 생성 성공 시 `201 Created` 와 `Location` 헤더를 반환합니다.
- `categoryId`, `title`, `content` 는 생성 시 필수입니다.
- 수정 시 `categoryId` 를 보내면 같은 게시판에 속한 카테고리인지 검증합니다.
- 수정 시 `null` 필드는 기존 값을 유지합니다.
- 삭제 성공 시 `204 No Content` 를 반환합니다.

### Authorization

`SecurityConfiguration` 기준으로 주요 권한 규칙은 다음과 같습니다.

| 요청 | 필요 권한 |
| --- | --- |
| `POST /board/{boardId}/categories` | `category.manage` |
| `PATCH /board/{boardId}/categories/{categoryId}` | `category.manage` |
| `DELETE /board/{boardId}/categories/{categoryId}` | `category.manage` |
| `POST /board/{boardId}/posts` | `post.create` |
| 그 외 요청 | 인증 필요 |

`BoardRoleCapabilityConfiguration` 에서는 `board` 네임스페이스 기준 capability 를 등록합니다.

- `GUEST`: `post.list`, `category.list`
- `USER`: `post.read`, `post.create`, `owned.post.update`, `owned.post.delete`, `comment.create`, `owned.comment.update`, `owned.comment.delete`
- `MAINTAINER`: `post.manage`, `comment.manage`, `category.create`, `category.manage`

## ERD

이 모듈의 JPA 엔티티는 `Board`, `Category`, `Post` 세 개입니다.

중요:

- `Board` 는 `Category`, `Post` 를 aggregate root 처럼 관리합니다.
- `Post.category` 는 nullable 이므로 게시글은 카테고리 없이도 존재할 수 있습니다.
- `Board -> Category`, `Board -> Post` 관계는 `CascadeType.ALL` 과 `orphanRemoval = true` 로 관리됩니다.

```mermaid
erDiagram
    BOARD {
        UUID id PK
        VARCHAR name
        VARCHAR description
    }

    CATEGORY {
        UUID id PK
        UUID board_id FK
        VARCHAR name
        VARCHAR description
    }

    POST {
        UUID id PK
        UUID board_id FK
        UUID category_id FK nullable
        VARCHAR title
        VARCHAR content
    }

    BOARD ||--o{ CATEGORY : contains
    BOARD ||--o{ POST : contains
    CATEGORY o|--o{ POST : classifies
```
