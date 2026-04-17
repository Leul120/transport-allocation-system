# Transport Allocation System

An intelligent transport management platform that optimizes vehicle allocation based on real-time station crowd density. Built with a microservices architecture using Spring Boot.

---

## What This Project Does

**In Simple Terms:**
This system helps transport operators efficiently allocate vehicles to stations based on how crowded each station is. When a station gets busy, the system automatically assigns vehicles to that location, ensuring passengers don't wait too long.

**Key Features:**
- **Real-time Station Monitoring**: Tracks crowd density at transport stations using camera feeds and computer vision
- **Smart Vehicle Allocation**: Automatically assigns vehicles to stations based on demand and availability
- **Driver Mobile Interface**: Drivers receive allocation assignments and can update their status in real-time
- **Admin Dashboard**: Comprehensive management interface for stations, vehicles, drivers, and allocations
- **Secure Authentication**: JWT-based auth with role-based access control (Admin, Driver, User)

---

## Architecture Overview

This is a **monorepo** containing two independent microservices:

| Service | Port | Purpose |
|---------|------|---------|
| `tas` | 8081 | Core Transport Allocation System - manages stations, vehicles, drivers, allocations |
| `stationCamera` | 8082 | Camera/Image Processing Service - handles person counting via computer vision |

**How They Work Together:**
1. Station cameras capture video feeds
2. `stationCamera` service processes frames using AWS Rekognition to count people
3. Person count data flows to the `tas` service
4. `tas` uses this data to calculate optimal vehicle allocations
5. Drivers receive assignments via mobile/web app

---

## Technology Stack

### Backend Framework
- **Spring Boot 3.4.1** - Java-based microservices framework
- **Java 17** - Modern LTS Java version
- **Maven** - Build and dependency management

### Data & Storage
- **MySQL** - Primary relational database
- **JPA/Hibernate** - Object-relational mapping
- **Spring Data JPA** - Data access layer

### Security
- **Spring Security** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Google OAuth 2.0** - Social login integration

### Cloud & AI Services
- **AWS S3** - Image/file storage
- **AWS Rekognition** - Computer vision for person detection
- **JavaCV** - Video frame capture and processing
- **Apache Kafka** - Event streaming (configured for person count events)

### Real-time Communication
- **WebSocket** - Bidirectional driver location updates
- **Spring Messaging** - Message handling infrastructure

### Utilities
- **Lombok** - Boilerplate code reduction
- **Mapbox API** - Geolocation and mapping services
- **Micrometer** - Application metrics and monitoring

---

## Project Structure

```
Transport-Allocation-System/
├── tas/                          # Main allocation service
│   ├── src/main/java/com/tas/
│   │   ├── controllers/          # REST API endpoints
│   │   │   ├── AuthenticationController.java  # Login/signup
│   │   │   ├── AdminController.java           # Admin CRUD ops
│   │   │   └── UserController.java            # Driver operations
│   │   ├── entities/             # JPA domain models
│   │   │   ├── Station.java      # Transport stations with location
│   │   │   ├── Vehicle.java      # Vehicle details & driver link
│   │   │   ├── Allocation.java   # Vehicle-to-station assignments
│   │   │   ├── PersonCount.java  # Crowd density readings
│   │   │   ├── User.java         # Driver/user accounts
│   │   │   └── Location.java     # Geographic coordinates
│   │   ├── services/             # Business logic layer
│   │   ├── repositories/         # Data access interfaces
│   │   ├── requests/             # DTOs for incoming data
│   │   └── responses/            # Standardized API responses
│   └── pom.xml
│
├── stationCamera/                # CV microservice
│   ├── src/main/java/com/stationCamera/
│   │   ├── controllers/
│   │   ├── services/             # Image processing logic
│   │   ├── entities/
│   │   └── config/               # AWS, Kafka configurations
│   └── pom.xml
│
└── README.md
```

---

## Core Domain Models

### Station
- Unique identifier, name, location (lat/lng)
- Status (ACTIVE/INACTIVE)
- List of person count readings (crowd history)

### Vehicle
- Type (BUS, MINIBUS, etc.), capacity, registration number
- Current status (AVAILABLE, ALLOCATED, IN_TRANSIT, MAINTENANCE)
- Linked to a driver (User)
- Real-time location tracking

### Allocation
- Links a vehicle to a station
- Timestamp of creation
- Status (PENDING, ACTIVE, COMPLETED, CANCELLED)

### PersonCount
- Station reference
- Count of people detected
- Timestamp (for historical analysis)

---

## API Overview

### Authentication (`/api/v1/auth`)
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/signup` | POST | Register new driver/admin |
| `/signin` | POST | Login with credentials |
| `/refresh` | POST | Refresh JWT token |

### Admin (`/api/v1/admin`)
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/get-dashboard` | GET | System overview stats |
| `/get-all-stations` | GET | List all stations |
| `/get-all-vehicles` | GET | List all vehicles |
| `/get-all-allocations` | GET | View all allocations |
| `/get-all-users` | GET | List all users/drivers |
| `/add-station` | POST | Create new station |
| `/update-station/{id}` | PUT | Modify station details |
| `/delete-station/{id}` | DELETE | Remove station |
| `/delete-allocation/{id}` | DELETE | Cancel allocation |

