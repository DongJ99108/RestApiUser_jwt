# RestApiUser - JWT + Refresh Token 버전

Spring Boot 4 + JDK 25 + JPA + Oracle REST API 예제에 JWT 인증과 Refresh Token 재발급 기능을 추가한 버전입니다.

## 추가된 기능

- Spring Security 적용
- BCrypt 비밀번호 암호화
- Access Token 발급
- Refresh Token 발급/저장/검증/재발급/로그아웃 폐기
- Refresh Token DB 저장 시 원문이 아니라 SHA-256 해시 저장
- JWT `roles` claim 추가
- `/api/auth/login`, `/api/auth/refresh`, `/api/auth/logout`, `/api/auth/me` 추가
- `/api/users` 목록/수정/삭제 API는 Bearer Token 필요
- `index.html`에 로그인/토큰 재발급/로그아웃 테스트 UI 추가

## 기본 계정

| 아이디 | 비밀번호 | 권한 |
|---|---|---|
| admin | admin1234 | ADMIN |
| user1 | user1234 | USER |
| user2 | user1234 | USER |
| oracle | oracle1234 | USER |
| restapi | rest1234 | USER |

기존 DB에 평문 비밀번호로 저장된 기본 계정은 애플리케이션 시작 시 BCrypt 암호로 보정됩니다.

## 인증 흐름

```text
POST /api/auth/login
  { userid, passwd }
      ↓
AuthenticationManager
      ↓
CustomUserDetailsService가 TUSER 조회
      ↓
PasswordEncoder.matches()로 BCrypt 비밀번호 검증
      ↓
JwtService가 Access Token 발급
RefreshTokenService가 Refresh Token 발급 후 DB에는 해시만 저장
      ↓
응답: { tokenType, accessToken, refreshToken, expiresIn, user }
```

## API 테스트 순서

1. Oracle Docker Compose 실행
2. Spring Boot 애플리케이션 실행
3. `http/user-api.http`에서 로그인 요청 실행
4. 응답의 `accessToken`, `refreshToken` 값을 변수에 복사
5. `Authorization: Bearer {{accessToken}}` 헤더가 있는 API 실행
6. Access Token 만료 시 `/api/auth/refresh` 호출
7. 로그아웃 시 `/api/auth/logout` 호출

## 주요 API

```http
POST /api/users
```
회원가입. 토큰 없이 접근 가능합니다.

```http
POST /api/auth/login
```
로그인. Access Token과 Refresh Token을 발급합니다.

```http
GET /api/users
Authorization: Bearer <accessToken>
```
회원 목록 조회. Access Token이 필요합니다.

```http
POST /api/auth/refresh
```
Refresh Token으로 Access Token과 Refresh Token을 새로 발급합니다. 이 예제는 Refresh Token rotation 방식이라 기존 Refresh Token은 폐기됩니다.

```http
POST /api/auth/logout
```
Refresh Token을 폐기합니다.

## 설정 위치

`src/main/resources/application.yml`

```yaml
app:
  jwt:
    issuer: rest-api-user
    secret: change-this-secret-key-for-jwt-demo-please-use-env-value-32bytes
    access-token-minutes: 30
    refresh-token-days: 7
```

운영 환경에서는 `secret` 값을 소스에 직접 적지 말고 환경변수나 Secret Manager로 분리하는 것이 좋습니다.
# RestApiUser_jwt
