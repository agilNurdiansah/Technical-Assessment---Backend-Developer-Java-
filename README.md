# Technical Assessment - Backend Developer (Java)

Mini project microservices berbasis **Spring Boot 4 / Java 17**, **PostgreSQL**, dan **Docker Compose** yang dirancang dengan **Clean Architecture (Hexagonal / Ports and Adapters)** dan mengimplementasikan seluruh spesifikasi untuk **Level Middle**:

1. **Spring IoC (Inversion of Control & Dependency Injection)**
2. **Java Stream API (Advanced Transformations, Grouping, Partitioning, Summary, Reduction)**
3. **Intermediate Native SQL Query (CTE, Window Functions, Multi-table JOINs, HAVING)**
4. **Containerization & Microservices (Inter-service REST Communication, Docker Compose Orchestration)**

---

## ⚙️ Konfigurasi Environment (100% Dinamis)

Seluruh konfigurasi bersifat **dinamis** dan dapat diubah langsung melalui environment variable atau file `.env`. Tidak ada nilai atau konfigurasi yang di-hardcode.

Berikut adalah template konfigurasi yang dapat langsung di-copy dan diubah nilainya:

```env
# Database PostgreSQL Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5433

# User Service Configuration (Port 8081)
USER_SERVICE_PORT=8081
USER_DB_URL=jdbc:postgresql://postgres:5432/userdb
USER_DB_USER=postgres
USER_DB_PASSWORD=postgres

# Order Service Configuration (Port 8082)
ORDER_SERVICE_PORT=8082
ORDER_DB_URL=jdbc:postgresql://postgres:5432/orderdb
ORDER_DB_USER=postgres
ORDER_DB_PASSWORD=postgres
USER_SERVICE_URL=http://user-service:8081
```

> **Catatan Port Host:**
> * Database PostgreSQL di-expose ke host pada port **`5433`** (agar tidak bentrok jika sudah ada PostgreSQL lokal yang berjalan di port default 5432).
> * Jika Anda menjalankan aplikasi langsung di host (via `./mvnw` / IDE) tanpa Docker untuk service Java-nya:
>   * Ubah `USER_DB_URL=jdbc:postgresql://localhost:5433/userdb`
>   * Ubah `ORDER_DB_URL=jdbc:postgresql://localhost:5433/orderdb`
>   * Ubah `USER_SERVICE_URL=http://localhost:8081`

---

## 🗄️ Bagaimana Database Bekerja?

1. **PostgreSQL 16 Multi-Database**:
   * Menggunakan container PostgreSQL 16.
   * File `infra/docker/postgres/init.sql` otomatis dieksekusi saat container pertama kali dibuat:
     ```sql
     CREATE DATABASE userdb;
     CREATE DATABASE orderdb;
     ```
   * Menerapkan prinsip **Database-per-Service** (pola standar microservices di mana `user-service` dan `order-service` memiliki schema database terpisah dan terisolasi).

2. **Automated Schema Migration (Flyway)**:
   * Setiap service mengelola tabelnya sendiri secara otomatis melalui migrasi Flyway:
     * `user-service`: `V1__init_user_schema.sql` (tabel `users`).
     * `order-service`: `V1__init_order_schema.sql` (tabel `orders` dan `order_items`).

3. **Automated Seeder (Data Awal)**:
   * Data awal otomatis diisikan saat aplikasi pertama kali menyala:
     * `user-service`: menginjeksi 2 user awal (`Alice` status `ACTIVE`, `Bob` status `INACTIVE`).
     * `order-service`: `DatabaseSeeder.java` menginjeksi 4 order realistis lintas status (`NEW`, `PAID`, `COMPLETED`) beserta item belanja untuk mendukung pengujian window function dan analitik Stream.

---

## 🚀 Cara Menjalankan Project

### Cara 1: Menggunakan Docker Compose (Sangat Direkomendasikan)
Cukup jalankan satu perintah dari root folder project:

```bash
docker compose -f infra/docker/docker-compose.yml up -d --build
```

Cek status seluruh container:
```bash
docker ps
```

Container yang akan aktif:
* **PostgreSQL**: `localhost:5433`
* **User Service**: `http://localhost:8081`
* **Order Service**: `http://localhost:8082`

Untuk mematikan container:
```bash
docker compose -f infra/docker/docker-compose.yml down
```

---

### Cara 2: Menjalankan Lokal (Maven & Java 17)
Jika ingin menjalankan service langsung di terminal/IDE:

