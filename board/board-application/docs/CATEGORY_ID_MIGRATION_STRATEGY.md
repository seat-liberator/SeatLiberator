# category_id 단계 전환 전략

## 목표

- 운영 데이터가 있는 환경에서 `post.category_id`를 안전하게 필수(FK + NOT NULL)로 전환한다.
- 배포 실패를 피하기 위해 스키마/코드/데이터를 분리해서 단계적으로 반영한다.

## 현재 상태

- 엔티티 기준 `post.category_id`는 nullable 상태다.
- 게시글 생성 요청은 `categoryId`를 필수로 받고 있어 신규 데이터는 대부분 category를 가진다.
- 과거 데이터/수동 데이터에 `category_id IS NULL`이 있을 가능성을 전제로 전환한다.

## 전환 단계

1. **사전 점검**
    - `post` 기준 `category_id IS NULL` 건수 확인
    - `category_id`가 있지만 실제 `category`가 없는 orphan 건수 확인
    - `post.board_id`와 `category.board_id`가 다른 cross-board 연결 건수 확인

2. **백필 기준 카테고리 준비**
    - 게시판(`board_id`)별 기본 카테고리(예: `미분류`)를 생성한다.
    - 이미 존재하면 재사용한다.

3. **데이터 백필**
    - `post.category_id IS NULL` 인 데이터를 해당 `board_id`의 기본 카테고리로 업데이트한다.
    - orphan 데이터가 있다면 같은 기준으로 정리한다.
    - cross-board 연결(`p.board_id <> c.board_id`) 데이터도 해당 `post.board_id`의 기본 카테고리로 재지정한다.

4. **애플리케이션 보호 로직 적용**
    - 게시글 생성/수정에서 `category` 미지정 또는 타 게시판 category 지정을 차단한다.
    - 카테고리 삭제 시 연결된 게시글 처리 정책(삭제 차단 또는 재배치)을 명시한다.

5. **제약 강화**
    - `post.category_id`에 FK 제약을 명시적으로 추가/검증한다.
    - `post.category_id`를 `NOT NULL`로 전환한다.

6. **후속 검증**
    - `NULL`/orphan/cross-board 건수가 0인지 확인한다.
    - API 시나리오 테스트(E2E)에서 category 관련 회귀가 없는지 확인한다.

## 운영 SQL 체크리스트 (PostgreSQL)

```sql
-- 1) NULL 확인
SELECT COUNT(*) AS null_category_posts
FROM post
WHERE category_id IS NULL;

-- 2) orphan 확인 (category_id는 있는데 category 없음)
SELECT COUNT(*) AS orphan_category_posts
FROM post p
LEFT JOIN category c ON c.id = p.category_id
WHERE p.category_id IS NOT NULL
  AND c.id IS NULL;

-- 3) cross-board 확인 (category는 존재하지만 다른 board 소유)
SELECT COUNT(*) AS cross_board_category_posts
FROM post p
JOIN category c ON c.id = p.category_id
WHERE p.board_id <> c.board_id;
```

> 실제 백필/DDL SQL은 운영 DB 상태를 확인한 뒤 별도 스크립트로 확정한다.

## 롤백 원칙

- 단계 5(제약 강화) 전까지는 데이터 정리 작업만 수행하므로 롤백이 비교적 단순하다.
- 단계 5 이후 문제 발생 시 `NOT NULL`/FK를 완화하는 다운 스크립트를 사전에 준비한다.
- 배포는 반드시 "코드 반영 -> 데이터 정리 확인 -> 제약 강화" 순서를 지킨다.
