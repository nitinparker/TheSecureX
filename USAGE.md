# 🔐 Access Credentials & Usage Guide

## 1. Portal Gateway (Java Spring Boot)
The central command center for TheSecureX.

- **URL**: [http://localhost:8081](http://localhost:8081)
- **Login Page**: [http://localhost:8081/login](http://localhost:8081/login)

### 🔑 Default OEM (Super Admin) Credentials
Use these credentials to access the full system:
- **Username**: `admin`
- **Password**: `admin123`

### 🛠️ Role Capabilities
- **OEM (Admin)**: Can generate Invite Codes and manage the entire platform.
- **MASTER**: Can create Access Groups for their organization.
- **USER**: Investigator with access to specific tools (AI, Network, etc.).

---

## 2. AI Engine (Python FastAPI)
The backend intelligence service.
- **URL**: [http://localhost:8000](http://localhost:8000)
- **Docs**: [http://localhost:8000/docs](http://localhost:8000/docs)

## 3. Network Tools (Go)
Running in background. Check terminal logs for packet capture data.
