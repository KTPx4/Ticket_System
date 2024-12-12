package services.response.order;

public class ResponseValid {
    private String message;

    public ResponseValid() {
    }

    public ResponseValid(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
