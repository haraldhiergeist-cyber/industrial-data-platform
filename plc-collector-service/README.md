\# plc-collector-service



\## Overview

The plc-collector-service connects to a PLC (Programmable Logic Controller) using Apache PLC4X,

reads configured tags, and publishes the data to Kafka.



This service is the entry point of the data pipeline.



\## Architecture

PLC → PLC4X → Collector → Kafka



\## Features

\- PLC connectivity via Apache PLC4X (S7 protocol)

\- Configurable tags via `application.yml`

\- Periodic polling

\- Kafka producer integration

\- Automatic reconnect handling



\## Configuration



Example:



```yaml

plc:

&nbsp; connection-string: "s7://127.0.0.1?controller-type=S7\_300"

&nbsp; poll-interval-ms: 1000

&nbsp; source: plc-simulator-1



&nbsp; tags:

&nbsp;   - name: temperature

&nbsp;     address: "%DB90.DBW0:INT"

&nbsp;   - name: pressure

&nbsp;     address: "%DB90.DBW2:INT"

&nbsp;   - name: level

&nbsp;     address: "%DB90.DBW4:INT"



kafka:

&nbsp; bootstrap-servers: 10.10.100.20:9094

