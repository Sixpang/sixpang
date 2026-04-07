# SIXPANG
## 팀원
| <img src="https://github.com/user-attachments/assets/09ef2083-5b28-41fe-9cb1-104256797ab5" width="110"> | <img src="https://img.shields.io/badge/Github-Link-181717?logo=Github" width="110"> | <img src="https://github.com/user-attachments/assets/9ad62a07-78a4-4115-aac6-728e5a3e1fb1" width="110"> | <img src="https://github.com/user-attachments/assets/ef1c1637-78cc-49e8-9a59-395c5f54da8c" width="110"> | <img src="https://github.com/user-attachments/assets/a6faa46c-007d-4cde-983e-6d09fe3c62f8" width="110"> |
| :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------: |
| 조여진 | 김민진 | 김혜린 | 곽찬홍 | 한소연 |
| 인증 / 인가<br>회원 | 허브 | 배송 | 업체<br>상품 | 주문 |
<br>

## 📌 프로젝트 소개

### six-pang은 여러 업체의 물류를 관리하고, 허브 기반으로 배송을 처리하는 MSA(Microservice Architecture) 구조의 백엔드 시스템입니다.

* 허브 중심 물류 관리
* 주문 기반 재고 차감 및 복구
* 배송 경로 및 담당자 관리
* MSA 기반 확장 가능한 구조

## 🛠 기술 스택

### Backend

* Java 17
* Spring Boot
* Spring Cloud (Eureka, Gateway)

### Database

* PostgreSQL
* Redis (캐싱, 조회수 등)

### DevOps

* Docker
* GitHub

| 분류          | 상세                                                    |
|---------------|---------------------------------------------------------|
| IDE           | IntelliJ IDEA                                           |
| Language      | Java17                                                  |
| Framework     | Spring Boot 3.5.11                                      |
| Build Tool    | Gradle                                                  |
| Spring Module | Spring Cloud                                            |
| DevOps        | Docker, Docker Compose                                  |
| DB            | PostgreSQL                                              |
| Security      | Spring Security, JWT (jjwt 0.12.7)                      |
| Cache         | Redis                                                   |
| Testing       | JUnit 5, Testcontainers                                 |
| Documentation | Swagger                                                 |

---

## 🏗 아키텍처

* API Gateway
* Eureka Server (Service Discovery)
* User Service
* Auth Service
* Order Service
* Delivery Service
* Hub Service
* Company Service

---

## 🌿 브랜치 전략

* `main` : 운영 브랜치
* `dev` : 개발 통합 브랜치
* `feat/*` : 기능 개발 브랜치

### 🔄 작업 흐름

1. `feat/*` 브랜치 생성
2. 기능 개발 및 커밋
3. `dev` 브랜치로 PR 생성
4. 코드 리뷰 및 승인 후 merge
5. `dev → main` PR 통해 배포

---

## 💬 커밋 컨벤션

* `feat` : 기능 추가
* `fix` : 버그 수정
* `chore` : 설정 및 환경 변경
* `refactor` : 코드 구조 개선

### 예시

```
feat: 회원가입 기능 추가
fix: 로그인 오류 수정
chore: gitignore 설정
```

---

## 👥 협업 규칙

* PR 필수 (직접 push 금지)
* 최소 1명 이상 승인 후 merge
* 코드 리뷰 코멘트 확인 후 반영

---

## 📂 프로젝트 구조

