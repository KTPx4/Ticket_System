package model.ticket;

import java.util.List;

public class MyEvent {
    private String name;
    private String date;
    private List<MyTicket> tickets;

    public MyEvent() {
    }

    public MyEvent(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public MyEvent(String name, String date, List<MyTicket> tickets) {
        this.name = name;
        this.date = date;
        this.tickets = tickets;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<MyTicket> getTickets() {
        return tickets;
    }

    public void setTickets(List<MyTicket> tickets) {
        this.tickets = tickets;
    }
}
