# SimpleCashSI - Banking Information System

A Spring Boot banking information system for managing clients, accounts, transfers, and financial operations.

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Features](#features)
- [REST API Documentation](#rest-api-documentation)
- [Example API Calls](#example-api-calls)
- [Business Rules](#business-rules)
- [Error Handling](#error-handling)
- [Architecture](#architecture)
- [Domain Model](#domain-model)
- [Database Access](#database-access)
- [Testing](#testing)
- [Logging](#logging)
- [Project Structure](#project-structure)
- [Bilan](#bilan)  

## Overview

SimpleCashSI handles client and advisor management, account operations like credit and debit, internal transfers between accounts, account auditing and reporting, card management, and global error handling with standardized responses.

## Technology Stack

- Java 17
- Spring Boot 3.5.8
- Gradle (build tool)
- Spring Web (REST APIs)
- Spring Data JPA (persistence)
- H2 Database (in-memory for development)
- Logback (logging with dedicated transfer logs)
- JUnit 5 (testing)

## Getting Started

### Prerequisites

You need Java 17 or higher. The project includes a Gradle wrapper, so you don't need to install Gradle separately.

### Build and Run

1. Clone the project
   ```bash
   git clone https://github.com/mhAkoum/projet_AKOUM_mohamad.git
   ```

2. Build the project
   ```bash
   ./gradlew build
   ```

3. Run the application
   ```bash
   ./gradlew bootRun
   ```

4. Access the application
   - API Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`

### Configuration

The application uses an H2 in-memory database for development. You can find the configuration in `src/main/resources/application.properties`.

```properties
# Database
spring.datasource.url=jdbc:h2:mem:simplecashdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.root=INFO
logging.level.com.simplecash.projet_akoum_mohamad=DEBUG
```

## Features

### Priority 1 - Core Features

#### Client Management

Create, read, update, and delete clients. Link clients to advisors and agencies. The system enforces a maximum of 10 clients per advisor. You can view all clients assigned to a specific advisor.

#### Account Management

Create CurrentAccount and SavingsAccount types. Each client can have one current account and one savings account. Default values are set automatically: current accounts get a 1000€ overdraft limit, and savings accounts get a 3% interest rate.

#### Account Operations

Credit accounts to increase balance, or debit accounts to decrease balance. The system enforces overdraft rules and validates that amounts are greater than zero. It raises errors when there are insufficient funds.

### Priority 2 - Important Features

#### Internal Transfers

Transfer money between accounts within the same bank. Transfers are transactional, meaning they're all-or-nothing. If something goes wrong, the entire transfer is rolled back. The system handles errors for invalid operations.

#### Transfer Logging

All transfers are logged to a dedicated file at `logs/transfers.log`. Each log entry includes the timestamp, source account, target account, amount, and status. Both successful and failed transfers are recorded.

#### Card Management

Create cards as either Visa Electron or Visa Premier. You can deactivate cards when needed. When a client is deleted, all their cards are automatically deactivated. Clients can have multiple cards.

### Priority 3

#### Account Audit

The system audits all accounts based on client type. Threshold rules apply: private clients can't have accounts below -5000€, and business clients can't have accounts below -50,000€. The audit report includes lists of credit accounts (positive balance), debit accounts (negative balance), totals of credits and debits, and any threshold violations.

## REST API Documentation

### Base URL

```
http://localhost:8080
```

### Client Management

#### GET /clients

Get all clients. Returns a 200 OK response with an array of client objects.

#### GET /clients/{id}

Get a specific client by ID. Returns 200 OK if found, or 404 Not Found if the client doesn't exist.

#### POST /clients

Create a new client. The request body should include `name`, `address`, `phone`, `email`, `clientType`, and `advisorId`. Returns 201 Created on success, 409 Conflict if the advisor already has 10 clients, or 404 Not Found if the advisor doesn't exist.

#### PUT /clients/{id}

Update client information. The request body should include `name`, `address`, `phone`, and `email`. Returns 200 OK on success, or 404 Not Found if the client doesn't exist.

#### DELETE /clients/{id}

Delete a client. This deactivates all associated cards and deletes associated accounts if their balance is zero. Returns 204 No Content on success, 400 Bad Request if any account has a non-zero balance, or 404 Not Found if the client doesn't exist.

### Account Operations

#### GET /accounts/{id}

Get account details by ID. Returns 200 OK with account information including balance, type, and client details.

#### POST /accounts/{id}/credit

Credit an account to increase its balance. The request body should include `amount` and `description`. Returns 200 OK on success, 400 Bad Request if the amount is zero or negative, or 404 Not Found if the account doesn't exist.

#### POST /accounts/{id}/debit

Debit an account to decrease its balance. The request body should include `amount` and `description`. Current accounts can go negative up to their overdraft limit, but savings accounts cannot go negative. Returns 200 OK on success, 400 Bad Request if there are insufficient funds, or 404 Not Found if the account doesn't exist.

### Audit Operations

#### GET /audit/accounts

Get a comprehensive audit report of all accounts. Returns 200 OK with an audit report containing credit accounts (positive balances), debit accounts (negative balances), threshold violations, total credits, total debits, total accounts audited, and the number of accounts with violations.

## Example API Calls

### Create a Client

```bash
curl -X POST http://localhost:8080/clients \
  -H "Content-Type: application/json" \
  -d '{"name": "New Client", "address": "123 Test Street", "phone": "555-1234", "email": "newclient@test.com", "clientType": "PRIVATE", "advisorId": 1}'
```

### Credit an Account

```bash
curl -X POST http://localhost:8080/accounts/1/credit \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.00, "description": "Initial deposit"}'
```

### Get Audit Report

```bash
curl -X GET http://localhost:8080/audit/accounts
```

## Business Rules

### Client Management

Each advisor can have a maximum of 10 clients. If you try to add an 11th client, the system throws an `AdvisorFullException` and returns HTTP 409 Conflict.

When deleting a client, all associated cards are deactivated. Accounts are only deleted if their balance is exactly zero. If any account has a non-zero balance, the deletion fails with an error.

### Account Operations

Credit operations require the amount to be greater than zero.

For current accounts, debits can make the balance go negative up to the overdraft limit (default 1000€). If a debit would exceed the balance plus the overdraft limit, the system throws an `InsufficientFundsException`.

Savings accounts cannot go negative. If a debit would make the balance negative, the system throws an `InsufficientFundsException`.

### Transfers

Transfer amounts must be greater than zero. You cannot transfer money to the same account. Transfers are transactional, so if the debit fails, the credit is not executed. All transfer attempts are logged to `logs/transfers.log`.

### Audit

Private clients cannot have accounts below -5000€. Business clients cannot have accounts below -50,000€. The audit report includes credit accounts, debit accounts, threshold violations, and totals.

## Error Handling

The application uses a global exception handler that provides standardized error responses. All errors follow the same format.

### Error Response Format

```json
{
  "timestamp": "2025-11-28T10:30:00",
  "status": 404,
  "error": "Client Not Found",
  "message": "Client with ID 999 not found",
  "path": "/clients/999"
}
```

### HTTP Status Codes

- 200 OK: Operation completed successfully
- 201 Created: Resource created successfully
- 204 No Content: Resource deleted successfully
- 400 Bad Request: Invalid input (amount is zero or negative, insufficient funds, non-zero balance on deletion)
- 404 Not Found: Resource not found (client, advisor, or account)
- 409 Conflict: Business rule violation (advisor already has 10 clients)
- 500 Internal Server Error: Unexpected server error

### Custom Exceptions

- `AdvisorFullException`: Advisor has reached the maximum client limit of 10
- `ClientNotFoundException`: The requested client was not found
- `AdvisorNotFoundException`: The requested advisor was not found
- `AccountNotFoundException`: The requested account was not found
- `InsufficientFundsException`: There are insufficient funds for the debit operation
- `AccountBalanceNotZeroException`: Cannot delete client because an account has a non-zero balance

## Architecture

### Layered Architecture

![Architecture](img/Architecture.png)

## Domain Model

### Entities and Relationships

![Diagram](img/UML.png)

### Entity Details

#### Agency

Primary key is `id`. Has a unique 5-character `code` and a `creationDate`.

#### Manager

Primary key is `id`. Has `name` and `email`. Has a one-to-one relationship with Agency.

#### Advisor

Primary key is `id`. Has `name` and `email`. Has a many-to-one relationship with Agency. Business rule: maximum 10 clients per advisor.

#### Client

Primary key is `id`. Has `name`, `address`, `phone`, `email`, and `clientType` (PRIVATE or BUSINESS). Has a many-to-one relationship with Advisor, one-to-one relationships with CurrentAccount and SavingsAccount, and a one-to-many relationship with Card.

#### Account (Abstract Base Class)

Primary key is `id`. Has `accountNumber` (unique), `balance`, and `openingDate`.

#### CurrentAccount (extends Account)

Has `overdraftLimit` with a default value of 1000.00€.

#### SavingsAccount (extends Account)

Has `interestRate` with a default value of 3.00%.

#### Card

Primary key is `id`. Has `cardType` (VISA_ELECTRON or VISA_PREMIER) and `status` (ACTIVE or INACTIVE). Has a many-to-one relationship with Client.

#### Transfer

Primary key is `id`. Has `amount`, `timestamp`, and `status`. Has many-to-one relationships with Account for both source and target accounts.

## Database Access

### H2 Console

1. Start the application
2. Open your browser and go to `http://localhost:8080/h2-console`
3. Use these login credentials
   - JDBC URL: `jdbc:h2:mem:simplecashdb`
   - User Name: `sa`
   - Password: (leave empty)
4. Click "Connect"

### Sample Queries

```sql
-- View all clients
SELECT * FROM CLIENTS;

-- View all accounts with balances
SELECT a.id, a.account_number, a.balance, 
       CASE WHEN ca.account_id IS NOT NULL THEN 'CURRENT' 
            WHEN sa.account_id IS NOT NULL THEN 'SAVINGS' 
            ELSE 'UNKNOWN' END as account_type
FROM ACCOUNTS a
LEFT JOIN CURRENT_ACCOUNTS ca ON a.id = ca.account_id
LEFT JOIN SAVINGS_ACCOUNTS sa ON a.id = sa.account_id;

-- View clients with their advisors
SELECT c.name as client_name, a.name as advisor_name, ag.code as agency_code
FROM CLIENTS c
JOIN ADVISORS a ON c.advisor_id = a.id
JOIN AGENCIES ag ON a.agency_id = ag.id;
```

## Testing

### Run All Tests

```bash
./gradlew test
```

### Test Coverage

The project includes unit tests and integration tests.

#### Unit Tests

- `ClientServiceTest` tests client creation and the max clients per advisor rule
- `AccountServiceTest` tests credit and debit operations, including overdraft limits
- `TransferServiceTest` tests transfer transactionality and rollback on failure
- `AuditServiceTest` tests audit report generation and threshold violations

#### Integration Tests

- `ClientControllerIntegrationTest` tests REST endpoints for client CRUD operations
- `AccountControllerIntegrationTest` tests REST endpoints for account operations
- `AuditControllerIntegrationTest` tests the REST endpoint for audit reports

### Test Data

The application automatically initializes test data on startup. The `DataInitializer` creates one agency (AG001), one manager (Kimo the Manager), two advisors (Tchoupi and Dora the Explora), three clients (John Sina, EPITA Students, and Mars Explorer), along with associated accounts and cards.

## Logging

### Log Files

General logs go to `logs/application.log`. Transfer logs go to `logs/transfers.log`, which is a dedicated file for all transfer operations.

### Transfer Logging

All transfer operations are logged to `logs/transfers.log` using this format:

```
TRANSFER;STARTED;2025-11-28T10:30:00;CA001;CA002;500.00
TRANSFER;SUCCESS;2025-11-28T10:30:00;CA001;CA002;500.00
```

On failure, the log looks like this:

```
TRANSFER;STARTED;2025-11-28T10:30:00;CA001;CA002;50000.00
TRANSFER;FAILED;2025-11-28T10:30:00;CA001;CA002;50000.00;Insufficient funds
```

### Logback Configuration

Logback configuration is in `src/main/resources/logback-spring.xml`. It includes a console appender for general logs, a file appender for application logs, a dedicated file appender for transfer logs, and a rolling policy for log files (10MB max, 30 days retention).

## Project Structure

```
projet_AKOUM_Mohamad/
├── src/
│   ├── main/
│   │   ├── java/com/simplecash/projet_akoum_mohamad/
│   │   │   ├── domain/          # JPA entities (Agency, Manager, Advisor, Client, Account, Card, Transfer)
│   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   ├── service/         # Business logic (ClientService, AccountService, TransferService, AuditService)
│   │   │   ├── web/             # REST controllers (ClientController, AccountController, AuditController)
│   │   │   ├── dto/             # Data transfer objects (ClientDTO, AccountDTO, ErrorResponse, etc.)
│   │   │   ├── exception/       # Custom exceptions and GlobalExceptionHandler
│   │   │   └── config/          # Configuration (DataInitializer, ComprehensiveTester)
│   │   └── resources/
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   └── test/
│       └── java/com/simplecash/projet_akoum_mohamad/
│           ├── service/         # Unit tests
│           └── web/             # Integration tests
├── build.gradle                 # Gradle build configuration
├── settings.gradle              # Gradle settings
├── README.md                    # This file
└── logs/                        # Log files (gitignored)
    ├── application.log
    └── transfers.log
```

## Bilan

### Difficulties Encountered

Working with JPA's JOINED inheritance strategy required careful handling of SQL queries, since account data is split across parent and child tables.
Some integration tests had issues with database constraint violations when using `@DirtiesContext`, which needed workarounds.
Ambiguous method calls in tests (when methods accept both `Long` and `String` parameters) required explicit type casting to resolve compilation errors.

### What's Left to Do

Loan simulation features were not implemented. Some integration tests still need fixes for database constraint handling. 
