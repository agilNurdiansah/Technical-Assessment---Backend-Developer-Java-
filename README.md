# Technical Assessment (Java)

Mini project microservices dengan Spring Boot, Postgres, dan Docker Compose. Terdiri dari User Service dan Order Service, lengkap dengan CRUD dan laporan (native SQL).

## Prasyarat
- Docker Desktop
- Git
- curl (untuk testing API)

Opsional (jika ingin build lokal tanpa Docker build):
- Java 17
- Maven 3.9+

Tidak perlu install Java/Maven jika menjalankan via Docker saja.

## Struktur Project
- `shared/common`: response envelope + global exception handler
- `services/user-service`: clean architecture (presentation, application, domain, infrastructure)
- `services/order-service`: clean architecture (presentation, application, domain, infrastructure)
- `infra/docker`: docker-compose + init database
- `docs/api`: OpenAPI specs

## Cara Menjalankan (Docker - disarankan)
1) Jalankan container
```bash
docker compose -f infra/docker/docker-compose.yml up -d --build
```

2) Cek container berjalan
```bash
docker ps
```

Service akan aktif di:
- User Service: `http://localhost:8081`
- Order Service: `http://localhost:8082`
- Postgres: `localhost:5433`

## Seeder (Data Awal)
Seeder otomatis berjalan saat service start, hanya jika tabel masih kosong.

Seed data saat ini:
- users: 2
- orders: 2
- order_items: 4

## Environment Variables
User Service:
- `USER_DB_URL` (default `jdbc:postgresql://postgres:5432/userdb`)
- `USER_DB_USER` (default `postgres`)
- `USER_DB_PASSWORD` (default `postgres`)
- `USER_SERVICE_PORT` (default `8081`)

Order Service:
- `ORDER_DB_URL` (default `jdbc:postgresql://postgres:5432/orderdb`)
- `ORDER_DB_USER` (default `postgres`)
- `ORDER_DB_PASSWORD` (default `postgres`)
- `ORDER_SERVICE_PORT` (default `8082`)

## Nilai Enum
User Status: `ACTIVE`, `INACTIVE`
Order Status: `NEW`, `PAID`, `SHIPPED`, `CANCELLED`

## Format Response
Success (ApiResponse):
```json
{
  "success": true,
  "message": "OK",
  "data": {},
  "timestamp": "2026-02-22T15:00:00Z"
}
```

List (PageResponse):
```json
{
  "items": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "timestamp": "2026-02-22T15:00:00Z"
}
```

Error (ErrorResponse):
```json
{
  "code": "BAD_REQUEST",
  "message": "Email already registered",
  "path": "/api/v1/users",
  "details": null,
  "timestamp": "2026-02-22T15:00:00Z"
}
```

## User Service API
Base URL: `http://localhost:8081/api/v1/users`

### Create User
Request:
```bash
curl -i -X POST "http://localhost:8081/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com","status":"ACTIVE"}'
```

