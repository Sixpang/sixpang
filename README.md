## 📌 프로젝트 소개

six-pang은 여러 업체의 물류를 관리하고, 허브 기반으로 배송을 처리하는
MSA(Microservice Architecture) 구조의 백엔드 시스템입니다.


* 허브 중심 물류 관리
* 주문 기반 재고 차감 및 복구
* 배송 경로 및 담당자 관리
* MSA 기반 확장 가능한 구조

---

## 🛠 기술 스택

### Backend

* Java 21
* Spring Boot
* Spring Cloud (Eureka, Gateway)

### Database

* PostgreSQL
* Redis (캐싱, 조회수 등)

### DevOps

* Docker
* GitHub

---

## 🏗 아키텍처

* API Gateway
* Eureka Server (Service Discovery)
* User Service
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

## 📂 프로젝트 구조 (예정)

```
six-pang/
 ┣ eureka-server/
 ┣ gateway/
 ┣ user-service/
 ┣ order-service/
 ┣ delivery-service/
 ┗ hub-service/
```

---

## 📌 향후 계획

* Eureka Server 구축
* Gateway 라우팅 구성
* 서비스별 도메인 분리
* Redis 캐싱 적용
* Docker 기반 배포

---