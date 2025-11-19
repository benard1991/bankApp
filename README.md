Core Banking App API

The  Core Banking App API is a secure and scalable backend system built with Spring Boot.
It provides a complete suite of core banking operations, allowing users to manage their accounts efficiently while maintaining high standards of performance and security.

Overview

This API supports the core functions of a modern banking platform, including:

* User registration and authentication
* Current and savings account management
* Real-time deposits and withdrawals
* Two-Factor Authentication (2FA) using OTP
* Transafers INTER & INTRA
* Administrative controls for account activation, deactivation, and user management



Key Features

User Features

* Register new users and verify identity
* Secure Login with Two-Factor Authentication (OTP):
   Step 1: User enters email + password
   Step 2: System generates a 6-digit OTP, saves it in Redis (5-min TTL), and emails it to the user
   Step 3: User submits OTP to complete login and receive
* Create and manage current or savings accounts
* Make real-time deposits and withdrawals
* Two-Factor Authentication (2FA) using OTP
* Reset password through an OTP-based email system (via Mailtrap)
* View and update profile information
* Change password securely

Admin Features

* Activate or deactivate user accounts
* View all registered users with pagination support
* Manage roles and permissions
* Monitor audit logs for user and account actions
* View and manage all transactions

Transaction Lock Management

Ensures safe and reliable handling of all transactions — deposits, withdrawals, inter-bank, and intra-bank transfers 
by preventing race conditions and maintaining data integrity under high concurrency.

Security and Performance

* JWT authentication for secure, stateless access
* Role-based authorization for user and admin separation
* Rate limiting with Bucket4J to prevent abuse
* Redis caching for improved speed and reduced database load
* Centralized exception handling for clear and consistent error messages

Infrastructure and DevOps

* Database migrations handled with **Flyway**
* File uploads for user profile images via **Cloudinary**
* Docker setup for easy deployment with **MySQL** and **Redis**
* Asynchronous email notifications for registration, transactions, and password resets


Tech Stack

| Component            | Technology                                          |
| -------------------- | --------------------------------------------------- |
| Backend              | Java, Spring Boot, Spring Security, Spring Data JPA |
| Database             | MySQL                                               |
| Caching              | Redis                                               |
| Authentication       | JWT                                                 |
| Rate Limiting        | Bucket4J                                            |
| Email                | Mailtrap (SMTP)                                     |
| File Storage         | Cloudinary                                          |
| Containerization     | Docker                                              |
| Build Tool           | Maven                                               |



Getting Started
Prerequisites

* Java 17+
* Maven 3.9+
* MySQL
* Redis
* Docker & Docker Compose *(optional for local setup)*


Environment Variables

Create a `.env` file or configure your environment variables:


CLOUDINARY_URL=cloudinary://<api_key>:<api_secret>@<cloud_name>
MAILTRAP_USERNAME=<your_mailtrap_username>
MAILTRAP_PASSWORD=<your_mailtrap_password>
JWT_SECRET=<your_jwt_secret>


Running with Docker
docker-compose up --build

Access MySQL:

docker exec -it mysql_db mysql -u root -p

Access Redis:

docker exec -it redis redis-cli


Running Locally (Without Docker)

Build the project:**
mvn clean package -Dmaven.test.skip=true

Run the application:**

java -jar target/BankApplication-0.0.1-SNAPSHOT.jar

Make sure MySQL and Redis are running before starting the application.

Logging and Monitoring

* Logs are displayed in the console
* Flyway provides migration logs during database setup
* Redis cache and request logs are visible through Spring Boot logging

Notes

* Each account type (SAVINGS or CURRENT) is validated to ensure uniqueness and compliance.
* Email notifications are handled asynchronously to improve response time.
* Redis caching is used for session management and frequently accessed data.
* JWT tokens are validated and refreshed securely through stored refresh tokens.





