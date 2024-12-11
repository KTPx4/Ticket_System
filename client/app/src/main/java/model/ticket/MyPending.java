package model.ticket;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

import model.account.Account;
import model.event.Event;
import modules.DateConverter;

public class MyPending{

    private String _id;
    private List<Account> members;
    private Account accCreate;
    private Event event;
    private List<TicketInfo> ticketInfo;
    private String date;
    private String typePayment;
    private String status;

    public MyPending() {
    }

    public MyPending(String _id, List<Account> members, Account accCreate, Event event, List<TicketInfo> ticketInfo, String date, String typePayment, String status) {
        this._id = _id;
        this.members = members;
        this.accCreate = accCreate;
        this.event = event;
        this.ticketInfo = ticketInfo;
        this.date = date;
        this.typePayment = typePayment;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<Account> getMembers() {
        return members;
    }

    public void setMembers(List<Account> members) {
        this.members = members;
    }

    public Account getAccCreate() {
        return accCreate;
    }

    public void setAccCreate(Account accCreate) {
        this.accCreate = accCreate;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<TicketInfo> getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(List<TicketInfo> ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypePayment() {
        return typePayment;
    }

    public void setTypePayment(String typePayment) {
        this.typePayment = typePayment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return DateConverter.convertToHCM(date);
    }

    @Override
    public String toString() {
        return "MyPending{" +
                "_id='" + _id + '\'' +
                ", members=" + members +
                ", accCreate=" + accCreate +
                ", event=" + event +
                ", ticketInfo=" + ticketInfo +
                ", date='" + date + '\'' +
                ", typePayment='" + typePayment + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
