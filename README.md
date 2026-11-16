# Technical Assessment - Backend Developer (Java)

Mini project microservices berbasis **Spring Boot 4 / Java 17**, **PostgreSQL**, dan **Docker Compose** yang dirancang dengan **Clean Architecture (Hexagonal / Ports and Adapters)** dan mengimplementasikan seluruh spesifikasi untuk **Level Middle**:

1. **Spring IoC (Inversion of Control & Dependency Injection)**
2. **Java Stream API (Advanced Transformations, Grouping, Partitioning, Summary, Reduction)**
3. **Intermediate Native SQL Query (CTE, Window Functions, Multi-table JOINs, HAVING)**
4. **Containerization & Microservices (Inter-service REST Communication, Docker Compose Orchestration)**

---

## 🏛️ Arsitektur Sistem

```
                         ┌───────────────────────┐
                         │    PostgreSQL 16      │
                         │ (userdb & orderdb)    │
                         └───────────┬───────────┘
                                     │
           ┌─────────────────────────┴────────────────────────┐
           ▼                                                  ▼
┌──────────────────────┐   REST HTTP (Spring RestClient)    ┌──────────────────────┐
│     User Service     │ ◄───────────────────────────────── │    Order Service     │
│      Port: 8081      │       (User Existence & Status)    │      Port: 8082      │
└──────────────────────┘                                    └──────────────────────┘
   Clean Architecture                                          Clean Architecture
   - Presentation                                              - Presentation
   - Application                                               - Application (IoC Strategy, Stream Analytics)
   - Domain                                                    - Domain (Events, Entities)
   - Infrastructure                                            - Infrastructure (Intermediate Native SQL)
```

---

## 🎯 Pemenuhan Kriteria Level Middle

### 1. Spring IoC (Inversion of Control)
- **Dynamic Strategy Pattern via Spring IoC**:
  - Interface `DiscountStrategy` memiliki implementasi:
    - `NoDiscountStrategy` (default/fallback)
    - `PercentageDiscountStrategy` (diskon 10%)
    - `BulkPurchaseDiscountStrategy` (diskon volume untuk pesanan >= 3 item)
    - `VipUserDiscountStrategy` (diskon 20% untuk tier VIP)
  - `DiscountStrategyFactory` secara otomatis meng-inject seluruh bean `DiscountStrategy` melalui Spring IoC Container (`List<DiscountStrategy>`) tanpa hardcoding.
- **Event-Driven Decoupling via Spring IoC**:
  - `ApplicationEventPublisher` mempublikasikan `OrderCreatedEvent`.
  - `@EventListener` di `OrderAuditEventListener` dan `OrderNotificationEventListener` menangkap event secara terdesentralisasi di dalam container Spring.
- **Custom Bean Configuration**:
  - `AppConfig` mengonfigurasi bean `RestClient` dengan connection timeout dan read timeout untuk komunikasi antar service.

### 2. Java Stream API
- `OrderAnalyticsServiceImpl` mengimplementasikan operasi Stream Java secara komprehensif:
  - **`groupingBy`**: Mengelompokkan transaksi berdasarkan `OrderStatus` (menghitung frekuensi & akumulasi pendapatan per status).
  - **`partitioningBy`**: Memisahkan pesanan bernilai tinggi (*High-Value Orders* >= $50) dengan pesanan reguler.
  - **`summarizingDouble`**: Menghasilkan metrik statistik distribusi nilai transaksi (*count, sum, min, average, max*).
  - **`reduce`**: Menghitung total revenue finansial secara fungsional (`BigDecimal::add`).
  - **`flatMap`**: Melakukan flatten list item transaksi untuk membuat papan peringkat produk terlaris (*product sales leaderboard*).
- Endpoint: `GET /api/v1/orders/analytics/summary`

### 3. Intermediate Native SQL Query
Disediakan query SQL Native intermediate pada `OrderReportJdbcRepository`:
1. **CTE (Common Table Expression) + Window Function (`DENSE_RANK`)**:
   - Menghitung ranking total belanja customer secara kompetitif.
   - Endpoint: `GET /api/v1/orders/reports/user-rankings`
2. **Window Functions (`ROW_NUMBER` & `SUM() OVER (...)` Running Total)**:
   - Menghitung urutan transaksi per user dan akumulasi belanja berjalan (*cumulative running total*).
   - Endpoint: `GET /api/v1/orders/reports/cumulative-spent`
