package services.response.event;

import java.util.List;

import model.event.News;

public class RSGetAllNews {
   private String message;
   private List<News> data;

    public RSGetAllNews() {
    }

    public RSGetAllNews(String message, List<News> data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<News> getData() {
        return data;
    }

    public void setData(List<News> data) {
        this.data = data;
    }
}
