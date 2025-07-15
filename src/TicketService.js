import TicketTypeRequest from '../lib/TicketTypeRequest.js';
import InvalidPurchaseException from '../lib/InvalidPurchaseException.js';
import TicketPaymentService from '../thirdparty/TicketPaymentService.js';
import SeatReservationService from '../thirdparty/SeatReservationService.js';

export default class TicketService {
  #paymentService = new TicketPaymentService();
  #reservationService = new SeatReservationService();

  purchaseTickets(accountId, ...ticketTypeRequests) {
    if (!Number.isInteger(accountId) || accountId <= 0) {
      throw new InvalidPurchaseException('Invalid account ID.');
    }

    if (!ticketTypeRequests.length) {
      throw new InvalidPurchaseException('At least one ticket must be requested.');
    }

    let totalTickets = 0;
    let adultTickets = 0;
    let childTickets = 0;
    let infantTickets = 0;
    let totalAmount = 0;
    let totalSeats = 0;

    for(const request of ticketTypeRequests) {
      const type = request.getTicketType();
      const count = request.getNoOfTickets();

      if(count<= 0) {
        throw new InvalidPurchaseException('Ticket cannot be negative.');
      }

      totalTickets += count;

      switch(type) {
        case 'ADULT':
          adultTickets += count;
          totalAmount += count * 25;
          totalSeats += count;
          break;
        case 'CHILD':
          childTickets += count;
          totalAmount += count * 15;
          totalSeats += count;
          break;
        case 'INFANT':
          infantTickets += count;
          totalSeats += count;
          break;
        default:
          throw new InvalidPurchaseException('Invalid ticket type. ${type}');
      }
    }
    if (totalTickets > 25) {
      throw new InvalidPurchaseException('Cannot purchase more than 25 tickets.');
    }

    if (adultTickets ===0 && (childTickets > 0 || infantTickets > 0)) {
      throw new InvalidPurchaseException('Cannot purchase infant tickets without an adult ticket.');
    }
    if (infantTickets > adultTickets) {
      throw new InvalidPurchaseException('Cannot purchase more infant tickets than adult tickets.');
    }
    this.#paymentService.makePayment(accountId, totalAmount);
    this.#reservationService.reserveSeat(accountId, totalSeats);
  }
}
