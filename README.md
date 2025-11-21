Here's an enhanced README with a dedicated **Fraud Detection** section:

---

# Core Banking App API

The Core Banking App API is a secure and scalable backend system built with Spring Boot. It provides a complete suite of core banking operations, allowing users to manage their accounts efficiently while maintaining high standards of performance and security.

## Overview

This API supports the core functions of a modern banking platform, including:
* User registration and authentication
* Current and savings account management
* Real-time deposits and withdrawals
* Two-Factor Authentication (2FA) using OTP
* Transfers INTER & INTRA
* Real-time fraud detection and prevention**
* Administrative controls for account activation, deactivation, and user management

## Key Features

### User Features
* Register new users and verify identity
* Secure Login with Two-Factor Authentication (OTP):**
   * Step 1: User enters email + password
   * Step 2: System generates a 6-digit OTP, saves it in Redis (5-min TTL), and emails it to the user
   * Step 3: User submits OTP to complete login and receive JWT token
* Create and manage current or savings accounts
* Make real-time deposits and withdrawals
* Reset password through an OTP-based email system (via Mailtrap)
* View and update profile information
* Change password securely

### Admin Features
* Activate or deactivate user accounts
* View all registered users with pagination support
* Manage roles and permissions
* Monitor audit logs for user and account actions
* View and manage all transactions
* Review and resolve fraud alerts
* Access fraud detection dashboard

### Transaction Lock Management
Ensures safe and reliable handling of all transactions — deposits, withdrawals, inter-bank, and intra-bank transfers by preventing race conditions and maintaining data integrity under high concurrency.

**How it works:**
* Each account has a dedicated lock object managed by `TransactionLockManager`
* Uses `ConcurrentHashMap` for thread-safe lock storage
* Prevents concurrent modifications to the same account
* Supports parallel processing of different accounts for optimal performance

## Fraud Detection & Security

The system includes **multi-layer fraud detection** that monitors transactions in real-time to protect against suspicious activities:

### ️ Fraud Detection Features

#### 1. **High-Value Transaction Detection**
Automatically flags deposits or withdrawals that exceed predefined thresholds.

**Example:**
* Deposit limit: ₦5,000,000
* If a user deposits ₦6,000,000, an alert is created:
  ```
  Alert Type: HIGH_VALUE_DEPOSIT
  Details: Deposit of ₦6,000,000 exceeds ₦5,000,000 limit.
  Status: Unresolved
  ```

**Configuration:**
```java
// Configurable limits per account type
SAVINGS_DEPOSIT_LIMIT = ₦5,000,000
CURRENT_DEPOSIT_LIMIT = ₦10,000,000
WITHDRAWAL_LIMIT = ₦2,000,000
```

#### 2. **Rapid Transaction Detection**
Identifies unusual patterns of multiple transactions in a short time period.

**Example:**
* If a user makes **5+ deposits within 10 minutes**, an alert is triggered:
  ```
  Alert Type: RAPID_DEPOSITS
  Details: More than 5 deposits within the last 10 minutes.
  ```

**Patterns Detected:**
* Rapid deposits (5+ in 10 minutes)
* Rapid withdrawals (5+ in 10 minutes)
* Unusual transfer frequency

#### 3. **IP Blacklist Monitoring**
Blocks transactions from known malicious IP addresses.

**Features:**
* Real-time IP checking against blacklist database
* Geographic anomaly detection
* Automatic transaction blocking from flagged IPs

**Example:**
```
Alert Type: BLACKLISTED_IP
Details: Transaction attempted from blacklisted IP: 192.168.1.100
Action: Transaction blocked automatically
```

#### 4. **Velocity Checks**
Monitors transaction velocity and spending patterns.

**Detects:**
* Sudden large withdrawals after account dormancy
* Unusual spending spikes compared to historical patterns
* Round-amount transactions (common in fraud)

#### 5. **Geographic Anomaly Detection**
Flags transactions from unusual or impossible locations.

**Example:**
* User logs in from Lagos at 10:00 AM
* Same user attempts transaction from London at 10:05 AM
* Alert triggered for impossible travel

###  Fraud Alert Management

#### Alert Structure
```java
{
  "id": 1,
  "userId": 123,
  "transactionId": 456,
  "alertType": "HIGH_VALUE_DEPOSIT",
  "alertDetails": "Deposit of ₦6,000,000.00 exceeds ₦5,000,000 limit.",
  "createdAt": "2025-11-20T11:23:44",
  "resolved": false
}
```

#### Admin Actions
* **View all fraud alerts** with filtering and pagination
* **Mark alerts as resolved** after investigation
* **Block/unblock accounts** based on fraud assessment
* **Export fraud reports** for compliance and analysis

###  Audit Trail Integration

Every transaction includes comprehensive logging:

```java
Audit Log Entry:
- User ID: 123
- Username: john_doe
- Action: DEPOSIT
- Entity: Account
- Entity ID: 456
- Old Value: ₦1,000,000.00
- New Value: ₦7,000,000.00
- Description: User deposited ₦6,000,000 into account 1234567890
- IP Address: 197.210.52.15
- Timestamp: 2025-11-20 11:23:44
- Status: SUCCESS
```

