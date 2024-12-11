package services.response.ticket;

import java.util.List;

import model.ticket.MyPending;
import model.ticket.MyTicket;

public class ResponseMyPending {
    private String message;
    private int length;
    private List<MyPending> data;

    public ResponseMyPending() {
    }

    public ResponseMyPending(String message, int length, List<MyPending> data) {
        this.message = message;
        this.length = length;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<MyPending> getData() {
        return data;
    }

    public void setData(List<MyPending> data) {
        this.data = data;
    }
}
