# FirstClub Membership Service

## Features

- **Multi-Tier Membership System**: Silver, Gold, Platinum, and Diamond tiers with configurable benefits
- **Flexible Subscription Plans**: Monthly, Quarterly, and Yearly billing options
- **Automatic Tier Progression**: Users automatically upgrade based on order count and total spend
- **JWT Authentication**: Secure API access with Spring Security
- **RESTful API**: Complete CRUD operations with OpenAPI documentation
- **Dockerized**: One-command deployment with Docker Compose
- **Production-Ready**: Includes health checks, metrics, and comprehensive logging

## Tech Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.2.1** - Framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **H2 Database** - In-memory database (local)
- **PostgreSQL** - Production database
- **Maven** - Build tool
- **Docker** - Containerization
- **Swagger/OpenAPI** - API documentation

## Quick Start

### Prerequisites

- Java 21
- Maven 3.6+
- Docker & Docker Compose (optional)

### Run Locally

```bash
# Clone the repository
git clone <repository-url>
cd first-club

# Build the project
mvn clean package

# Run the application
java -jar target/membership-service-1.0.0.jar
```

The application will start on `http://localhost:8080`

### Run with Docker

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## Testing

### Run Unit Tests

```bash
mvn test
```

### API Testing with Postman

1. Import the collection: `postman/FirstClub-Membership-Service.postman_collection.json`
2. Import the environment: `postman/FirstClub-Local.postman_environment.json`
3. Run the collection to test all endpoints

## Project Structure

```
src/
├── main/
│   ├── java/com/firstclub/membership/
│   │   ├── config/          # Configuration classes
│   │   ├── constants/       # Application constants
│   │   ├── controller/      # REST controllers
│   │   ├── domain/          # Entities and enums
│   │   ├── dto/             # Request/Response DTOs
│   │   ├── exception/       # Custom exceptions
│   │   ├── repository/      # Data repositories
│   │   ├── scheduler/       # Scheduled tasks
│   │   ├── security/        # Security components
│   │   └── service/         # Business logic
│   └── resources/
│       ├── application.yml           # Main configuration
│       ├── application-local.yml     # Local profile
│       ├── application-dev.yml       # Dev profile
│       ├── application-prod.yml      # Production profile
│       └── data.sql                  # Sample data
└── test/                    # Unit tests
```

## Key Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Membership Plans
- `GET /api/plans` - Get all plans
- `GET /api/plans/{id}` - Get plan by ID

### Membership Tiers
- `GET /api/tiers` - Get all tiers
- `GET /api/tiers/{id}` - Get tier with benefits

### Subscriptions (Requires Authentication)
- `POST /api/subscriptions` - Create subscription
- `GET /api/subscriptions/current` - Get current subscription
- `PUT /api/subscriptions/order` - Update order statistics
- `DELETE /api/subscriptions` - Cancel subscription

## Configuration

### Profiles

- **local**: H2 in-memory database, debug logging
- **dev**: PostgreSQL, development settings
- **prod**: PostgreSQL, production-optimized

### Environment Variables

```bash
SPRING_PROFILE=local              # Active profile
JWT_SECRET=your-secret-key        # JWT signing key
REDIS_HOST=localhost              # Redis host 
REDIS_PORT=6379                   # Redis port 
```

## Design Highlights
- **Optimistic Locking**: Prevents concurrent modification issues
- **Pessimistic Locking**: Ensures data consistency for critical operations
- **Scheduled Tasks**: Automatic subscription expiry handling
- **Comprehensive Logging**: SLF4J with contextual information
- **Exception Handling**: Global exception handler with meaningful responses

