# 📦 Java Connector Framework (Spring Boot & Docker)

A lightweight, production-grade, and highly extensible **Extract-Load (EL)** Connector Framework built with **Spring Boot 4.x**, **Java 21**, and **Docker**.

This framework is built around the **Open-Closed Principle**, allowing you to completely swap database sources (MySQL, PostgreSQL) and target file formats (CSV, JSON) using simple `application.yml` configurations—without altering any core orchestration, state tracking, or extraction code.

---

## 🚀 Key Architectural Features

* 🔌 **Plugin Architecture (Decoupled Design):** Leverages Spring Boot's built-in `ApplicationContext` as a dynamic **Connector Factory** to bind and initialize technology-specific beans on the fly.
* 📦 **Universal Data Carrier (`Record` Interface):** Keeps source and target components entirely decoupled by wrapping rows into generic, technology-agnostic containers.
* 💾 **Stateful Incremental Loads (Watermarking):** Avoids redundant database reads. It tracks the latest modified timestamp (`last_updated`) from your records and stores it securely in a local checkpoint file.
* 🔒 **Reliable Checkpoint Order:** Adheres to a strict **Extract $\rightarrow$ Write $\rightarrow$ Commit $\rightarrow$ Checkpoint** workflow, ensuring *at-least-once delivery* and preventing data loss.
* 🛡️ **Memory-Safe Streaming:** Uses JDBC streaming cursors (specifically setting `fetchSize` to `Integer.MIN_VALUE` for MySQL) to stream millions of rows row-by-row, keeping a flat JVM memory footprint and preventing `OutOfMemoryError` (OOM).

---

## 📁 Directory Structure

The project is structured according to clean, enterprise-grade Java packages, separating core abstractions from concrete drivers and configurations:

```text
com.example.demo.localconnector/
│
├── config/                          # Configuration, Orchestration & State Management
│    ├── ConnectorProperties.java    # Maps configuration keys from application.yml
│    ├── ConnectorJobRunner.java     # Orchestrates the ETL pipeline lifecycle
│    └── LocalCheckpointStore.java   # Manages saving/loading local watermarks
│
├── core/                            # Framework Core Interfaces (Universal Contracts)
│    ├── Record.java                 # Standardized row wrapper
│    ├── SourceConnector.java        # Interface for data readers
│    ├── TargetConnector.java        # Interface for data writers
│    └── MapRecord.java              # Backing map implementation of Record
│
└── connector/                       # Concrete Pluggable Connectors (Implementation)
     ├── MySqlConnector.java         # Native streaming reader for MySQL
     ├── PostgresConnector.java      # Native streaming reader for PostgreSQL
     ├── LocalCsvWriter.java         # Lands records into local CSV files
     └── LocalJsonWriter.java        # Lands records into local JSON files
```

## 🛠️ Local Environment Setup

This section guides you through configuring your local MySQL infrastructure inside Docker, setting up the initialization scripts, and configuring your Spring Boot properties to run the pipeline seamlessly.

### 1. Project Directory Verification
Ensure your project directories are organized as follows before spinning up the services:
```text
C:\Java_Learn\Springboot\Connector_FrameWork\demo\
├── docker-compose.yml
├── init-db/
│    └── mysql/
│         └── init_mysql.sql      # Your MySQL schema & seed data
├── target/
│    └── demo-0.0.1-SNAPSHOT.jar # Compiled Java program
└── src/main/resources/
     └── application.yml          # Spring Boot properties

```
## 🛠️ Start the container

```shell
docker-compose up -d
```

## To insert additional testing data directly via the CLI, connect with:

```shell
docker exec -it demo-mysql-1 mysql -u user -p localdb
# Password is: password
```

## Running Directly from IntelliJ IDEA

run DemoApplication.java 

## Expected output for json target
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 1200.00,
    "last_updated": "2026-10-07 12:30:37"
  }
]

```

## Document

```url
https://amedeloitte.sharepoint.com/:u:/r/sites/FedExTransformationTeam/Shared%20Documents/General/Java%20Backend/java-connector-frameworks.html?d=wd1cc1cd374a44249bdcdf9c32848dc64&csf=1&web=1
```