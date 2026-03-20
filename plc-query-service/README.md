# PLC Query Service

## Overview

The **PLC Query Service** is responsible for maintaining the **current state of PLC data** and providing **real-time updates** to clients.

It consumes PLC readings from Kafka, compares them with the current state stored in Redis, and pushes updates via WebSocket (STOMP) when values change.

This service represents the **query/read layer** of the platform.

---

## Responsibilities

- Consume `PlcReadingEvent` from Kafka
- Maintain latest value per PLC tag in Redis
- Detect value changes
- Publish real-time updates via WebSocket

---

## Data Flow

Kafka → Query Service → Redis → WebSocket → Clients

### Processing Steps

1. Receive event from Kafka
2. Load current value from Redis
3. Compare values
4. If changed:
   - update Redis
   - send WebSocket message

---

## Kafka Integration

The service listens to PLC readings:

```java
@KafkaListener(topics = Topics.PLC_RAW_READINGS)
public void handle(PlcReadingEvent event)
```

---

## Redis Usage

Redis is used as a **current state store**:

- Key: `source:tagName`
- Value: `PlcReadingEvent`

Only the **latest value** is stored (no history).

---

## WebSocket (STOMP)

Real-time updates are sent via:

- Endpoint: `/ws`
- Topic: `/topic/plc-updates`

Example:

```java
messagingTemplate.convertAndSend("/topic/plc-updates", event);
```

---

## Domain Model

Currently, a single model is used:

```java
PlcReadingEvent
```

Used across:

- Kafka messages
- Redis storage
- WebSocket payload

---

## Configuration

### Kafka

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: plc-query-service
      auto-offset-reset: earliest
```

---

### Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

---

## Running the Service

### Prerequisites

- Kafka running
- Redis running

### Start

```bash
mvn spring-boot:run
```

---

## Design Notes

- Event-driven architecture (Kafka as backbone)
- Redis used for fast lookup of current values
- WebSocket avoids polling and enables real-time UI updates
- Stateless service (state externalized in Redis)

---

## Author

Harald Hiergeist
