# Crypto Streaming Platform

A Coding Challenge real-time cryptocurrency price streaming platform built with a microservices architecture. The system generates simulated cryptocurrency prices and streams them to connected clients through WebSocket connections.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
  - [System Context](#system-context)
  - [Container Diagram](#container-diagram)
  - [Component Diagram: Price Producer](#component-diagram-price-producer)
  - [Component Diagram: WebSocket Bridge](#component-diagram-websocket-bridge)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Services](#services)
- [Configuration](#configuration)

## Overview

The Crypto Streaming Platform is a demonstration project that showcases real-time data streaming using modern technologies. It simulates cryptocurrency price updates and delivers them to web clients through a distributed architecture using Redis Streams as the message backbone.

![Crypto Streaming dashboard](image.png)

## Architecture

### System Context

The following diagram shows the high-level system context, illustrating how users interact with the platform.

```mermaid
C4Context
    title System Context Diagram for Crypto Streaming Platform

    Person(user, "User", "A person who wants to monitor cryptocurrency prices in real-time")

    System(cryptoStreaming, "Crypto Streaming Platform", "Provides real-time cryptocurrency price updates through a web dashboard")

    Rel(user, cryptoStreaming, "Views real-time crypto prices", "HTTPS/WebSocket")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

### Container Diagram

The container diagram shows the main building blocks of the system and how they communicate.

```mermaid
C4Container
    title Container Diagram for Crypto Streaming Platform

    Person(user, "User", "A person who wants to monitor cryptocurrency prices in real-time")

    Container_Boundary(platform, "Crypto Streaming Platform") {
        Container(dashboard, "Dashboard", "JavaScript, HTML, CSS, Nginx", "Web application that displays real-time cryptocurrency prices")
        
        Container(priceProducer, "Price Producer", "Java, Spring Boot", "Generates simulated cryptocurrency price updates and publishes them to Redis Streams")
        
        Container(websocketBridge, "WebSocket Bridge", "Java, Spring Boot", "Consumes price events from Redis and broadcasts them to connected WebSocket clients")
        
        ContainerDb(redis, "Redis", "Redis 8", "Message broker using Redis Streams for event distribution")
    }

    Rel(user, dashboard, "Views prices", "HTTPS")
    Rel(dashboard, websocketBridge, "Receives price updates", "WebSocket")
    Rel(priceProducer, redis, "Publishes price events", "Redis Streams")
    Rel(websocketBridge, redis, "Consumes price events", "Redis Streams")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

### Component Diagram: Price Producer

The Price Producer service generates simulated cryptocurrency prices using a random walk algorithm and publishes them to Redis Streams.

```mermaid
C4Component
    title Component Diagram for Price Producer

    ContainerDb(redis, "Redis", "Redis 8", "Message broker using Redis Streams")

    Container_Boundary(priceProducer, "Price Producer") {
        Component(scheduler, "Price Generator Scheduler", "Spring Scheduled Task", "Triggers price generation at configured intervals (default: 1 second)")
        
        Component(generator, "Price Generator", "Domain Service", "Generates new prices for all tracked cryptocurrencies")
        
        Component(strategy, "Random Walk Strategy", "Domain Service", "Implements price movement algorithm using random walk with configurable volatility")
        
        Component(publisher, "Redis Stream Publisher", "Adapter", "Publishes price events to Redis Streams using Lettuce client")
    }

    Rel(scheduler, generator, "Triggers price generation")
    Rel(generator, strategy, "Uses for price calculation")
    Rel(generator, publisher, "Publishes events via")
    Rel(publisher, redis, "XADD commands", "Redis Protocol")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

### Component Diagram: WebSocket Bridge

The WebSocket Bridge service acts as a bridge between Redis Streams and WebSocket clients, enabling real-time price delivery to the dashboard.

```mermaid
C4Component
    title Component Diagram for WebSocket Bridge

    ContainerDb(redis, "Redis", "Redis 8", "Message broker using Redis Streams")
    Container(dashboard, "Dashboard", "JavaScript SPA", "Web application displaying prices")

    Container_Boundary(bridge, "WebSocket Bridge") {
        Component(consumer, "Price Stream Consumer", "Spring Component", "Orchestrates consumption of Redis events and broadcasting to clients")
        
        Component(redisConsumer, "Redis Stream Consumer", "Adapter", "Reads events from Redis Streams using consumer groups")
        
        Component(broadcaster, "Message Broadcaster", "Domain Service", "Serializes and sends price updates to interested WebSocket sessions")
        
        Component(sessionRegistry, "Session Registry", "Domain Service", "Manages WebSocket sessions and tracks symbol subscriptions")
        
        Component(wsHandler, "WebSocket Handler", "Spring WebSocket", "Handles WebSocket connection lifecycle and incoming messages")
    }

    Rel(redisConsumer, redis, "XREADGROUP commands", "Redis Protocol")
    Rel(consumer, redisConsumer, "Subscribes to events")
    Rel(consumer, broadcaster, "Forwards events to")
    Rel(broadcaster, sessionRegistry, "Gets interested sessions from")
    Rel(wsHandler, sessionRegistry, "Registers/unregisters sessions")
    Rel(broadcaster, dashboard, "Sends price updates", "WebSocket")

    UpdateLayoutConfig($c4ShapeInRow="3", $c4BoundaryInRow="1")
```

### Deployment Diagram

The following diagram shows how the system is deployed using Docker containers.

```mermaid
C4Deployment
    title Deployment Diagram for Crypto Streaming Platform

    Deployment_Node(docker, "Docker Host", "Docker Compose") {
        Deployment_Node(network, "crypto-network", "Bridge Network") {
            
            Deployment_Node(dashboardNode, "crypto-dashboard", "Nginx Alpine") {
                Container(dashboardContainer, "Dashboard", "Static Files + Nginx", "Serves the web dashboard on port 3000")
            }
            
            Deployment_Node(producerNode, "crypto-price-producer", "Eclipse Temurin JDK 21") {
                Container(producerContainer, "Price Producer", "Spring Boot JAR", "Generates prices on port 8080")
            }
            
            Deployment_Node(bridgeNode, "crypto-websocket-bridge", "Eclipse Temurin JDK 21") {
                Container(bridgeContainer, "WebSocket Bridge", "Spring Boot JAR", "WebSocket server on port 8081")
            }
            
            Deployment_Node(redisNode, "crypto-redis", "Redis 8 Alpine") {
                ContainerDb(redisContainer, "Redis", "In-memory Data Store", "Streams on port 6379")
            }
        }
    }

    Rel(dashboardContainer, bridgeContainer, "WebSocket", "ws://localhost:8081")
    Rel(producerContainer, redisContainer, "Publishes", "Redis Protocol")
    Rel(bridgeContainer, redisContainer, "Consumes", "Redis Protocol")

    UpdateLayoutConfig($c4ShapeInRow="2", $c4BoundaryInRow="1")
```

## Technology Stack

| Component | Technology |
|-----------|------------|
| Price Producer | Java 17, Spring Boot 3, Lettuce (Redis client) |
| WebSocket Bridge | Java 17, Spring Boot 3, Spring WebSocket, Lettuce |
| Dashboard | Vanilla JavaScript, HTML5, CSS3, Nginx |
| Message Broker | Redis 8 with Streams |
| Containerization | Docker, Docker Compose |

## Getting Started

### Prerequisites

- Docker and Docker Compose installed on your system
- (Optional) Java 17 and Maven for local development

### Running the Platform

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd crypto-streaming
   ```

2. Start all services using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Access the dashboard:
   - Open your browser and navigate to `http://localhost:3000`

4. Monitor service health:
   - Price Producer: `http://localhost:8080/actuator/health`
   - WebSocket Bridge: `http://localhost:8081/actuator/health`

### Stopping the Platform

```bash
docker-compose down
```

To remove all data (including Redis volume):
```bash
docker-compose down -v
```

## Services

### Price Producer (Port 8080)

Generates simulated cryptocurrency prices at regular intervals and publishes them to a Redis Stream.

**Key Features:**
- Configurable price generation interval (default: 1 second)
- Random walk algorithm for realistic price movements
- Stream trimming to manage memory usage
- Health endpoint for monitoring

### WebSocket Bridge (Port 8081)

Bridges Redis Streams to WebSocket connections, enabling real-time price delivery to web clients.

**Key Features:**
- Consumer group based consumption for scalability
- Session management with symbol subscription support
- Heartbeat mechanism for connection health
- Configurable batch processing

### Dashboard (Port 3000)

A web-based dashboard that displays real-time cryptocurrency prices.

**Key Features:**
- Real-time price updates via WebSocket
- Automatic reconnection with exponential backoff
- Dynamic price card creation for new symbols
- Visual indicators for price trends (up/down)

## Configuration

### Environment Variables

| Variable | Service | Description | Default |
|----------|---------|-------------|---------|
| `REDIS_HOST` | Price Producer, WebSocket Bridge | Redis server hostname | `localhost` |
| `REDIS_PORT` | Price Producer, WebSocket Bridge | Redis server port | `6379` |
| `HOSTNAME` | WebSocket Bridge | Consumer ID for Redis consumer group | `consumer-1` |

### Application Configuration

#### Price Producer (`application.yml`)

```yaml
crypto:
  redis:
    stream:
      name: crypto:prices        # Redis stream name
      max-length: 10000          # Maximum stream length
  producer:
    interval-ms: 1000            # Price generation interval
```

#### WebSocket Bridge (`application.yml`)

```yaml
crypto:
  redis:
    stream:
      name: crypto:prices        # Redis stream name
      consumer-group: price-consumers
      block-timeout: 5s          # Blocking read timeout
      batch-size: 100            # Messages per batch
  websocket:
    endpoint: /ws/prices         # WebSocket endpoint path
    allowed-origins: "*"         # CORS configuration
    max-sessions: 1000           # Maximum concurrent sessions
```