```text
📂 sixpang (Root)
 ┣ 📂 auth-service
 ┣ 📂 common-server
 ┃ ┗ 📂 src/main/java/org/sixpang/commonserver
 ┃ ┃ ┣ 📂 config
 ┃ ┃ ┃ ┣ 📄 JpaConfig.java
 ┃ ┃ ┃ ┣ 📄 RedisConfig.java
 ┃ ┃ ┃ ┗ 📄 WebMvcConfig.java
 ┃ ┃ ┣ 📂 entity
 ┃ ┃ ┃ ┗ 📄 BaseEntity.java
 ┃ ┃ ┣ 📂 global
 ┃ ┃ ┃ ┣ 📄 CustomException.java
 ┃ ┃ ┃ ┣ 📄 GlobalErrorCode.java
 ┃ ┃ ┃ ┗ 📄 GlobalExceptionHandler.java
 ┃ ┃ ┣ 📂 response
 ┃ ┃ ┃ ┗ 📄 ApiResponse.java
 ┃ ┃ ┗ 📂 security
 ┣ 📂 company-service
 ┣ 📂 delivery-service
 ┣ 📂 eureka-server
 ┣ 📂 gateway-server
 ┣ 📂 hub-service
 ┃ ┗ 📂 src/main/java/org/sixpang/hubservice
 ┃ ┃ ┣ 📄 HubServiceApplication.java
 ┃ ┃ ┣ 📂 application
 ┃ ┃ ┃ ┣ 📂 dto
 ┃ ┃ ┃ ┃ ┣ 📄 HubRequestDto.java
 ┃ ┃ ┃ ┃ ┣ 📄 HubResponseDto.java
 ┃ ┃ ┃ ┃ ┣ 📄 OptimalRouteResponseDto.java
 ┃ ┃ ┃ ┃ ┗ 📄 PathResponse.java
 ┃ ┃ ┃ ┗ 📂 service
 ┃ ┃ ┃   ┣ 📄 HubService.java
 ┃ ┃ ┃   ┗ 📄 RouteService.java
 ┃ ┃ ┣ 📂 domain
 ┃ ┃ ┃ ┣ 📂 model
 ┃ ┃ ┃ ┃ ┣ 📂 entity
 ┃ ┃ ┃ ┃ ┃ ┣ 📄 Hub.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📄 Route.java
 ┃ ┃ ┃ ┃ ┗ 📂 enums
 ┃ ┃ ┃ ┗ 📂 repository
 ┃ ┃ ┃   ┣ 📄 HubRepository.java
 ┃ ┃ ┃   ┗ 📄 RouteRepository.java
 ┃ ┃ ┣ 📂 infrastructure
 ┃ ┃ ┃ ┣ 📂 repository
 ┃ ┃ ┃ ┃ ┣ 📄 HubJpaRepository.java
 ┃ ┃ ┃ ┃ ┣ 📄 HubRepositoryImpl.java
 ┃ ┃ ┃ ┃ ┣ 📄 RouteJpaRepository.java
 ┃ ┃ ┃ ┃ ┗ 📄 RouteRepositoryImpl.java
 ┃ ┃ ┃ ┗ 📄 NaverMapFeignClient.java
 ┃ ┃ ┗ 📂 presentation
 ┃ ┃ ┃ ┗ 📂 controller
 ┃ ┃ ┃   ┣ 📄 HubController.java
 ┃ ┃ ┃   ┗ 📄 RouteController.java
 ┃ ┗ 📂 resources
 ┃ ┃ ┗ 📄 application.yaml
 ┣ 📂 notification-service
 ┣ 📂 order-service
 ┣ 📂 prediction-service
 ┣ 📂 product-service
 ┣ 📄 build.gradle
 ┗ 📄 settings.gradle
```

---

## 실행 방법

### 1. 프로젝트 클론

```bash
git clone https://github.com/Sixpang/sixpang.git
cd sixpang
```
### 2. 환경 설정

**`application.yml` 또는 `.env` 파일에 아래 환경 변수를 설정하세요.**

```properties
# 🗄️ Database (PostgreSQL)
DB_NAME=hub
DB_URL=jdbc:postgresql://localhost:5432/sixpang
DB_USER=<POSTGRES_USER>
DB_PASSWORD=<POSTGRES_PASSWORD>

# 🗺️ NAVER Map API
NAVER_CLIENT_ID=<YOUR_NAVER_CLIENT_ID>
NAVER_CLIENT_SECRET=<YOUR_CLIENT_SECRET>
DIRECTIONS_URL=https://maps.apigw.ntruss.com

```

### 3. Docker로 PostgreSQL · Redis 실행
```bash
docker compose up -d
```
### 4. 애플리케이션 실행

```bash
./gradlew bootRun
```

---
