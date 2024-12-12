package services.response.ticket;


import model.ticket.MyPending;

public class ResponsePending {
    private String message;
    private int length;
    private MyPending data;

    public ResponsePending() {
    }

    public ResponsePending(String message, int length, MyPending data) {
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

    public MyPending getData() {
        return data;
    }

    public void setData(MyPending data) {
        this.data = data;
    }
}