Response (201):
```json
{
  "success": true,
  "message": "User created",
  "data": {
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com",
    "status": "ACTIVE",
    "createdAt": "2026-02-22T15:00:00Z",
    "updatedAt": "2026-02-22T15:00:00Z"
  },
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### Get User by ID
Request:
```bash
curl -i "http://localhost:8081/api/v1/users/1"
```

Response (200):
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com",
    "status": "ACTIVE",
    "createdAt": "2026-02-22T15:00:00Z",
    "updatedAt": "2026-02-22T15:00:00Z"
  },
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### List Users (Pagination)
Request:
```bash
curl -i "http://localhost:8081/api/v1/users?page=0&size=10"
```

Response (200):
```json
{
  "items": [
    {
      "id": 1,
      "name": "Alice",
      "email": "alice@example.com",
      "status": "ACTIVE",
      "createdAt": "2026-02-22T15:00:00Z",
      "updatedAt": "2026-02-22T15:00:00Z"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### Update User
Request:
```bash
curl -i -X PUT "http://localhost:8081/api/v1/users/1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Updated","email":"alice@example.com","status":"INACTIVE"}'
```

Response (200):
```json
{
  "success": true,
  "message": "User updated",
  "data": {
    "id": 1,
    "name": "Alice Updated",
    "email": "alice@example.com",
    "status": "INACTIVE",
    "createdAt": "2026-02-22T15:00:00Z",
    "updatedAt": "2026-02-22T15:00:00Z"
  },
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### Delete User
Request:
```bash
curl -i -X DELETE "http://localhost:8081/api/v1/users/1"
```

Response (200):
```json
{
  "success": true,
  "message": "User deleted",
  "data": null,
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### Report: Active Users (Native SQL)
Request:
```bash
curl -i "http://localhost:8081/api/v1/users/reports/active"
```

Response (200):
```json
{
  "success": true,
  "message": "Active users",
  "data": [
    {
      "id": 1,
      "name": "Alice",
      "email": "alice@example.com",
      "status": "ACTIVE"
    }
  ],
  "timestamp": "2026-02-22T15:00:00Z"
}
```

## Order Service API
Base URL: `http://localhost:8082/api/v1/orders`

### Create Order
Request:
```bash
curl -i -X POST "http://localhost:8082/api/v1/orders" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"status":"NEW","items":[{"productName":"Keyboard","quantity":1,"price":49.99},{"productName":"Mouse","quantity":2,"price":19.99}]}'
```

Response (201):
```json
{
  "success": true,
  "message": "Order created",
  "data": {
    "id": 1,
    "userId": 1,
    "status": "NEW",
    "totalAmount": 89.97,
    "items": [
      {"productName":"Keyboard","quantity":1,"price":49.99,"subtotal":49.99},
      {"productName":"Mouse","quantity":2,"price":19.99,"subtotal":39.98}
    ],
    "createdAt": "2026-02-22T15:00:00Z",
    "updatedAt": "2026-02-22T15:00:00Z"
  },
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### Get Order by ID
Request:
```bash
curl -i "http://localhost:8082/api/v1/orders/1"
```

### List Orders (Pagination)
Request:
```bash
curl -i "http://localhost:8082/api/v1/orders?page=0&size=10"
```

### Get Orders by User ID
Request:
```bash
curl -i "http://localhost:8082/api/v1/orders/user/1"
```

### Update Order Status
Request:
```bash
curl -i -X PUT "http://localhost:8082/api/v1/orders/1/status" \
  -H "Content-Type: application/json" \
  -d '{"status":"PAID"}'
```

### Delete Order
Request:
```bash
curl -i -X DELETE "http://localhost:8082/api/v1/orders/1"
```

### Report: Order Summary by User (Native SQL)
Request:
```bash
curl -i "http://localhost:8082/api/v1/orders/reports/by-user"
```

Response (200):
```json
{
  "success": true,
  "message": "Order summary by user",
  "data": [
    {"userId": 1, "orderCount": 2, "totalAmount": 199.98}
  ],
  "timestamp": "2026-02-22T15:00:00Z"
}
```

## Contoh Error
### Duplicate Email (400)
Request:
```bash
curl -i -X POST "http://localhost:8081/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{"name":"Dup","email":"dup@example.com","status":"ACTIVE"}'
```
Request kedua dengan email yang sama akan menghasilkan:
```json
{
  "code": "BAD_REQUEST",
  "message": "Email already registered",
  "path": "/api/v1/users",
  "details": null,
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### Order Items Kosong (400)
Request:
```bash
curl -i -X POST "http://localhost:8082/api/v1/orders" \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"status":"NEW","items":[]}'
```
Response:
```json
{
  "code": "BAD_REQUEST",
  "message": "Order items are required",
  "path": "/api/v1/orders",
  "details": null,
  "timestamp": "2026-02-22T15:00:00Z"
}
```

### Not Found (404)
Request:
```bash
curl -i "http://localhost:8081/api/v1/users/99999"
```
Response:
```json
{
  "code": "NOT_FOUND",
  "message": "User not found",
  "path": "/api/v1/users/99999",
  "details": null,
  "timestamp": "2026-02-22T15:00:00Z"
}
```

## OpenAPI
Spesifikasi tersedia di `docs/api/`.
