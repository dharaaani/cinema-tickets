package uk.gov.dwp.uc.pairtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.validation.TicketPurchaseValidator;

import java.util.Map;

public class TicketServiceImpl implements TicketService {

    private static final Logger log =
            LoggerFactory.getLogger(TicketServiceImpl.class);

    private static final int ADULT_PRICE = 25;
    private static final int CHILD_PRICE = 15;

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;
    private final TicketPurchaseValidator validator;

    public TicketServiceImpl(
            TicketPaymentService ticketPaymentService,
            SeatReservationService seatReservationService
    ) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
        this.validator = new TicketPurchaseValidator();
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) {

        log.debug("Validating purchase request for accountId={}", accountId);

        Map<TicketTypeRequest.Type, Integer> ticketsByType =
                validator.validate(accountId, ticketTypeRequests);

        int adults = ticketsByType.getOrDefault(TicketTypeRequest.Type.ADULT, 0);
        int children = ticketsByType.getOrDefault(TicketTypeRequest.Type.CHILD, 0);

        int totalPrice = ADULT_PRICE * adults + CHILD_PRICE * children;
        int seatsToReserve = adults + children;

        log.info("Processing payment: accountId={}, amount={}", accountId, totalPrice);
        ticketPaymentService.makePayment(accountId, totalPrice);

        log.info("Reserving seats: accountId={}, seats={}", accountId, seatsToReserve);
        seatReservationService.reserveSeat(accountId, seatsToReserve);
    }
}