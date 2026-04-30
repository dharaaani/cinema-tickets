import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {

    private final TicketPaymentService ticketPaymentService = mock();
    private final SeatReservationService seatReservationService = mock();

    private final TicketService ticketService = new TicketServiceImpl(ticketPaymentService, seatReservationService);

    @BeforeEach
    void setupMocks() {
        doNothing().when(ticketPaymentService).makePayment(anyLong(), anyInt());
        doNothing().when(seatReservationService).reserveSeat(anyLong(), anyInt());
    }
    @Test
    void oneAdultTwoInfantsIsAllowed() {
        long accountId = 1L;

        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        ticketService.purchaseTickets(accountId, adult, infants);

        verify(ticketPaymentService).makePayment(accountId, 25);
        verify(seatReservationService).reserveSeat(accountId, 1);
    }

    @Test
    void oneAdultThreeInfantsThrowsException() {
        long accountId = 1L;

        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infants = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3);

        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(accountId, adult, infants)
        );

        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }
    @Test
    void oneAdultTicketReservesOneSeatCosts25() {
        long accountId = 1L;
        int expectedPrice = 25;
        int expectedSeats = 1;
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        ticketService.purchaseTickets(accountId, ticketTypeRequest);

        verify(ticketPaymentService).makePayment(accountId, expectedPrice);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
        verifyNoMoreInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void twoAdultTicketsReserveTwoSeatsCosts50() {
        long accountId = 1L;
        int expectedPrice = 50;
        int expectedSeats = 2;
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        ticketService.purchaseTickets(accountId, ticketTypeRequest);

        verify(ticketPaymentService).makePayment(accountId, expectedPrice);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
        verifyNoMoreInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void oneAdultOneChildReserveTwoSeatsCosts40() {
        long accountId = 1L;
        int expectedPrice = 40;
        int expectedSeats = 2;
        TicketTypeRequest adultTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest childTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        ticketService.purchaseTickets(accountId, adultTicketRequest, childTicketRequest);

        verify(ticketPaymentService).makePayment(accountId, expectedPrice);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
        verifyNoMoreInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void oneAdultOneInfantReserveOneSeatCosts25() {
        long accountId = 1L;
        int expectedPrice = 25;
        int expectedSeats = 1;
        TicketTypeRequest adultTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(accountId, adultTicketRequest, infantTicketRequest);

        verify(ticketPaymentService).makePayment(accountId, expectedPrice);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
        verifyNoMoreInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void oneAdultOneChildOneInfantReserveTwoSeatsCosts40() {
        long accountId = 1L;
        int expectedPrice = 40;
        int expectedSeats = 2;
        TicketTypeRequest adultTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest childTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        ticketService.purchaseTickets(accountId, adultTicketRequest, childTicketRequest, infantTicketRequest);

        verify(ticketPaymentService).makePayment(accountId, expectedPrice);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
        verifyNoMoreInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void noAdultOneInfantThrowsException() {
        long accountId = 1L;
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, infantTicketRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void noAdultOneChildThrowsException() {
        long accountId = 1L;
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, infantTicketRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }


    @Test
    void twoAdultsTwoInfantsReservesTwoSeatsCosts50() {
        long accountId = 1L;
        int expectedPrice = 50;
        int expectedSeats = 2;
        TicketTypeRequest adultTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2);

        ticketService.purchaseTickets(accountId, adultTicketRequest, infantTicketRequest);

        verify(ticketPaymentService).makePayment(accountId, expectedPrice);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
        verifyNoMoreInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void positiveAccountIdWorks() {
        long accountId = 3L;
        int expectedPrice = 25;
        int expectedSeats = 1;
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        ticketService.purchaseTickets(accountId, ticketTypeRequest);

        verify(ticketPaymentService).makePayment(accountId, expectedPrice);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
        verifyNoMoreInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void accountId0ThrowsException() {
        long accountId = 0L;
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, ticketTypeRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void negativeAccountIdThrowsException() {
        long accountId = -1L;
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, ticketTypeRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void nullAccountIdThrowsException() {
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(null, ticketTypeRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void moreThan25AdultsThrowsException() {
        long accountId = 1L;
        TicketTypeRequest ticketTypeRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, ticketTypeRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void moreThan25TicketsThrowsException() {
        long accountId = 1L;
        TicketTypeRequest adultTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 13);
        TicketTypeRequest childTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 13);
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 13);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, adultTicketRequest, childTicketRequest, infantTicketRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void nullRequestThrowsException() {
        long accountId = 1L;

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, (TicketTypeRequest) null));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void nullArrayRequestThrowsException() {
        long accountId = 1L;

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, (TicketTypeRequest[]) null));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void emptyRequestThrowsException() {
        long accountId = 1L;
        TicketTypeRequest[] emptyArray = new TicketTypeRequest[]{};

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, emptyArray));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void nullRequestInArrayThrowsException() {
        long accountId = 1L;
        TicketTypeRequest ticketTypeRequest1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest ticketTypeRequest2 = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, ticketTypeRequest1, null, ticketTypeRequest2));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void nullTypeInRequestThrowsException() {
        long accountId = 1L;
        TicketTypeRequest correctRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest nullTypeRequest = new TicketTypeRequest(null, 1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, correctRequest, nullTypeRequest));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void negativeNumberOfTicketsThrowsException() {
        long accountId = 1L;
        TicketTypeRequest correctRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest negativeTickets = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, correctRequest, negativeTickets));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }

    @Test
    void zeroTicketsInRequestThrowsException() {
        long accountId = 1L;
        TicketTypeRequest correctRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest zeroTickets = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);

        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(accountId, correctRequest, zeroTickets));
        verifyNoInteractions(ticketPaymentService, seatReservationService);
    }
}
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


Sent from Outlook for Mac

**********************************************************************
This document is strictly confidential and is intended only for use by the addressee.
If you are not the intended recipient any disclosure, copying, distribution
or other action taken in reliance of the information contained in this email is strictly prohibited.
 
Any views expressed by the sender of this message are not necessarily those of the Department
for Work and Pensions.
If you have received this transmission in error please tell us and then permanently delete
what you have received.
This email was scanned for viruses by the Department for Work and Pensions antivirus services and was found to be virus free.
Please note: Incoming and outgoing email messages are routinely monitored for compliance with our Email Policy.
**********************************************************************

