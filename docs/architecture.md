# Architecture Overview

## Modules
- `shared/common`: shared response envelope, exception handling, and utility helpers.
- `services/user-service`: user domain (users, status), CRUD APIs, reporting.
- `services/order-service`: order domain (orders, items), CRUD APIs, reporting.
- `infra/docker`: docker-compose and database initialization.

## Clean Architecture Notes
- Presentation: REST controllers.
- Application: use-case ports, services, DTOs, mappers, and exceptions.
- Domain: entities and enums.
- Infrastructure: persistence adapters (JPA, JdbcTemplate), config, and bootstrap.

## Data Flow (Example)
Request -> Controller -> Service -> Repository -> Database -> Service -> Mapper -> Response

## Migration Strategy
- Flyway migrations live under `db/migration` in each service.
- Schema and seed data are separated into `V1__` and `V2__` scripts.

## Running Locally
1. Start Postgres via docker-compose.
2. Run `mvn -pl services/user-service spring-boot:run` and `mvn -pl services/order-service spring-boot:run`.
