package services.response.event;

import java.util.List;

import model.event.News;

public class RSGetNews {
    private String message;
    private News data;
    private String eventName;

    public RSGetNews() {
    }

    public RSGetNews(String message, News data, String eventName) {
        this.message = message;
        this.data = data;
        this.eventName = eventName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public News getData() {
        return data;
    }

    public void setData(News data) {
        this.data = data;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
