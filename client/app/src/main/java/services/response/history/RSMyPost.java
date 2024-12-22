package services.response.history;

import model.Rating;

public class RSMyPost {
    private String message;
    private Rating data;

    public RSMyPost() {

    }

    public RSMyPost(String message, Rating data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Rating getData() {
        return data;
    }

    public void setData(Rating data) {
        this.data = data;
    }
}
