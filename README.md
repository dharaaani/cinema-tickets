# Ticket Service

## Problem Understanding
Implement a ticket purchasing service that
- Validates ticket requests based on business needs
- Calciulates payment and seat reservations
= Interacts with external services for payment and seat booking

## Approach
- Used a class-based design with private service instances.
- Validated all inputs and enforced business rules.
- Modularized logic for readability and maintainability.

## Time/Space Complexity
- Time: O(n) where n = number of ticket types
- Space: O(1), constant space for counters and totals

## Improvements
- Add logging.for audit trails
- Add support for async services
- Addi18n for error messages