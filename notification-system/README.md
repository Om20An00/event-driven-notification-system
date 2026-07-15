# Event-Driven Notification System

A multi-channel (Email / SMS / Push) notification service built with **Java 17, Spring Boot, and Apache Kafka**. A REST API accepts a notification request, publishes it as an event to Kafka, and one or more consumers pick it up and dispatch it on the correct channel — fully decoupling the caller from delivery.

## Architecture

```
Client
  │  POST /api/notifications
  ▼
NotificationController ──▶ NotificationProducerService ──▶ Kafka topic: notification-events
                                                                     │
                                                    (consumer group: notification-consumer-group)
                                                                     ▼
                                                          NotificationConsumer
                                                                     │
                                                     NotificationServiceFactory (strategy pattern)
                                                          ┌──────────┼──────────┐
                                                          ▼          ▼          ▼
                                                       Email       SMS        Push
                                                      Service    Service    Service
```

Key design choices:
- **Producer/consumer decoupling** — the API returns `202 Accepted` the moment the event is queued; actual delivery happens asynchronously, so a slow/down email provider never blocks the caller.
- **Keyed partitioning** — events are keyed by `recipient`, so all notifications for one user land on the same partition and process in order.
- **Strategy pattern** for channels — `NotificationService` interface + `NotificationServiceFactory` means adding a new channel (Slack, WhatsApp) is a new class, not a rewrite.
- **Manual offset commits** — an offset is only committed after a notification is actually dispatched; failures are retried (2x) and then routed to a `notification-events.DLT` dead-letter topic instead of blocking the partition.
- **Idempotent producer** (`acks=all`, `enable.idempotence=true`) so retried sends never duplicate a message on the broker.
- **`eventId` (UUID) on every event** — not used yet, but it's the hook a Redis-backed dedup check would use on the consumer side (see below).

## Tech stack
Java 17 · Spring Boot 3 · Spring Kafka · Maven · Docker / Docker Compose · (Kubernetes manifests included for future deployment)

## Running locally

```bash
# 1. Start Kafka (KRaft mode, no Zookeeper needed) + Kafka UI
docker compose up -d kafka kafka-ui

# 2. Run the app
mvn spring-boot:run

# or build + run everything in containers
docker compose up --build
```

Kafka UI is available at http://localhost:8081 to inspect topics/messages.

## Try it

```bash
curl -X POST http://localhost:8082/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
        "type": "EMAIL",
        "recipient": "user@example.com",
        "subject": "Welcome!",
        "message": "Thanks for signing up.",
        "metadata": {"source": "signup-flow"}
      }'
```

Response:
```json
{ "eventId": "b3f1...", "status": "QUEUED" }
```

Check the app logs (or `docker logs notification-app`) to see the consumer pick it up and dispatch it, e.g.:
```
[EMAIL] to=user@example.com subject='Welcome!' body='Thanks for signing up.' eventId=b3f1...
```

## Running tests
```bash
mvn test
```
Uses Spring Kafka's embedded broker (`@EmbeddedKafka`) so the producer → Kafka → consumer path is exercised without a real cluster.

## Future scaling extensions (not yet implemented — left as clear next steps)

- **Redis** — add `spring-boot-starter-data-redis` and, in `NotificationConsumer`, check `SETNX eventId` before calling `send()` to make delivery idempotent even under Kafka's at-least-once redelivery. Also useful for per-recipient rate limiting and caching delivery status.
- **Kubernetes** — `k8s/deployment.yaml`, `k8s/service.yaml`, `k8s/hpa.yaml` are included as a starting point. Because consumers in the same group split partitions automatically, scaling `replicas` (or letting the HPA do it) parallelizes processing with no code changes — just remember to also increase the topic's partition count so there's enough parallelism to scale into.
- **More partitions** — the topic currently defaults to Kafka's auto-created partition count; for real throughput, create it explicitly with more partitions (e.g. `kafka-topics.sh --create --topic notification-events --partitions 6`).
- **Schema registry / Avro** — swap the JSON serializer for Avro + Confluent Schema Registry if the event schema needs to evolve safely across teams.
- **Real providers** — each `*NotificationService.send()` currently logs instead of calling a real provider; swap in SES/SendGrid (email), Twilio/SNS (SMS), or FCM/APNs (push).

## Connecting to a separate Prometheus/Grafana monitoring project

This app now exposes real Prometheus metrics at `/actuator/prometheus`. To let an existing Prometheus instance (e.g. from a separate monitoring project) scrape it:

- When running via `docker compose up`, the app is reachable at host port **8082** (mapped from container port 8080) — changed from 8080 specifically to avoid clashing with another project's app on the same port.
- Add a scrape job pointing Prometheus at `host.docker.internal:8082` (works on Docker Desktop for Windows/Mac without needing to share a Docker network):
  ```yaml
  - job_name: "notification-system"
    metrics_path: /actuator/prometheus
    static_configs:
      - targets: ["host.docker.internal:8082"]
  ```
- Restart both `docker compose` stacks, then check `http://localhost:9090/targets` (Prometheus UI) — `notification-system` should show as `UP`.

## Notes on using this for your resume

This is a genuine, runnable project — every piece described above (Kafka producer/consumer, manual acks, DLQ routing, strategy pattern, embedded-Kafka test) is actually implemented in the code, not just described. A couple of honest framing tips:

- Describe what's built (event-driven architecture, Kafka producer/consumer, error handling with DLQ, strategy pattern for extensibility) rather than claiming production traffic numbers or "handles X requests/sec" — you haven't load-tested it, so don't invent a figure.
- It's fair to say Redis/Kubernetes are "designed for, with manifests included" — don't claim they're deployed/running unless you actually stand them up.
- If you do run the embedded-Kafka test and it passes, "includes an integration test using Spring Kafka's embedded broker" is a true, resume-worthy detail that also signals testing discipline.
