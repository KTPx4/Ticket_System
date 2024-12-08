package model.ticket;

import java.util.List;

import model.event.Event;

public class MyTicket {
    private List<Ticket> tickets;
    private Event event;

    public MyTicket() {
    }

    public MyTicket(List<Ticket> tickets, Event event) {
        this.tickets = tickets;
        this.event = event;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