###  Fraud Response Workflow

```
Transaction Initiated
        ↓
  Lock Acquired
        ↓
  Balance Updated
        ↓
  Transaction Saved
        ↓
  Fraud Checks Run:
    ├─→ High Value Check
    ├─→ Velocity Check
    ├─→ IP Blacklist Check
    └─→ Pattern Analysis
        ↓
  Alert Created (if suspicious)
        ↓
  Admin Notification Sent
        ↓
  Audit Log Recorded
        ↓
  Lock Released
```

###  Fraud Detection Metrics

The system tracks:
* Total alerts generated
* Alert resolution time
* False positive rate
* Blocked transaction amount
* Most common fraud patterns
* High-risk account identification

### API Endpoints for Fraud Management

```
GET  /api/admin/fraud-alerts              # List all alerts
GET  /api/admin/fraud-alerts/{id}         # Get specific alert
POST /api/admin/fraud-alerts/{id}/resolve # Mark alert as resolved
GET  /api/admin/fraud-alerts/stats        # Fraud statistics
GET  /api/admin/fraud-alerts/user/{id}    # User-specific alerts
```

## Security and Performance

* **JWT authentication** for secure, stateless access
* **Role-based authorization** for user and admin separation
* **Rate limiting with Bucket4J** to prevent abuse (100 requests/minute per user)
* **Redis caching** for improved speed and reduced database load
* **Centralized exception handling** for clear and consistent error messages
* **BigDecimal precision** for accurate financial calculations
* **Thread-safe transaction processing** with account-level locking
* **Real-time fraud detection** with multiple security layers

## Infrastructure and DevOps

* Database migrations handled with **Flyway**
* File uploads for user profile images via **Cloudinary**
* **Docker setup** for easy deployment with MySQL and Redis
* Asynchronous email notifications for registration, transactions, and password resets
* **UTF-8 (utf8mb4) encoding** for international currency support (₦, $, €, £, etc.)

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA |
| Database | MySQL 8.0 (utf8mb4 encoding) |
| Caching | Redis |
| Authentication | JWT |
| Rate Limiting | Bucket4J |
| Email | Mailtrap (SMTP) |
| File Storage | Cloudinary |
| Containerization | Docker, Docker Compose |
| Database Migration | Flyway |
| Build Tool | Maven |

## Getting Started

### Prerequisites
* Java 17+
* Maven 3.9+
* MySQL 8.0+
* Redis
* Docker & Docker Compose (optional for local setup)

### Environment Variables

Create a `.env` file or configure your environment variables:

```env
# Database
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=bank_db
MYSQL_USER=bank_user
MYSQL_PASSWORD=bank_password

# Cloudinary
CLOUDINARY_URL=cloudinary://<api_key>:<api_secret>@<cloud_name>

# Email
MAILTRAP_USERNAME=<your_mailtrap_username>
MAILTRAP_PASSWORD=<your_mailtrap_password>

# JWT
JWT_SECRET=<your_jwt_secret>
JWT_EXPIRATION=86400000

# Fraud Detection Limits
DEPOSIT_LIMIT=5000000
WITHDRAWAL_LIMIT=2000000
RAPID_TRANSACTION_THRESHOLD=5
RAPID_TRANSACTION_WINDOW_MINUTES=10
```

### Running with Docker

```bash
# Start all services
docker-compose up --build

# Access MySQL
docker exec -it bank_mysql mysql -u bank_user -p

# Access Redis
docker exec -it redis redis-cli

# View logs
docker-compose logs -f bank_app
```

### Running Locally (Without Docker)

1. **Build the project:**
   ```bash
   mvn clean package -Dmaven.test.skip=true
   ```

2. **Run the application:**
   ```bash
   java -jar target/BankApplication-0.0.1-SNAPSHOT.jar
   ```

Make sure MySQL and Redis are running before starting the application.

## Database Schema

### Key Tables
* `users` - User account information
* `accounts` - Bank account details (savings/current)
* `transactions` - Transaction history
* `fraud_alerts` - Fraud detection alerts
* `audit_logs` - Complete audit trail
* `account_limits` - Configurable transaction limits
* `flyway_schema_history` - Migration tracking

All tables use **utf8mb4** character encoding to support international currencies like ₦ (Nigerian Naira).

## Logging and Monitoring

* Logs are displayed in the console with configurable log levels
* Flyway provides migration logs during database setup
* Redis cache and request logs are visible through Spring Boot logging
* Fraud detection events are logged with full context
* Audit trail provides complete transaction history

## Notes

* Each account type (SAVINGS or CURRENT) is validated to ensure uniqueness and compliance
* Email notifications are handled asynchronously to improve response time
* Redis caching is used for session management and frequently accessed data
* JWT tokens are validated and refreshed securely through stored refresh tokens
* All monetary values use **BigDecimal** for precision
* Transaction locks prevent race conditions in concurrent operations
* Fraud alerts are generated in real-time but don't block legitimate transactions
* Admin dashboard provides fraud analytics and resolution tools

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request


Built with ❤️ for secure and reliable banking operations