1. **Nyalakan Database PostgreSQL saja di Docker**:
   ```bash
   docker compose -f infra/docker/docker-compose.yml up -d postgres
   ```

2. **Jalankan User Service (Terminal 1)**:
   ```bash
   USER_DB_URL=jdbc:postgresql://localhost:5433/userdb ./mvnw spring-boot:run -pl services/user-service
   ```

3. **Jalankan Order Service (Terminal 2)**:
   ```bash
   ORDER_DB_URL=jdbc:postgresql://localhost:5433/orderdb USER_SERVICE_URL=http://localhost:8081 ./mvnw spring-boot:run -pl services/order-service
   ```

---

## 📮 Postman Collection & Environment

Telah disediakan file collection dan environment lengkap yang siap langsung di-import ke Postman:

* 📁 **Collection File**: `postman/Technical_Assessment.postman_collection.json`
* 📁 **Environment File**: `postman/Technical_Assessment.postman_environment.json`

### Cara Import ke Postman:
1. Buka aplikasi **Postman**.
2. Klik tombol **Import** (di pojok kiri atas).
3. Pilih/drag kedua file di atas (`postman/Technical_Assessment.postman_collection.json` dan `postman/Technical_Assessment.postman_environment.json`).
4. Di pojok kanan atas Postman, pilih environment aktif: **`Technical Assessment Local`**.
5. Seluruh request sudah terkelompok rapi ke dalam folder:
   * `1. User Service (Port 8081)`
   * `2. Order Service - Middle Level Core Features`
   * `3. Order Service - Standard CRUD & Junior Report`

---

## 🧪 Cara Testing Fitur Level Middle

### 1. Spring IoC (Strategy Pattern & Dynamic Injection)
Sistem memiliki 4 strategi kalkulasi diskon yang di-inject secara dinamis oleh Spring IoC container tanpa hardcoding:
* `PERCENTAGE_TEN`: Diskon 10%.
* `BULK_PURCHASE`: Diskon volume 15% jika jumlah item $\ge$ 3.
* `VIP_TIER`: Diskon loyalitas 20%.
* `NONE`: Fallback tanpa diskon.

**Contoh Test POST Order dengan Diskon 10%:**
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "discountType": "PERCENTAGE_TEN",
    "items": [
      {
        "productName": "Mechanical Keyboard RGB",
        "quantity": 1,
        "price": 100.00
      }
    ]
  }'
```
*Hasil:* Subtotal \$100.00 $\rightarrow$ Diskon \$10.00 $\rightarrow$ Total bayar \$90.00. Spring IoC juga otomatis mempublikasikan `OrderCreatedEvent` ke listener audit log dan notifikasi.

---

### 2. Validasi Komunikasi Antar Microservice
`order-service` secara sinkron memanggil `user-service:8081/api/v1/users/{userId}` untuk memverifikasi apakah customer terdaftar dan aktif.

**Test Order dengan User yang Tidak Ada (userId 9999):**
```bash
curl -X POST http://localhost:8082/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 9999,
    "discountType": "NONE",
    "items": [{"productName": "USB Cable", "quantity": 1, "price": 10.00}]
  }'
```
*Hasil:* Ditolak dengan respon `400 Bad Request` (*"User with id 9999 does not exist or is unreachable"*).

---

### 3. Java Stream API Analytics
Menguji seluruh operasi fungsional Stream Java (`groupingBy`, `partitioningBy`, `summarizingDouble`, `reduce`, dan `flatMap`):

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

### 4. Intermediate Native SQL Queries

#### A. CTE (*Common Table Expression*) + Window Function `DENSE_RANK()`
Menghitung peringkat kompetitif belanja customer:
```bash
curl -X GET http://localhost:8082/api/v1/orders/reports/user-rankings
```

#### B. Window Function `ROW_NUMBER()` & `SUM(...) OVER (...)` (Running Total)
Menghitung urutan transaksi dan saldo akumulasi pengeluaran customer:
```bash
curl -X GET http://localhost:8082/api/v1/orders/reports/cumulative-spent
```

#### C. Multi-table `INNER JOIN` dengan Aggregations dan `HAVING`
Menampilkan laporan penjualan produk yang memenuhi syarat volume minimum penjualan:
```bash
curl -X GET "http://localhost:8082/api/v1/orders/reports/product-performance?minQuantity=1"
```

---

## 🧪 Menjalankan Automated Unit Test

Seluruh 10 unit test dapat dijalankan secara instan di lokal:
```bash
./mvnw clean test
```
*Hasil:* `BUILD SUCCESS` (10 tests run, 0 failures, 0 errors).
