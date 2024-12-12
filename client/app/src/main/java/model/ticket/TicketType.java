package model.ticket;

public class TicketType {
    private String _id;
    private String event;
    private String typeTicket;
    private String location;
    private long price;

    public TicketType() {
    }

    public TicketType(String _id, String event, String typeTicket, String location, long price) {
        this._id = _id;
        this.event = event;
        this.typeTicket = typeTicket;
        this.location = location;
        this.price = price;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTypeTicket() {
        return typeTicket;
    }

    public void setTypeTicket(String typeTicket) {
        this.typeTicket = typeTicket;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