3. **Multi-table JOIN dengan Aggregations dan `HAVING`**:
   - Menggabungkan tabel `order_items` dan `orders` dengan filter kuantitas minimum agregat.
   - Endpoint: `GET /api/v1/orders/reports/product-performance?minQuantity=1`

### 4. Containerization & Microservices
- Terdiri dari 2 microservice terpisah:
  - `user-service` (Port 8081)
  - `order-service` (Port 8082)
- **Komunikasi Antar Microservice**:
  - `order-service` memanggil `user-service` via HTTP REST (`UserRestClientAdapter`) saat pembuatan pesanan untuk memvalidasi keberadaan dan status aktif user.
- **Docker Compose**:
  - Multi-stage build Dockerfile untuk image yang ringan dan aman.
  - Healthcheck pada PostgreSQL (`pg_isready`).
  - Dependensi startup terorkestrasi (`order-service` menunggu `postgres` healthy dan `user-service` running).

---

## 🚀 Quick Start (Docker Compose)

### 1. Menjalankan Seluruh Service
```bash
docker compose -f infra/docker/docker-compose.yml up -d --build
```

### 2. Cek Status Container
```bash
docker ps
```

Port yang tersedia:
- **User Service**: `http://localhost:8081`
- **Order Service**: `http://localhost:8082`
- **PostgreSQL**: `localhost:5433`

---

## 🧪 Testing API & Fitur Middle Level

### 1. Test Komunikasi Microservices & Spring IoC Strategy
Buat pesanan baru dengan strategi diskon `PERCENTAGE_TEN`:
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "discountType": "PERCENTAGE_TEN",
    "items": [
      {
        "productName": "Ergonomic Mechanical Keyboard",
        "quantity": 1,
        "price": 100.00
      }
    ]
  }'
```
*Hasil:* `order-service` memvalidasi user id 1 ke `user-service`. Jika aktif, Spring IoC memanggil `PercentageDiscountStrategy` (diskon $10.00, total bayar $90.00) dan memicu `OrderCreatedEvent`.

Coba dengan user yang tidak terdaftar (userId 999):
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 999,
    "discountType": "NONE",
    "items": [{"productName": "USB Cable", "quantity": 1, "price": 10.00}]
  }'
```
*Hasil:* Respon `400 Bad Request` ("User with id 999 does not exist or is unreachable").

---

### 2. Test Java Stream API Analytics
```bash
curl -X GET http://localhost:8082/api/v1/orders/analytics/summary
```
*Contoh Respon:*
```json
{
  "success": true,
  "message": "Order analytics summary (Java Stream API: groupingBy, partitioningBy, summarize, reduce)",
  "data": {
    "totalOrdersCount": 4,
    "totalRevenue": 700.00,
    "ordersCountByStatus": {
      "NEW": 1,
      "PAID": 2,
      "COMPLETED": 1
    },
    "revenueByStatus": {
      "NEW": 95.00,
      "PAID": 335.00,
      "COMPLETED": 320.00
    },
    "partitionedByValue": {
      "HIGH_VALUE": 4,
      "STANDARD": 0
    },
    "amountStatistics": {
      "count": 4,
      "sum": 750.0,
      "min": 50.0,
      "average": 187.5,
      "max": 320.0
    },
    "topSellingProducts": [
      {"productName": "Desk Mat", "totalQuantity": 3},
      {"productName": "Wireless Mouse", "totalQuantity": 3},
      {"productName": "Monitor Stand", "totalQuantity": 2}
    ]
  }
}
```

---

### 3. Test Intermediate Native SQL Queries

#### A. CTE + DENSE_RANK Window Function (Customer Spending Ranking)
```bash
curl -X GET http://localhost:8082/api/v1/orders/reports/user-rankings
```

#### B. Window Functions ROW_NUMBER & Running Cumulative Sum
```bash
curl -X GET http://localhost:8082/api/v1/orders/reports/cumulative-spent
```

#### C. Multi-table JOIN dengan Aggregations & HAVING
```bash
curl -X GET "http://localhost:8082/api/v1/orders/reports/product-performance?minQuantity=2"
```

---

## 🛠️ Build & Unit Test Lokal

Jika ingin menjalankan test unit di lingkungan lokal:
```bash
./mvnw clean test
```

Seluruh unit test mencakup:
- Pengujian Spring IoC Strategy Factory (`DiscountStrategyFactoryTest`)
- Pengujian Java Stream Analytics (`OrderAnalyticsServiceTest`)
- Pengujian Business Service & Validasi Microservice Client (`OrderServiceTest`, `UserServiceTest`)
