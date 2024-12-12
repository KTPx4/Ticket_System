package services.response.ticket;

import java.util.List;

import model.ticket.MyTicket;

public class ResponseMyTicket {
    private String message;
    private int length;
    private List<MyTicket> data;

    public ResponseMyTicket() {
    }

    public ResponseMyTicket(String message, int length, List<MyTicket> data) {
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

    public List<MyTicket> getData() {
        return data;
    }

    public void setData(List<MyTicket> data) {
        this.data = data;
    }
    // Getters and Setters
}

