export default class TicketTypeRequest {
  #type;
  #count;

  constructor(type, count) {
    if (!['ADULT', 'CHILD', 'INFANT'].includes(type)) {
      throw new TypeError('Invalid ticket type');
    }
    if (!Number.isInteger(count) || count < 0) {
      throw new TypeError('Invalid ticket count');
    }
    this.#type = type;
    this.#count = count;
  }

  getTicketType() {
    return this.#type;
  }

  getNoOfTickets() {
    return this.#count;
  }
}
