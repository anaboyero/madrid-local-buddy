# Madrid Local Buddy

[![Java Version](https://img.shields.io/badge/Java-17%20%2F%2021-orange.svg)](https://www.oracle.com/java/)
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

## 📂 Project Structure

```text
├── docs/             # Documentation and feedback logs
├── specs/            # Functional specifications and requirements
├── src/              # Source code of the application
├── agents.md         # Custom AI agent definitions for workspace development
├── pom.xml           # Maven project configuration
└── README.md         # Project overview