### Driver/User (`/api/v1/user`)
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/get-allocation` | GET | View current driver's allocation |
| `/get-vehicle` | GET | Get linked vehicle details |
| `/update-allocation-status/{id}` | GET | Mark allocation complete |
| `/update-vehicle-status/{status}` | GET | Update vehicle state |
| `/update-vehicle` | PUT | Update location/status |

---

## Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.8+** (or use included wrapper: `mvnw`)
- **MySQL 8.0+** (running locally or accessible instance)
- **AWS Account** (for Rekognition and S3 - optional for local dev)
- **Google Cloud Console** (for OAuth - optional)

---

## Quick Start

### 1. Clone and Setup

```bash
git clone <repository-url>
cd Transport-Allocation-System
```

### 2. Configure Environment

Create environment variables (PowerShell example):

```powershell
# Database
$env:URL = "jdbc:mysql://localhost:3306/tas_db"
$env:USERNAME = "root"
$env:PASSWORD = "your_password"

# AWS (optional - for image processing)
$env:AWS_ACCESS_ID = "your_access_key"
$env:AWS_SECRET_KEY = "your_secret_key"
$env:AWS_REGION = "us-east-1"

# Google OAuth (optional)
$env:GOOGLE_CLIENT_ID = "your_client_id"
$env:GOOGLE_CLIENT_SECRET = "your_client_secret"

# Mapbox (for geolocation features)
$env:MAPBOX_KEY = "your_mapbox_token"
```

### 3. Build Both Services

```powershell
# Build TAS service
cd tas
.\mvnw.cmd clean package -DskipTests

# Build stationCamera service
cd ..\stationCamera
.\mvnw.cmd clean package -DskipTests
```

### 4. Run Services

```powershell
# Terminal 1 - Start TAS (port 8081)
cd tas
.\mvnw.cmd spring-boot:run

# Terminal 2 - Start stationCamera (port 8082)
cd stationCamera
.\mvnw.cmd spring-boot:run
```

### 5. Verify

- TAS API: http://localhost:8081/api/v1/auth/
- stationCamera: http://localhost:8082/

---

## Development Commands

```powershell
# Run tests
cd tas; .\mvnw.cmd test
cd stationCamera; .\mvnw.cmd test

# Run with specific profile
cd tas; .\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# Package for deployment
cd tas; .\mvnw.cmd clean package
cd stationCamera; .\mvnw.cmd clean package

# Run packaged JARs
java -jar tas/target/tas-0.0.1-SNAPSHOT.jar
java -jar stationCamera/target/stationCamera-0.0.1-SNAPSHOT.jar
```

---

## Configuration Reference

### `tas/src/main/resources/application.properties`

```properties
# Server
server.port=8081

# Database
spring.datasource.url=${URL}
spring.datasource.username=${USERNAME}
spring.datasource.password=${PASSWORD}
spring.jpa.hibernate.ddl-auto=update

# Security
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}

# CORS (frontend integration)
spring.web.cors.allowed-origins=http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE

# AWS
aws.accessKeyId=${AWS_ACCESS_ID}
aws.region=${AWS_REGION}
mapbox_key=${MAPBOX_KEY}
```

---

## Security Considerations

**⚠️ NEVER commit real secrets to Git.**

If secrets were accidentally committed:
1. **Rotate credentials immediately** in AWS/Google consoles
2. Use `git filter-repo` or BFG Repo-Cleaner to remove from history
3. Force push cleaned history (coordinate with team)

**Security Features Implemented:**
- Passwords hashed with BCrypt
- JWT tokens with expiration
- Role-based access control (RBAC)
- CORS configuration for frontend isolation
- Input validation on all endpoints

---

## Deployment Options

### Docker (Recommended)
```dockerfile
# Example Dockerfile for tas service
FROM eclipse-temurin:17-jdk-alpine
COPY target/tas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### AWS ECS/Fargate
- Containerize both services
- Use AWS RDS for managed MySQL
- Configure ALB for load balancing
- Set up CloudWatch for monitoring

### Traditional Server
- Use systemd services or PM2
- Configure reverse proxy (Nginx)
- Enable HTTPS with Let's Encrypt

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Port 8081/8082 already in use | Change `server.port` in `application.properties` |
| Database connection refused | Verify MySQL is running; check URL/credentials |
| AWS Rekognition errors | Check AWS credentials and region configuration |
| JWT token invalid | Ensure system clocks are synchronized; check token expiration |
| CORS errors from frontend | Verify `spring.web.cors.allowed-origins` matches your frontend URL |
| Maven build failures | Run `mvnw clean` and ensure Java 17 is in PATH |

---

## Contributing

1. **Fork** the repository
2. Create a **feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit** changes with clear messages
4. **Push** to your fork (`git push origin feature/amazing-feature`)
5. Open a **Pull Request** against `main`

**PR Checklist:**
- [ ] Code compiles without warnings
- [ ] All tests pass
- [ ] No secrets committed
- [ ] API documentation updated (if applicable)
- [ ] Commit messages are descriptive

---

## Future Enhancements

- [ ] Machine learning for demand prediction
- [ ] Mobile app for drivers (React Native/Flutter)
- [ ] Real-time passenger mobile app
- [ ] Integration with traffic APIs for route optimization
- [ ] Analytics dashboard with historical trends

---

## License

[Add your license here - MIT, Apache 2.0, etc.]

---

## Support

For issues or questions:
- Open a GitHub Issue
- Contact: [leulmelkamu15@gmail.com]

---

**Built with Spring Boot | AWS | MySQL | Computer Vision**
