\# plc-persist-service



\## Overview

Kafka consumer service that persists PLC readings into PostgreSQL.



\## Architecture

PLC → Kafka → Camel → Postgres



\## Features

\- Kafka consumer (Apache Camel)

\- Idempotent persistence (topic/partition/offset)

\- Liquibase migrations

\- Retry with backoff

\- Database readiness handling



\## Tech Stack

\- Spring Boot

\- Apache Camel

\- Kafka

\- PostgreSQL

\- Liquibase

\- Lombok



\## Configuration

See `application.yml`



\## Run

```bash

mvn spring-boot:run

