# 📨 Unified Messaging Platform (UMP)

## 📌 프로젝트 개요
`Unified Messaging Platform (UMP)`은 다양한 메시징 채널(이메일, SMS, 푸시 알림, 카카오 알림톡 등)을 통합하여 관리하는 **대규모 메시징 시스템**입니다.  
Kafka, Redis, ActiveMQ를 활용하여 **대량의 트래픽을 효율적으로 처리**하며, AWS 환경에서 Docker 기반으로 배포 및 운영됩니다.

---

## 🚀 기술 스택
| 기술                              | 역할               |
|---------------------------------|------------------|
| **Spring Boot**                 | 백엔드 개발           |
| **Redis**                       | 메시지 캐싱 및 중복 방지   |
| **Kafka**                       | 대량 메시지 큐 처리      |
| **ActiveMQ**                    | 실시간 메시징 처리       |
| **MySQL**                 | 메시지 이력 저장        |
| **REST API**                    | 메시지 전송 인터페이스 제공  |
| **Docker**                      | 컨테이너화 및 배포       |
| **AWS**                         | 클라우드 환경에서 서비스 운영 |
| **Prometheus + Grafana**        | 모니터링 및 성능 개선     |
| **API Gateway**                 | 클라이언트 요청을 중앙에서 처리 |
| **Spring Boot 기반 Message Service** | 메시지 요청을 받아 큐에 저장 |
---

## 🛠 핵심 기능
### ✅ 1) **메시징 통합 API 제공 (REST API)**
- 다양한 메시지 유형(이메일, SMS, 푸시, 알림톡)을 한 API로 요청할 수 있도록 설계
- 비동기 처리를 통해 빠른 응답을 보장
- API 호출 시 메시지 유형별 라우팅 수행 (SMS → SMS 전송 모듈, 이메일 → 이메일 전송 모듈 등)

### ✅ 2) **메시지 큐 기반 비동기 처리 (Kafka, ActiveMQ)**
- API에서 들어오는 메시지를 **Kafka**를 통해 대량으로 처리
- **ActiveMQ**를 활용하여 지연 시간이 짧은 메시지는 빠르게 처리할 수 있도록 구성

### ✅ 3) **Redis 기반 메시지 캐싱 및 중복 방지**
- 동일한 메시지가 중복 발송되지 않도록 **Redis**에 최근 발송된 메시지 캐싱
- 메시지 요청을 일정 시간 동안 캐싱하여 빠른 응답 보장

### ✅ 4) **사용자별 발송 이력 및 모니터링**
- **MySQL 활용하여 메시지 발송 이력 저장**
- **관리자 대시보드에서 발송 상태 및 성공/실패 여부를 확인할 수 있도록 구현**

### ✅ 5) **성능 개선 및 모니터링**
- **Spring Boot 기반 백엔드의 성능을 최적화하여 대량 트래픽을 처리**
- **Prometheus + Grafana를 활용하여 서버 및 애플리케이션의 모니터링 및 성능 개선**

### ✅ 6) **AWS 환경에서의 배포 및 운영**
- **AWS의 ECS (Fargate) / EC2 / S3 / RDS를 활용하여 클라우드 환경에서 서비스 운영**
- **Docker를 이용한 컨테이너 기반 배포**

## 📐 시스템 아키텍처
```plaintext
[Client] 
    ↓ 
[API Gateway (Nginx / Spring Cloud Gateway)] 
    ↓ 
[Message Service (Spring Boot)]
    ├──> [Kafka] → [Message Processor] 
    │         ├──> [Redis] (메시지 캐싱 및 중복 방지)
    │         ├──> [ActiveMQ] (실시간 메시징 처리)
    │         └──> [MySQL] (메시지 이력 저장)
    ↓
[AWS (ECS, EC2, RDS)] - Docker 기반 배포