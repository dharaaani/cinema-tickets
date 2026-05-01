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

class TicketServiceImplUnitTest {

    private TicketPaymentService paymentService;
    private SeatReservationService seatService;
    private TicketService ticketService;

    @BeforeEach
    void setup() {
        paymentService = mock(TicketPaymentService.class);
        seatService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, seatService);
    }

    @Test
    void oneAdultOneChildIsAllowed() {
        ticketService.purchaseTickets(
                1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
        );

        verify(paymentService).makePayment(1L, 40);
        verify(seatService).reserveSeat(1L, 2);
    }

    @Test
    void oneAdultTwoInfantsIsAllowed() {
        ticketService.purchaseTickets(
                1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
        );

        verify(paymentService).makePayment(1L, 25);
        verify(seatService).reserveSeat(1L, 1);
    }

    @Test
    void noAdultOneChildThrowsException() {
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(
                        1L,
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
                )
        );

        verifyNoInteractions(paymentService, seatService);
    }

    @Test
    void noTicketsProvidedThrowsException() {
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L)
        );

        verifyNoInteractions(paymentService, seatService);
    }

    @Test
    void nullTicketRequestThrowsException() {
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(1L, (TicketTypeRequest) null)
        );

        verifyNoInteractions(paymentService, seatService);
    }

    @Test
    void moreThan25TicketsThrowsException() {
        assertThrows(
                InvalidPurchaseException.class,
                () -> ticketService.purchaseTickets(
                        1L,
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 13),
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 13)
                )
        );

        verifyNoInteractions(paymentService, seatService);
    }
}
