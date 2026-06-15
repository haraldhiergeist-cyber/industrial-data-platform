# Industrial Data Platform Showcase

Cloud-native Industrial IoT platform demonstrating modern industrial data acquisition, event-driven architecture, Kubernetes operations, GitOps deployment, security, and observability.

The project showcases the complete technology stack from infrastructure to business application and serves as a reference implementation for modern industrial software platforms.

# Industrial Data Platform Showcase

Cloud-native Industrial IoT platform demonstrating modern industrial data acquisition, event-driven architecture, Kubernetes operations, GitOps deployment, security, and observability.

The project showcases the complete technology stack from infrastructure to business application and serves as a reference implementation for modern industrial software platforms.

---

## Screenshots

### Industrial Dashboard

![Dashboard](docs/screenshots/dashboard.png)

### Historical Analysis

![History](docs/screenshots/history.png)

### Kubernetes Operations

![Rancher](docs/screenshots/rancher.png)

### Infrastructure Layer

![Harvester](docs/screenshots/harvester.png)

---

## Documentation

- [Overview](docs/01-overview.md)
- [Architecture](docs/02-architecture.md)
- [Platform Stack](docs/03-platform-stack.md)
- [Data Flow](docs/04-data-flow.md)
- [Deployment](docs/05-deployment.md)
- [Security](docs/06-security.md)
- [Observability](docs/07-observability.md)
- [Demo Script](docs/08-demo-script.md)

---

## Key Features

### Industrial Connectivity

- Siemens PLC integration using Apache PLC4X
- Industrial data acquisition
- Real-time process monitoring

### Event-Driven Architecture

- Apache Kafka as central event backbone
- Decoupled microservices
- Scalable data processing

### Real-Time Dashboards

- Angular frontend
- WebSocket/STOMP updates
- Live machine status visualization

### Historical Data Analysis

- PostgreSQL persistence
- Time-series visualization
- Historical trend analysis

### Security

- Keycloak
- OAuth2
- OpenID Connect
- JWT-based authentication

### Cloud-Native Operations

- Kubernetes (RKE2)
- Rancher
- GitOps with Fleet
- Harbor container registry

### Observability

- Prometheus
- Grafana
- Loki
- Spring Boot Actuator

---

## Platform Stack

```text
Business Application
        │
        ▼
Industrial Data Platform
        │
        ▼
Platform Services
(Kafka, Redis, PostgreSQL,
 Keycloak, Grafana, Loki)
        │
        ▼
Container Platform
(Rancher, RKE2 Kubernetes)
        │
        ▼
Infrastructure Layer
(Harvester, Virtual Machines,
 Networking, DNS, DHCP, TLS)
        │
        ▼
Hardware Layer
(Compute, Storage, Network)
```

The platform intentionally covers all layers required to build and operate a modern cloud-native industrial solution.

---

## High-Level Architecture

```text
PLC / Simulator
        │
        ▼
PLC Collector Service
(Spring Boot + Camel + PLC4X)
        │
        ▼
Apache Kafka
        │
 ┌──────┴───────────┐
 │                  │
 ▼                  ▼

PLC Persist      PLC Query
Service          Service

 │                  │
 ▼                  ▼

PostgreSQL       Redis
                    │
                    ▼

             WebSocket/STOMP
                    │
                    ▼

                Angular UI
```

---

## Technology Stack

### Application Layer

- Java 21
- Spring Boot
- Apache Camel
- Apache PLC4X
- Angular

### Platform Services

- Apache Kafka
- Redis
- PostgreSQL
- Keycloak

### Observability

- Prometheus
- Grafana
- Loki

### Container Platform

- Rancher
- RKE2 Kubernetes

### Infrastructure

- Harvester
- KVM Virtualization

### DevOps

- GitLab CI/CD
- Harbor
- Fleet GitOps


---

## Repository Structure

```text
industrial-data-platform

├── plc-collector-service
├── plc-query-service
├── plc-persist-service
├── plc-industrial-ui
├── shared-model
├── shared-camel
└── docs
```

---

## Purpose

This project demonstrates how a traditional industrial application can evolve into a modern cloud-native platform using:

- Event-driven architecture
- Platform engineering
- Kubernetes
- GitOps
- Security
- Observability
- Industrial protocol integration

The showcase intentionally combines software development, infrastructure engineering, DevOps, and cloud-native operations into a single end-to-end solution.

---

## License

This project is provided as a technical showcase and architecture reference implementation.
