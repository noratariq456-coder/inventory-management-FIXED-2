# Inventory Management System

An inventory management system built with **Spring Boot 3 (Java 21)**, featuring web pages (Thymeleaf), a REST API, and login via Spring Security.

## Contents
- [Data Model (ERD)](#data-model-erd)
- [Requirements](#requirements)
- [Running with Docker](#running-with-docker-recommended)
- [Running Locally without Docker](#running-locally-without-docker)
- [Default Users](#default-users)
- [Important Links](#important-links)
- [Security Note](#security-note)

## Data Model (ERD)

- **Products**: `product_id, name, sku, price, category_id`
- **Categories**: `category_id, name, description`
- **Suppliers**: `supplier_id, name, contact_email, phone`
- **Product_Suppliers** (join table): `product_id, supplier_id`
- **Stock_Transactions**: `transaction_id, product_id, supplier_id, quantity, transaction_type, transaction_date`
- **Users / Authorities**: login credentials and roles

**Relationships:**

| From | To | Type | Notes |
|---|---|---|---|
| Product | Category | Many-to-One | `Product.category_id`, required |
| Product | Supplier | **Many-to-Many** | via `product_suppliers`; a product can have several suppliers and a supplier can supply several products. Managed through `POST/DELETE /api/products/{id}/suppliers/{supplierId}` |
| Stock_Transaction | Product | Many-to-One | each transaction is for exactly one product |
| Stock_Transaction | Supplier | Many-to-One | each transaction records exactly one supplier for that stock movement |

> A product's suppliers are now available two ways: **directly** (`Product.suppliers`, via `product_suppliers` — `GET /api/products/{id}/linked-suppliers`) for "which suppliers can supply this product", and **historically** through `Stock_Transactions` (`GET /api/products/{id}/suppliers`) for "which suppliers we've actually received stock from for this product". A product's current stock quantity is still calculated on the fly from the sum of its `STOCK_IN` and `STOCK_OUT` transactions, rather than being stored directly on the product itself.

## Requirements

- Docker + Docker Compose (recommended)

**Or**, to run without Docker:
- Java 21 (JDK)
- Maven 3.9+
- MySQL 8

## Running with Docker (Recommended)

Starts the application and a MySQL database together automatically:

```bash
docker compose up --build
```

The app will be available at: **http://localhost:8081**

To change the database password (instead of the default `changeMe123`), create a `.env` file next to `docker-compose.yml`:

```
DB_PASSWORD=your_strong_password
```

To stop everything:
```bash
docker compose down
```

To also wipe the database data (clean start):
```bash
docker compose down -v
```

## Running Locally without Docker

1. Start MySQL and make sure an empty database is available (or let `createDatabaseIfNotExist=true` create it for you).
2. Set your connection details via environment variables (instead of hardcoding them in `application.properties`):

```bash
export DB_URL="jdbc:mysql://localhost:3306/inventory_management?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME=root
export DB_PASSWORD=your_password
```

3. Run the project:

```bash
./mvnw spring-boot:run
```

Or build a jar and run it directly:

```bash
./mvnw clean package -DskipTests
java -jar target/inventory-management-0.0.1-SNAPSHOT.jar
```

## Default Users

These are created automatically on first startup (from `DataInitializer`):

| Username | Password     | Role           |
|----------|--------------|----------------|
| admin    | admin123     | ADMIN          |
| manager  | manager123   | STORE_MANAGER  |
| employee | employee123  | EMPLOYEE       |

> Change these passwords before any real-world use.

## Important Links

- Web UI (Products/Suppliers/Transactions pages): `http://localhost:8081/products`
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- Actuator (application health): `http://localhost:8081/actuator/health`

## Security Note

The MySQL password now defaults to the placeholder `changeMe123` (same default used by `docker-compose.yml`) inside `application.properties` via `spring.datasource.password=${DB_PASSWORD:changeMe123}`. Override it with a real value through the `DB_PASSWORD` environment variable for any non-local environment - never commit a real password into this file:

```cmd
set "DB_PASSWORD=your_password"
mvnw spring-boot:run
```

## REST API (Angular Frontend)

### Authentication
- `POST /api/auth/login` - body `{"username": "...", "password": "..."}`, public. Authenticates against MySQL (`users`/`authorities`) and returns `{"username": "...", "roles": ["ADMIN"]}`. Establishes a session cookie used by subsequent requests.
- `GET /api/auth/me` - requires authentication, returns the same shape for the currently logged-in user.

### CORS
Configured in `SecurityConfig` for `http://localhost:4200` / `http://127.0.0.1:4200` (the default Angular dev server). Update `ALLOWED_ORIGINS` there for other environments.

### Main list endpoints (pagination + sorting + filters)
`GET /api/products` and `GET /api/stock-transactions` both return:
```json
{
  "records": [...],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 42,
  "totalPages": 5
}
```
Query params:
- Products: `page`, `size`, `sort` (e.g. `name,asc`), `keyword`, `categoryId`, `minPrice`, `maxPrice`
- Stock transactions: `page`, `size`, `sort` (e.g. `transactionDate,desc`), `productId`, `supplierId`, `transactionType`

> **Breaking change**: `GET /api/products` and `GET /api/stock-transactions` used to return a plain JSON array. They now return the paginated envelope above - the Angular services calling these endpoints need to read `.records` instead of the raw response body.

### Suppliers
Full CRUD now available: `GET /api/suppliers`, `GET /api/suppliers/{id}`, `POST /api/suppliers`, `PUT /api/suppliers/{id}`, `PATCH /api/suppliers/{id}`, `DELETE /api/suppliers/{id}`.

### Dashboard
`GET /api/stock-transactions/dashboard` returns real SQL aggregates (`SUM`/`AVG`/`COUNT` computed by MySQL, not Java loops): `totalStockIn`, `totalStockOut`, `netStock`, `totalTransactions`, `averageTransactionQuantity`. `GET /api/products/summary` returns a simple product `COUNT`.

### Error responses
All errors follow `{"timestamp", "status", "error", "message", ["fieldErrors"]}`:
- `404` - resource not found
- `409` - business rule conflict (duplicate SKU, insufficient stock, deleting a still-referenced Category/Supplier)
- `401` - bad login credentials
- `403` - authenticated but insufficient role
- `400` - validation failure (includes `fieldErrors`)

