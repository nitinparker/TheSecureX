# 🛡️ TheSecureX: Enterprise Digital Forensics & Security Platform

> **A Polyglot, Distributed Trust Architecture for Secure Evidence Management**

TheSecureX is a state-of-the-art security platform designed for **Digital Forensics**, **Network Analysis**, and **Evidence Chain of Custody**. It leverages a microservices architecture to ensure scalability, security, and interoperability across different technology stacks.

---

## 🏗️ System Architecture

TheSecureX operates on a **Distributed Trust Model**, using a central Identity Provider (IdP) to secure access to specialized forensic tools.

### 🧩 The "Massive" Ecosystem

| Service | Technology | Role | Port |
|---------|------------|------|------|
| **Portal Gateway** | Java (Spring Boot) | **Central IdP**, RBAC, Evidence Vault Manager | `8081` |
| **AI Engine** | Python (FastAPI) | **Forensic Analysis**, Pattern Recognition, Hashing | `8000` |
| **TheNetProtectX** | Go (Golang) | **Network Sniffing**, PCAP Capture, High Performance | `8002` |
| **Evidence Vault** | Shared Volume | **Chain of Custody** Storage (Write-Once Read-Many) | `N/A` |
| **Database** | PostgreSQL 15 | Centralized Metadata & User Store | `5432` |

### 🔄 The Workflow: Chain of Custody

1.  **Capture**: The **Go Network Tool** detects an anomaly and writes `attack.pcap` to the secure **Evidence Vault** (`/data/vault`).
2.  **Log**: The **Java Portal** logs the file creation, timestamps it, and assigns a **Chain of Custody ID**.
3.  **Analyze**: The **Python AI Engine** reads the *same* file from the Vault, calculates a **SHA-256 Hash** to verify integrity, and performs deep analysis.
4.  **Report**: Results are sent back to the Portal for the **Master Dashboard**.

---

## 🚀 Getting Started

### Prerequisites

*   **Docker Desktop** (Required for the full stack experience)
*   **Git**

### 📦 Installation & Launch

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/your-repo/TheSecureX.git
    cd TheSecureX
    ```

2.  **Ignite the Platform**
    Run the unified Docker Compose command to build and start all services:
    ```bash
    docker compose up --build
    ```
    *(Note: If using older Docker versions, try `docker-compose up --build`)*
    *This will compile the Java, Go, and Python services, create the database, and link the mesh network.*

3.  **Fallback: Local Development (No Docker)**
    If Docker is not available, you can run services individually:
    *   **Portal**: `mvn spring-boot:run` (Port 8081)
    *   **AI Engine**: `python -m uvicorn main:app --port 8000` (Port 8000)
    *   **Network Tools**: `go run main.go` (Port 8002)
    *   *Ensure `evidence-vault/` directory exists in the project root.*

3.  **Access the Portal**
    *   Navigate to: [http://localhost:8081](http://localhost:8081)
    *   **Master Admin Credentials**:
        *   User: `admin`
        *   Pass: `admin123`

---

## 🛠️ Usage Guide

### 1. 🔐 Master Dashboard
*   Login as `admin`.
*   Generate **Invite Codes** for your forensic team.
*   Monitor system health and active users.

### 2. 🕵️ Agent Launchpad
*   Users login with their credentials.
*   Access the **Launchpad** to see authorized tools.
*   **Single Sign-On (SSO)**: Clicking a tool (e.g., *TheLogX*) generates a **One-Time Token**.
*   The tool validates and **burns** the token instantly to prevent Replay Attacks.

### 3. 📡 Network Forensics (Go)
*   Access via Launchpad -> *TheNetProtectX*.
*   Click **"Simulate Attack Capture"**.
*   This triggers the sniffer to write a PCAP file to the shared **Evidence Vault**.

### 4. 🧠 AI Analysis (Python)
*   Access via Launchpad -> *TheLogX*.
*   The AI Engine has direct access to the **Evidence Vault**.
*   Upload forensic artifacts or analyze captured PCAP files.
*   SHA-256 hashes are automatically calculated and sent to the Portal.

---

## 📂 Project Structure

```
TheSecureX/
├── portal-gateway/       # Java Spring Boot (IdP & Core Logic)
│   ├── Dockerfile        # Multi-stage Maven build
│   └── src/              # Entities: User, Evidence, Tool
├── ai-engine/            # Python FastAPI (Forensics)
│   ├── Dockerfile        # Python 3.9 + Forensic Libs
│   └── main.py           # Analysis Logic
├── network-tools/        # Go (Network Sniffer)
│   ├── Dockerfile        # Multi-stage Go build
│   └── main.go           # Packet Capture Logic
├── evidence-vault/       # Host Directory for Shared Volume
├── docker-compose.yml    # Orchestration & Network Definition
└── README.md             # This Manual
```

---

## 🛡️ Security Features

*   **One-Time Tokens (OTT)**: Links expire immediately after use.
*   **Role-Based Access Control (RBAC)**: Only authorized users can see specific tools.
*   **Isolated Mesh Network**: Containers communicate internally; only necessary ports are exposed.
*   **Evidence Integrity**: SHA-256 hashing ensures files are tampered-proof.

---

> *"Security is not a product, but a process."* — TheSecureX Team
