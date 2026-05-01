# Cinema Tickets – Ticket Service

This project provides an implementation of the `TicketService` interface.

The service validates ticket purchase requests, calculates the payment amount, and reserves seats using the provided third‑party services, while enforcing all required business rules.

## Technology Stack

*   **Java 21**
*   **Maven**
*   **JUnit 5**
*   **Mockito**
*   **JaCoCo** (code coverage)
*   **GitHub Actions** (CI)
*   **Docker** (optional)


### Prerequisites

*   Java 21
*   Maven 3.8+

### Run tests locally

From the Java project directory:

```bash
mvn clean test
```

*   Compiles the project
*   Runs all unit tests
*   Generates a JaCoCo code‑coverage report


## Business Rules Implemented

### Ticket Types and Prices

| Ticket Type | Price | Seat Required |
| ----------- | ----- | ------------- |
| ADULT       | £25   | Yes           |
| CHILD       | £15   | Yes           |
| INFANT      | £0    | No            |

### Ticket prices

Ticket prices and limits are defined as constants to keep simple.

In a production system, these values could be configured like properties files or environment variables to support pricing changes

### Rules

*   Child and Infant tickets **cannot** be purchased without at least one Adult ticket
*   Infants do **not** receive seats
*   A maximum of **2 infants per adult** is allowed
*   A maximum of **25 tickets total** may be purchased per request
*   Seats are reserved only for Adult and Child tickets


## Validation & Error Handling

Invalid purchase requests result in an `InvalidPurchaseException`.

Examples of invalid scenarios:

*   Null, zero, or negative account ID
*   No ticket requests provided
*   Null ticket request or null ticket type
*   Zero or negative ticket quantities
*   Child or Infant tickets without an Adult ticket
*   More than 2 infants per adult
*   More than 25 total tickets

Validation occurs before any external services are invoked.

## Design Decisions & Assumptions

### Assumptions

*   Any account ID greater than zero is valid
*   All valid accounts have sufficient funds
*   The payment and seat reservation services are reliable and always succeed when called

### Design Notes

*   `TicketServiceImpl` focuses on orchestration and delegation
*   All request validation and business rules are encapsulated in a dedicated validator
*   Third‑party packages are not modified
*   Domain validation errors are represented using a specific exception (`InvalidPurchaseException`)
*   Exceptions are allowed to propagate intentionally (fail‑fast); no unnecessary `try/catch` blocks are used
*   Ticket prices and limits are declared as constants for clarity; these could be externalised to configuration if required in future campaigns
*   The solution deliberately avoids additional infrastructure (APIs, UI, containers beyond build support) to remain within scope

## Development Approach (TDD)

I approached this exercise using a test‑first mindset.

I started by writing failing unit tests for the core business rules, such as:
- Adult ticket requirement
- Infant‑to‑adult ratio limits
- Maximum ticket count
- Seat allocation rules

Then implemented the service and validation logic to satisfy those tests, adding further tests incrementally to cover edge cases and invalid input scenarios.

### BDD

Tests are named to describe behaviour and expected outcome.

## Testing Strategy

The solution was developed with a **test‑first mindset**, focusing on business rules and edge cases before implementation.

### Testing approach

*   All external services are mocked
*   Unit tests cover:
    *   Valid purchase scenarios
    *   Boundary conditions (ticket limits, infant limits)
    *   Defensive cases (nulls, invalid inputs)
*   Tests explicitly verify:
    *   Correct payment amounts
    *   Correct seat reservation counts
    *   No interaction with external services for invalid requests

Test names are written to clearly describe expected behaviour.


## Code Coverage (JaCoCo)

JaCoCo is used to generate a code‑coverage report during the Maven test phase.

Coverage is used as a **quality signal**, not a target metric, and helps ensure that:

*   Core business rules are exercised
*   Validation and edge cases are tested
*   Failure paths are verified

The report is generated automatically at:

    target/site/jacoco/index.html


## Logging

*   Logging is kept intentionally lightweight
*   Validation and processing steps are logged at appropriate levels
*   No sensitive data is logged
*   Logging exists to support traceability, not as a control mechanism



## Continuous Integration (GitHub Actions)

A lightweight GitHub Actions workflow may be included to:

*   Build the project
*   Run the full test suite
*   Verify the solution in a clean environment

The pipeline mirrors the local `mvn test` workflow and introduces no additional behaviour.


## Docker (Optional)

A minimal Dockerfile may be provided to:

*   Run the Maven build and test suite in a clean, reproducible environment
*   Avoid differences between local and CI builds
```bash
    docker build -t cinema-tickets .
```
Docker is **not required** to use or understand the solution and is kept simple.