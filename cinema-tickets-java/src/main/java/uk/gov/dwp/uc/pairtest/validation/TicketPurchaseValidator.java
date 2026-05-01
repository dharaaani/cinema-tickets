package uk.gov.dwp.uc.pairtest.validation;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketPurchaseValidator {

    private static final int MAX_TICKETS = 25;
    private static final int MAX_INFANTS_PER_ADULT = 2;

    public Map<TicketTypeRequest.Type, Integer> validate(
            Long accountId,
            TicketTypeRequest... ticketTypeRequests
    ) {
        validateAccount(accountId);
        validateRequestArray(ticketTypeRequests);

        Map<TicketTypeRequest.Type, Integer> ticketsByType =
                Arrays.stream(ticketTypeRequests)
                        .peek(this::validateRequest)
                        .collect(Collectors.toMap(
                                TicketTypeRequest::getTicketType,
                                TicketTypeRequest::getNoOfTickets,
                                Integer::sum
                        ));

        validateTotalTickets(ticketsByType);
        validateCombinations(ticketsByType);

        return ticketsByType;
    }

    private void validateAccount(Long accountId) {
        if (accountId == null || accountId < 1) {
            throw new InvalidPurchaseException();
        }
    }

    private void validateRequestArray(TicketTypeRequest... requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException();
        }
    }

    private void validateRequest(TicketTypeRequest request) {
        if (request == null ||
                request.getTicketType() == null ||
                request.getNoOfTickets() < 1) {
            throw new InvalidPurchaseException();
        }
    }

    private void validateTotalTickets(Map<TicketTypeRequest.Type, Integer> tickets) {
        int total = tickets.values().stream().mapToInt(Integer::intValue).sum();
        if (total > MAX_TICKETS) {
            throw new InvalidPurchaseException();
        }
    }

    private void validateCombinations(Map<TicketTypeRequest.Type, Integer> tickets) {
        int adults = tickets.getOrDefault(TicketTypeRequest.Type.ADULT, 0);
        int children = tickets.getOrDefault(TicketTypeRequest.Type.CHILD, 0);
        int infants = tickets.getOrDefault(TicketTypeRequest.Type.INFANT, 0);

        if ((children > 0 || infants > 0) && adults == 0) {
            throw new InvalidPurchaseException();
        }

        if (infants > adults * MAX_INFANTS_PER_ADULT) {
            throw new InvalidPurchaseException();
        }
    }
}