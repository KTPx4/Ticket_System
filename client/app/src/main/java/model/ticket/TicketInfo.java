package model.ticket;

import model.discount.Coupon;

public class TicketInfo {
    private String _id;
    private Ticket ticket;
    private String event;
    private Coupon coupon;
    private long price;
    private long priceFinal;
    private long moneyGive;
    private long moneyChange;
    private String date;
    private String status;

    public TicketInfo() {
    }

    public TicketInfo(String _id, Ticket ticket, String event, Coupon coupon, long price, long priceFinal, long moneyGive, long moneyChange, String date, String status) {
        this._id = _id;
        this.ticket = ticket;
        this.event = event;
        this.coupon = coupon;
        this.price = price;
        this.priceFinal = priceFinal;
        this.moneyGive = moneyGive;
        this.moneyChange = moneyChange;
        this.date = date;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getPriceFinal() {
        return priceFinal;
    }

    public void setPriceFinal(long priceFinal) {
        this.priceFinal = priceFinal;
    }

    public long getMoneyGive() {
        return moneyGive;
    }

    public void setMoneyGive(long moneyGive) {
        this.moneyGive = moneyGive;
    }

    public long getMoneyChange() {
        return moneyChange;
    }

    public void setMoneyChange(long moneyChange) {
        this.moneyChange = moneyChange;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TicketInfo{" +
                "_id='" + _id + '\'' +
                ", ticket=" + ticket +
                ", event='" + event + '\'' +
                ", coupon=" + coupon +
                ", price=" + price +
                ", priceFinal=" + priceFinal +
                ", moneyGive=" + moneyGive +
                ", moneyChange=" + moneyChange +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
