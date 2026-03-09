# Identity service

Identity service는 두 개의 애플리케이션을 갖습니다.

| 애플리케이션 명 | 설명                    |
|----------|-----------------------|
| Identity | 사용자 인증, 생성, 토큰 발급 처리  |
| JWKS     | 토큰 검증 시 사용할 공개키 목록 반환 | 

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

