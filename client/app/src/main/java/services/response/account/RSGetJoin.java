package services.response.account;

import java.util.List;

import model.event.Event;

public class RSGetJoin {
    private String message;
    private List<Event> data;

    public RSGetJoin() {
    }

    public RSGetJoin(String message, List<Event> data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Event> getData() {
        return data;
    }

    public void setData(List<Event> data) {
        this.data = data;
    }
}
