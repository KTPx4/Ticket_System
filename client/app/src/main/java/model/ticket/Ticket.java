package model.ticket;

import model.event.EventDetails;

public class Ticket {
    private String _id;
    private String event;
    private int position;
    private String desc;
    private TicketType info;
    private boolean isAvailable;
    private boolean isValid;
    private String accBuy;
    private String expiresAt;
    private EventDetails eventDetails;

    public Ticket() {
    }

    public Ticket(String _id, String event, int position, String desc, TicketType info, boolean isAvailable, boolean isValid, String accBuy, String expiresAt, EventDetails eventDetails) {
        this._id = _id;
        this.event = event;
        this.position = position;
        this.desc = desc;
        this.info = info;
        this.isAvailable = isAvailable;
        this.isValid = isValid;
        this.accBuy = accBuy;
        this.expiresAt = expiresAt;
        this.eventDetails = eventDetails;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TicketType getInfo() {
        return info;
    }

    public void setInfo(TicketType ticketType) {
        this.info = ticketType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getAccBuy() {
        return accBuy;
    }

    public void setAccBuy(String accBuy) {
        this.accBuy = accBuy;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public EventDetails getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDetails eventDetails) {
        this.eventDetails = eventDetails;
    }
}
