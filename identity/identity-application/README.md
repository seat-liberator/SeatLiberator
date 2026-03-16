# Identity service

Identity service는 두 개의 애플리케이션을 갖습니다.

| 애플리케이션 명 | 설명                    |
|----------|-----------------------|
| Identity | 사용자 인증, 생성, 토큰 발급 처리  |
| JWKS     | 토큰 검증 시 사용할 공개키 목록 반환 |

## Identity application

### 시작 설정

애플리케이션 실행을 위한 주요 속성 값은 다음과 같습니다.

| 속성                                                    | 설명                         |
|-------------------------------------------------------|----------------------------|
| `spring.datasource.*`                                 | PostgreSQL 연결 정보           |
| `spring.jpa.hibernate.ddl-auto`                       | JPA 스키마 생성 전략              |
| `spring.security.oauth2.client.registration.github.*` | GitHub OAuth2 로그인 클라이언트 설정 |
| `spring.security.oauth2.client.registration.google.*` | Google OIDC 로그인 클라이언트 설정   |
| `identity.application.auth.credential.sign-in`        | credential 로그인 엔드포인트 설정    |
| `identity.application.auth.credential.sign-up`        | credential 회원가입 엔드포인트 설정   |
| `identity.application.security.*`                     | CORS 설정                    |
| `identity.jwt.expiration`                             | JWT Access Token 만료 시간     |

현재 `application.yml`에는 PostgreSQL 연결 정보, GitHub/Google OAuth2 Client 정보, 로깅 설정이 포함되어있습니다.

`identity.application.auth.credential.*` 속성은 명시하지 않으면 다음 기본값을 사용합니다.

| 속성                                             | 기본값                  |
|------------------------------------------------|----------------------|
| `identity.application.auth.credential.sign-in` | `POST /auth/sign-in` |
| `identity.application.auth.credential.sign-up` | `POST /auth/sign-up` |

`identity.jwt.expiration`은 명시하지 않으면 `15분`을 사용합니다.

### 인증 방식

Identity application은 credential 인증과 federated 인증을 함께 처리합니다.

| 인증 방식      | 설명                              |
|------------|---------------------------------|
| Credential | 이메일/비밀번호 기반 회원가입, 로그인           |
| Federated  | 외부 인증 제공자 로그인 이후 사용자 인증 및 토큰 발급 |

비밀번호는 `BCryptPasswordEncoder`로 해싱합니다.

사용자 도메인은 `User`를 기준으로 credential 계정은 `1:1`, federated 계정은 `1:N` 관계를 가집니다.

### 구성

Identity application의 주요 구성은 다음과 같습니다.

| 구성                  | 설명                                                    |
|---------------------|-------------------------------------------------------|
| Application service | 사용자 등록, 계정 인증, 계정 존재 여부 확인 유스케이스 처리                   |
| Domain              | `User`, `CredentialAccount`, `FederatedAccount` 모델 정의 |
| Persistence         | JPA 기반 사용자/계정 저장, 조회 처리                               |
| Security            | credential/federated 인증용 Security Filter Chain 구성     |
| Token issuing       | 인증 성공 이후 Access Token, Refresh Token 발급               |

### Endpoint

**Credential 회원가입**

`POST /auth/sign-up`

```json
{
  "nickname": "tester",
  "email": "tester@example.com",
  "password": "password"
}
```

**Credential 로그인**

`POST /auth/sign-in`

```json
{
  "email": "tester@example.com",
  "password": "password"
}
```

인증이 성공하면 다음 형식으로 토큰을 반환합니다.

```json
{
  "accessToken": "<jwt access token>",
  "refreshToken": "<opaque refresh token>"
}
```

인증이 실패하면 `401 Unauthorized`와 함께 실패 메시지를 반환합니다.

### Federated authentication

federated 인증 체인은 `/oauth2/**`, `/login/**` 요청을 처리합니다.

현재 소스에는 다음 외부 인증 제공자 설정이 포함되어있습니다.

| 제공자      | 설명                                                  |
|----------|-----------------------------------------------------|
| `github` | OAuth2 Client registration 및 principal mapper 구현 포함 |
| `google` | OIDC Client registration 및 principal mapper 구현 포함   |

federated 인증 성공 시에는 provider 계정 존재 여부를 먼저 확인합니다.

- 계정이 없으면 사용자와 federated 계정을 먼저 생성합니다.
- 계정이 있으면 바로 인증을 진행합니다.
- 인증이 완료되면 credential 인증과 동일하게 Access Token, Refresh Token을 발급합니다.

provider에서 전달된 principal은 내부 `FederatedPrincipal`로 정규화해 사용합니다.

현재 Security Filter Chain에는 `OidcUserService`가 연결되어있고, `CustomOAuth2UserService` 구현도 별도로 포함되어있습니다.

### 토큰 발급

토큰 발급에는 두 개의 `TokenProvider` 구현을 사용합니다.

| 구현체                   | 설명                             |
|-----------------------|--------------------------------|
| `JwtProvider`         | RSA 키로 서명한 JWT Access Token 발급 |
| `OpaqueTokenProvider` | 랜덤 바이트 기반 Refresh Token 발급     |

JWT Access Token의 subject에는 인증된 사용자 ID가 들어갑니다.

## JWKS application

### 시작 설정

애플리케이션 실행을 위한 속성 값이 `JWKSProperties`에 정의되어있습니다.

| 속성            | 설명                         |
|---------------|----------------------------|
| `signableKid` | 토큰 서명에 사용할 키 식별자           |
| `keys`        | 애플리케이션에서 사용할 수 있는 키 엔트리 목록 |

각 키 엔트리는 다음 속성을 가집니다.

| 속성           | 설명                                |
|--------------|-----------------------------------|
| `kid`        | 키 식별자                             |
| `privateKey` | 해당 식별자 `KeyEntry`의 비밀 키 PEM 파일 경로 |
| `publicKey`  | 해당 식별자 `KeyEntry`의 공개 키 PEM 파일 경로 |

`keys` 설정은 토큰 발급 시 서명, 공개 키 목록 반환 등에서 재사용되기 때문에 공개 키 및 비밀 키 경로를 모두 포함합니다.(JWKS 응답에는 공개 키만 노출합니다.)

키 로테이션을 위한 헬퍼 스크립트(`keyGen.sh`)는 `resources` 디렉토리에 위치합니다.

`keyGen.sh` 사용법

```shell
sh keyGen.sh [<custom kid>]
```

**사용 예시**

```shell
sh keyGen.sh key-20260309-120000
```

**예시 생성 결과**

```text
key-20260309-120000/
  private.pem
  public.pem
```

`custom kid`를 전달하면 해당 `kid` 이름을 사용하는 디렉토리 내부에 공개 키, 비밀 키가 생성됩니다.

`custom kid` 인자를 넘기지 않고 스크립트를 호출하면 `key-%Y%m%d-%H%M%S` 형식의 디렉토리가 생성됩니다.

### Endpoint

**JWKS 조회**

`GET /.well-known/jwks.json`

```json
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "abc123",
      "alg": "RS256",
      "use": "sig",
      "n": "...",
      "e": "AQAB"
    },
    {
      "kty": "RSA",
      "kid": "123abc",
      "alg": "RS256",
      "use": "sig",
      "n": "...",
      "e": "AQAB"
    }
  ]
}
```
