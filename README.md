<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0F2027,50:203A43,100:2C5364&height=220&section=header&text=Event-Driven%20Notification%20System&fontSize=38&fontColor=ffffff&animation=fadeIn&fontAlignY=35&desc=Java%20%7C%20Spring%20Boot%20%7C%20Apache%20Kafka&descAlignY=55&descSize=18" />

<a href="https://github.com/Om20An00/event-driven-notification-system">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=22&pause=1000&color=2C5364&center=true&vCenter=true&width=650&lines=Decoupled+%C2%B7+Fault-Tolerant+%C2%B7+Horizontally+Scalable;Kafka+Idempotent+Producer+%2B+Manual+Offset+Commits;Strategy+Pattern+%2B+Dead+Letter+Topic+Routing;Live+Metrics+via+Prometheus+%2B+Grafana" alt="Typing SVG" />
</a>

<br/>

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-KRaft%20Mode-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Manifests-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-Metrics-E6522C?style=for-the-badge&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-F46800?style=for-the-badge&logo=grafana&logoColor=white)

![GitHub repo size](https://img.shields.io/github/repo-size/Om20An00/event-driven-notification-system?style=flat-square&color=blueviolet)
![GitHub last commit](https://img.shields.io/github/last-commit/Om20An00/event-driven-notification-system?style=flat-square&color=blueviolet)
![GitHub stars](https://img.shields.io/github/stars/Om20An00/event-driven-notification-system?style=flat-square&color=gold)
![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)

</div>

---

## 📖 About This Project

A **production-shaped**, event-driven notification system that decouples request handling from message delivery using **Apache Kafka**. Built solo, end-to-end — from the Kafka reliability internals to a live Prometheus/Grafana observability stack scraping real runtime metrics.

Instead of an API blocking on an email/SMS/push provider, this system publishes an event and returns instantly — a consumer group handles delivery asynchronously, with retries, dead-lettering, and full observability baked in.

> 🧠 **Author's note:** This project was designed and built entirely by me — architecture, code, testing, containerization, and the monitoring integration — as a hands-on deep dive into distributed, event-driven systems.

---

## 🏗️ Architecture

```mermaid
flowchart LR
    A[Client / curl] -->|POST /api/notifications| B[REST Controller]
    B --> C[Kafka Producer<br/>acks=all, idempotent]
    C --> D[(Kafka Topic<br/>notification-events)]
    D --> E[Kafka Consumer Group<br/>manual offset commit]
    E -->|success| F[Strategy Factory]
    F --> G1[Email Service]
    F --> G2[SMS Service]
    F --> G3[Push Service]
    E -->|failure after retries| H[(Dead Letter Topic)]
    E -.metrics.-> I[Micrometer /actuator/prometheus]
    I --> J[Prometheus]
    J --> K[Grafana Dashboard]
```

---

## ✨ Features

| Category | What's Implemented |
|---|---|
| **Messaging** | Kafka producer with `acks=all` for idempotent, no-duplicate delivery |
| **Reliability** | Manual offset commits + automatic retry-then-dead-letter-topic (DLQ) routing |
| **Extensibility** | Strategy pattern (`NotificationService` interface + factory) — add new channels without touching consumer logic |
| **Channels** | Email, SMS, and Push notification dispatch |
| **Testing** | Integration tests using Spring Kafka's **embedded broker** — validates the full producer → consumer flow |
| **Containerization** | Dockerfile + Docker Compose running Kafka in **KRaft mode** (no Zookeeper) |
| **Scaling (prepared)** | Kubernetes manifests — Deployment, Service, and HPA — ready to apply to a cluster |
| **Observability** | Spring Boot Actuator + Micrometer exposing `/actuator/prometheus`, scraped live by a companion [Kubernetes-Monitoring-Dashboard](https://github.com/Om20An00/Kubernetes-Monitoring-Dashboard) project (Prometheus + Grafana) |

---

## 🛠️ Tech Stack

<div align="center">

![Java](https://skillicons.dev/icons?i=java)
![Spring](https://skillicons.dev/icons?i=spring)
![Kafka](https://skillicons.dev/icons?i=kafka)
![Docker](https://skillicons.dev/icons?i=docker)
![Kubernetes](https://skillicons.dev/icons?i=kubernetes)
![Maven](https://skillicons.dev/icons?i=maven)
![Grafana](https://skillicons.dev/icons?i=grafana)
![Prometheus](https://skillicons.dev/icons?i=prometheus)

</div>

---

## 🚀 Getting Started

### Prerequisites

- JDK 17
- Maven
- Docker Desktop
- (Optional) VS Code with the *Extension Pack for Java* + *Spring Boot Extension Pack*

### 1. Clone the repository

```bash
git clone https://github.com/Om20An00/event-driven-notification-system.git
cd event-driven-notification-system
```

### 2. Start Kafka

```bash
docker compose up -d kafka kafka-ui
```

Wait ~20–30 seconds for the health check to pass.

### 3. Run the application

```bash
mvn spring-boot:run
```

You should see `Started NotificationSystemApplication` in the logs.

### 4. Send a test notification

```bash
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{"type":"EMAIL","recipient":"user@example.com","subject":"Welcome!","message":"Hi there"}'
```

### 5. Inspect Kafka visually

Open [http://localhost:8081](http://localhost:8081) to see the `notification-events` topic in Kafka UI.

### 6. Stop everything

```bash
docker compose down
```

---

## 📊 Metrics & Monitoring (Optional but Recommended)

This project exposes Prometheus-compatible metrics out of the box at:

```
http://localhost:8080/actuator/prometheus
```

Pair it with the companion **[Kubernetes-Monitoring-Dashboard](https://github.com/Om20An00/Kubernetes-Monitoring-Dashboard)** repo (Prometheus + Grafana) to get live panels for:

- Notifications consumed / published
- JVM heap usage & live threads
- CPU usage
- GC pause time

---

## 📸 Screenshots

<div align="center">

| Prometheus Targets | Grafna Login |
|:---:|:---:|
| <img src="https://raw.githubusercontent.com/Om20An00/event-driven-notification-system/b2935186ad90581fc658b0885f52c8cd31e9b988/notification-system/Screenshot%20(557).png" width="400"/> | <img src="https://raw.githubusercontent.com/Om20An00/event-driven-notification-system/b2935186ad90581fc658b0885f52c8cd31e9b988/notification-system/Screenshot%20(558).png" width="400"/> |

| Dashboard | Actuator / Prometheus Endpoint |
|:---:|:---:|
| <img src="https://raw.githubusercontent.com/Om20An00/event-driven-notification-system/b2935186ad90581fc658b0885f52c8cd31e9b988/notification-system/Screenshot%20(559).png" width="400"/> | <img src="https://raw.githubusercontent.com/Om20An00/event-driven-notification-system/b2935186ad90581fc658b0885f52c8cd31e9b988/notification-system/Screenshot%20(561).png" width="400"/> |

| Prometheus Query Explorer | Grafana Dashboard — Metrics Panel |
|:---:|:---:|
| <img src="https://raw.githubusercontent.com/Om20An00/event-driven-notification-system/b2935186ad90581fc658b0885f52c8cd31e9b988/notification-system/Screenshot%20(562).png" width="400"/> | <img src="https://raw.githubusercontent.com/Om20An00/event-driven-notification-system/b2935186ad90581fc658b0885f52c8cd31e9b988/notification-system/Screenshot%20(563).png" width="400"/> |

| Grafana Dashboard — Full View |
|:---:|
| <img src="https://raw.githubusercontent.com/Om20An00/event-driven-notification-system/b2935186ad90581fc658b0885f52c8cd31e9b988/notification-system/Screenshot%20(564).png" width="820"/> |

</div>



---

## ☸️ Kubernetes (Manifests Included)

Manifests are provided under `k8s/` as a starting point for horizontal scaling — they are **not deployed by default**:

```bash
kubectl apply -f k8s/
```

Includes `Deployment`, `Service`, and `HorizontalPodAutoscaler` for scaling consumers via Kafka partition-based load distribution.

---

## 🔮 Roadmap / Future Enhancements

- [ ] Redis for producer-side idempotency dedup and caching
- [ ] Increase Kafka partitions for true consumer-group parallelism
- [ ] Live deployment to a Kubernetes cluster (minikube/kind → cloud)
- [ ] Webhook / Slack notification channel via the existing strategy pattern

---

## 🍴 Forking & Cloning

This repository is open for learning purposes. If you'd like to explore, run, or build on top of it:

```bash
# Clone directly
git clone https://github.com/Om20An00/event-driven-notification-system.git

# Or fork it via the GitHub UI (top-right "Fork" button) to make your own copy
```

If you fork this project or use it as a reference/base for your own work, a ⭐ star or a mention/credit back to this repo is appreciated but not required. Pull requests with genuine improvements are welcome — please open an issue first to discuss what you'd like to change.

---

## 👤 Author

**Om** — [@Om20An00](https://github.com/Om20An00)

This project, including its architecture, code, tests, and monitoring integration, was designed and built entirely by me as an independent, hands-on project.

<div align="center">

If this project helped you or you found it interesting, consider giving it a ⭐!

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:2C5364,50:203A43,100:0F2027&height=120&section=footer" />

</div>
