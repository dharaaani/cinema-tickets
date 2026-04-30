package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketServiceImpl implements TicketService {

    private static final int ADULT_PRICE = 25;
    private static final int CHILD_PRICE = 15;
    private static final int MAX_TICKETS = 25;
    private static final int MAX_INFANTS_PER_ADULT = 2;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(
            TicketPaymentService ticketPaymentService,
            SeatReservationService seatReservationService
    ) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        rejectInvalidAccountId(accountId);
        rejectInvalidRequestArray(ticketTypeRequests);

        Map<TicketTypeRequest.Type, Integer> ticketsByType =
                validateAndGroupTicketsByType(ticketTypeRequests);

        int adults = ticketsByType.getOrDefault(TicketTypeRequest.Type.ADULT, 0);
        int children = ticketsByType.getOrDefault(TicketTypeRequest.Type.CHILD, 0);
        int infants = ticketsByType.getOrDefault(TicketTypeRequest.Type.INFANT, 0);

        rejectInvalidTicketCombinations(adults, children, infants);

        int totalPrice = ADULT_PRICE * adults + CHILD_PRICE * children;
        int seatsToReserve = adults + children;

        ticketPaymentService.makePayment(accountId, totalPrice);
        seatReservationService.reserveSeat(accountId, seatsToReserve);
    }

    private void rejectInvalidAccountId(Long accountId) {
        if (accountId == null || accountId < 1) {
            throw new InvalidPurchaseException();
        }
    }

    private void rejectInvalidRequestArray(TicketTypeRequest... ticketTypeRequests) {
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException();
        }
    }

    private Map<TicketTypeRequest.Type, Integer> validateAndGroupTicketsByType(
            TicketTypeRequest... ticketTypeRequests) {

        Map<TicketTypeRequest.Type, Integer> ticketsByType =
                Arrays.stream(ticketTypeRequests)
                        .peek(this::rejectInvalidRequest)
                        .collect(Collectors.toMap(
                                TicketTypeRequest::getTicketType,
                                TicketTypeRequest::getNoOfTickets,
                                Integer::sum
                        ));

        int totalTickets = ticketsByType.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException();
        }

        return ticketsByType;
    }

    private void rejectInvalidRequest(TicketTypeRequest request) {
        if (request == null ||
                request.getTicketType() == null ||
                request.getNoOfTickets() < 1) {
            throw new InvalidPurchaseException();
        }
    }

    private void rejectInvalidTicketCombinations(int adults, int children, int infants) {

        if ((children > 0 || infants > 0) && adults == 0) {
            throw new InvalidPurchaseException();
        }

        if (infants > adults * MAX_INFANTS_PER_ADULT) {
            throw new InvalidPurchaseException();
        }
    }
}