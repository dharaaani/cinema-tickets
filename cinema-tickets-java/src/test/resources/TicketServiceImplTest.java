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