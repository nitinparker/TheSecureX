# TheSecureX: Polyglot AI-Augmented Forensic & Security Orchestration 🛡️

TheSecureX is a massive, enterprise-grade ecosystem designed for automated incident response and digital forensics. It leverages a **Polyglot Microservices Architecture** to ensure high performance, type safety, and scalability.

## 🏛️ Architecture Overview

TheSecureX uses the best tool for each job:

| Component | Service Name | Language | Function |
| :--- | :--- | :--- | :--- |
| **Portal Gateway** | `portal-gateway` | **Java (Spring Boot)** | Central Authentication, RBAC, OEM/Master Management. |
| **AI Brain** | `ai-engine` | **Python (FastAPI)** | TheSecureAnalyzerX: Machine Learning & Evidence Correlation. |
| **Network Tools** | `network-tools` | **Go (Golang)** | TheNetProtectX: High-speed packet capture & filtering. |
| **Database** | `db` | **PostgreSQL** | Shared source of truth for identities and permissions. |
| **Message Broker** | `broker` | **RabbitMQ** | Asynchronous communication between services. |

## 📂 Project Structure

```plaintext
TheSecureX/
├── portal-gateway/            # [JAVA] Spring Boot Admin & Auth
│   ├── src/main/java/         # OEM & Master Logic
│   └── pom.xml                # Maven Dependencies
├── ai-engine/                 # [PYTHON] TheSecureAnalyzerX
│   ├── models/                # ML Models
│   └── main.py                # FastAPI Service
├── network-tools/             # [GO] TheNetProtectX
│   ├── sniffer.go             # High-speed packet capture
│   └── go.mod                 # Go Modules
├── infrastructure/            # [DOCKER] Orchestration
│   └── docker-compose.yml     # Launches all services
├── docs/                      # Documentation
└── portal-gateway-python-legacy/ # [ARCHIVED] Old Python Prototype
```

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local dev)
- Go 1.21+ (for local dev)
- Python 3.9+ (for local dev)

### One-Click Deployment
To start the entire ecosystem:

```bash
docker-compose up --build
```

### ☕ Portal Gateway (Java)
The entry point for all users.
- **URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html` (if enabled)
- **Features**:
  - OEM generates Invite Codes.
  - Masters create Access Groups.
  - Users login via JWT.

### 🐍 AI Engine (Python)
Performs deep learning analysis on evidence.
- **URL**: `http://localhost:8000`

### 🐹 Network Tools (Go)
Runs in the background to monitor traffic.
- **Logs**: Check container logs for captured packets.

## 🔐 Security & RBAC
The system implements a strict hierarchy:
1.  **OEM**: Super Admin, generates invite codes.
2.  **MASTER**: Tenant Admin, manages Access Groups.
3.  **USER**: Investigator, limited to assigned tools.

## ⚖️ Compliance
Designed for DFIR professionals. Ensure legal authorization before using network interception tools.
