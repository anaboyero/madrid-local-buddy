# Madrid Local Buddy

[![Java Version](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Build Status](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)

**Madrid Local Buddy** is a language immersion management system designed to connect local hosts with foreign visitors in Madrid for natural, exclusive English conversation. The core concept leverages a non-monetary value exchange: the local host provides native knowledge of the city and curates low-cost, authentic cultural experiences, in exchange for a 100% English-speaking environment.

---

## 🚀 Key Features & Application Flow

The system acts as an operational orchestrator that manages the lifecycle of local encounters from the initial proposal to the post-activity feedback loop:

1. **Digital Route Inventory:** A categorized repository of local plans, filtered by cost (caps at €10/person) and expected duration. It ensures all activities stay clear of "tourist traps."
2. **Booking & Matchmaking Pipeline:** Processes incoming requests from international users, filtering candidates by profile matching and real-time availability.
3. **Logistics Automation:** Once a meetup is validated by the host, the system automatically dispatches precise meeting points, scheduling, and constraints.
4. **Feedback & Review Loop:** Implements a post-event review module to record user experiences, locking down historical logs to guarantee language immersion criteria were strictly met.

---

## 🛠️ Tech Stack

- **Backend:** Java (Maven-managed project structure)
- **Architecture:** Modular domain design focusing on clean code practices and strict validation pipelines.
- **Specifications:** Behavior/Feature driven definitions located inside the `/specs` directory.

---

## Running locally (JAR)

Run the API as a standalone executable JAR — no IDE required.

### Prerequisites

- **Java 17+**
- **Maven 3.x**

### Build

From the project root:

```bash
mvn package
```

All tests must pass. The executable artifact is written to:

```text
target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
```

### Run

**Profile A — local development (default):** no environment variables required. Host notification emails are written to the application log only (`EMAIL_SENDER_MODE=log`).

```bash
java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
```

The API listens on **http://localhost:8080** by default.

**Profile B — real email delivery:** set `EMAIL_SENDER_MODE=http` and the required Resend variables before starting. See [`specs/techstack.md`](specs/techstack.md) for the full list. Copy [`.env.example`](.env.example) to `.env` (ignored by git), fill in your values, and load them in your shell or IDE — never commit secrets.

### Environment variables

| Profile | Purpose | Variables |
|---------|---------|-----------|
| **A** (default) | Log-only notifications | None, or `EMAIL_SENDER_MODE=log` |
| **B** | Real email via Resend | `EMAIL_SENDER_MODE=http`, `HOST_NOTIFICATION_EMAIL`, `EMAIL_FROM`, `EMAIL_API_KEY` |

Optional: `EMAIL_API_URL` (Resend endpoint override). Details and defaults: [`specs/techstack.md`](specs/techstack.md).

### Optional port

```bash
# Linux / macOS
SERVER_PORT=9090 java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar

# Windows PowerShell
$env:SERVER_PORT=9090; java -jar target/madrid-local-buddy-0.0.1-SNAPSHOT.jar
```

### Quick check

With the JAR running (default port 8080):

```bash
curl http://localhost:8080/api/experiences
```

Expected: HTTP **200** and a JSON array with **2** experiences (including `"Cinema"`).

---

## 📂 Project Structure

```text
├── docs/             # Documentation and feedback logs
├── specs/            # Functional specifications and requirements
├── src/              # Source code of the application
├── agents.md         # Custom AI agent definitions for workspace development
├── pom.xml           # Maven project configuration
└── README.md         # Project overview