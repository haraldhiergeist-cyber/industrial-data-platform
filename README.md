# Industrial Data Platform

Cloud-native platform for industrial PLC data acquisition, processing
and visualization.

The platform collects machine data from PLC systems, processes the data
in real time, stores historical data, and provides APIs for industrial
applications and user interfaces.

## Overview

Industrial systems often require two different types of data handling:

-   **Live state information** (current machine status)
-   **Historical data** (events, measurements, and production history)

This platform separates both concerns and processes them through
different layers.

PLC data is collected via **Apache PLC4X**, processed using **Apache
Camel**, streamed through **Kafka**, and stored or cached depending on
the use case.

## Architecture

PLC / Simulator │ ▼ PLC Collector Service (Spring Boot + Camel + PLC4X)
│ ├──────────────► Redis (current machine state) │ ▼ Kafka Event
Stream │ ▼ Historian Service │ ▼ Postgres Database │ ▼ REST APIs │ ▼
Angular UI

## Core Components

### PLC Collector Service

Reads data from PLC systems and transforms raw signals into structured
events.

Responsibilities:

-   connect to PLC using PLC4X
-   read machine variables
-   detect state changes
-   publish events to Kafka
-   update live machine state in Infinispan

### Historian Service

Consumes events from Kafka and stores them in a relational database.

Responsibilities:

-   consume event streams
-   transform event payloads
-   persist historical data in Postgres

### State API Service

Provides access to the **current machine state** stored in Infinispan.

Responsibilities:

-   expose REST endpoints for live machine data
-   provide fast access for UI dashboards

### History API Service

Provides access to **historical machine data** stored in Postgres.

Responsibilities:

-   query historical events
-   provide time-range queries
-   supply data for reporting and visualization

### Angular UI

Industrial dashboard for visualizing machine states and historical data.

Typical features:

-   machine overview
-   live machine status
-   alarm visualization
-   production history

## Technology Stack

Backend

-   Java 21
-   Spring Boot
-   Apache Camel
-   Apache PLC4X

Data & Messaging

-   Apache Kafka
-   Redis
-   PostgreSQL

Frontend

-   Angular

Platform

-   Docker
-   Kubernetes
-   Rancher
-   GitOps (Fleet)

## Repository Structure

industrial-data-platform │ ├── plc-collector-service ├──
historian-writer-service ├── state-api-service ├── history-api-service
├── shared-model ├── shared-camel └── angular-ui

## Development Roadmap

Phase 1\
PLC simulator integration and data collection.

Phase 2\
Kafka event streaming and historical persistence.

Phase 3\
State and history APIs.

Phase 4\
Angular dashboard.

Phase 5\
Deployment on Kubernetes using GitOps.

## Purpose of this Project

This project demonstrates how a traditional industrial application can
evolve from a monolithic architecture into a modern cloud-native
platform using event-driven microservices.

Key goals:

-   industrial protocol integration
-   event-driven architecture
-   scalable data processing
-   real-time state management
-   historical data analysis
-   cloud-native deployment

## License

This project is provided as a technical showcase and architecture
example.
