import TicketService from '../src/TicketService.js';
import TicketTypeRequest from '../lib/TicketTypeRequest.js';
import InvalidPurchaseException from '../lib/InvalidPurchaseException.js';

jest.mock('../lib/TicketPaymentService.js', () => {
  return jest.fn().mockImplementation(() => ({
    makePayment: jest.fn()
  }));
});

jest.mock('../lib/SeatReservationService.js', () => {
  return jest.fn().mockImplementation(() => ({
    reserveSeat: jest.fn()
  }));
});

describe('TicketService', () => {
  test('valid purchase', () => {
    const service = new TicketService();
    const request = [
      new TicketTypeRequest('ADULT', 2),
      new TicketTypeRequest('CHILD', 1),
      new TicketTypeRequest('INFANT', 1)
    ];
    expect(() => service.purchaseTickets(10, ...request)).not.toThrow();
  });

  test('no adult should throw error', () => {
    const service = new TicketService();
    const request = [
      new TicketTypeRequest('CHILD', 2)
    ];
    expect(() => service.purchaseTickets(5, ...request)).toThrow(InvalidPurchaseException);
  });

  test('more than 25 tickets throws error', () => {
    const service = new TicketService();
    const request = [
      new TicketTypeRequest('ADULT', 20),
      new TicketTypeRequest('CHILD', 6)
    ];
    expect(() => service.purchaseTickets(1, ...request)).toThrow(InvalidPurchaseException);
  });

  test('more infants than adults throws error', () => {
    const service = new TicketService();
    const request = [
      new TicketTypeRequest('ADULT', 1),
      new TicketTypeRequest('INFANT', 2)
    ];
    expect(() => service.purchaseTickets(1, ...request)).toThrow(InvalidPurchaseException);
  });
});
