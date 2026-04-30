# Cinema Tickets – Ticket Service

This project provides an implementation of the `TicketService` interface for the Cinema Tickets coding exercise.

The service validates ticket purchase requests, calculates the correct payment amount, and reserves seats using the provided third‑party services, while enforcing all required business rules.

## Technology Stack

*   **Java 21**
*   **Maven**
*   **JUnit 5**
*   **Mockito**
*   Docker & GitHub Actions for build verification

### Prerequisites

*   Java 21
*   Maven 3.8+

### Run tests locally

From the Java project directory:

```bash
mvn clean test
```


## Business Rules Implemented

### Ticket Types and Prices

| Ticket Type | Price | Seat Required |
| ----------- | ----- | ------------- |
| ADULT       | £25   | Yes           |
| CHILD       | £15   | Yes           |
| INFANT      | £0    | No            |

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


## Testing Approach

*   All external services are mocked
*   Unit tests cover:
    *   Valid purchase scenarios
    *   Boundary conditions (e.g. infant limits, ticket limits)
    *   Defensive cases (nulls, invalid inputs)
*   Tests verify:
    *   Correct payment amounts
    *   Correct seat reservations
    *   No external service interaction on invalid requests

The test suite is designed to clearly describe expected behaviour and edge cases.


## Design Notes

*   The `TicketServiceImpl` focuses on orchestration and validation
*   Third‑party packages are not modified
*   Business rules (prices and limits) are defined as constants to make them explicit and easy to change
*   The implementation avoids unnecessary frameworks or infrastructure

## Assumptions

*   Any account ID greater than zero is valid
*   All valid accounts have sufficient funds
*   Payment and seat reservation services are reliable and succeed when called


### GitHub Actions

A lightweight CI pipeline may be included to run the test suite on push and pull requests.

### Docker

A minimal Dockerfile may be used to run the Maven build and tests in a clean environment.

Both are optional and kept intentionally simple.