package services.response.order;

public class ResponeGetCheckOut {
    private String message;
    private String token;
    private DataGetCheckOut data;

    public ResponeGetCheckOut() {
    }

    public ResponeGetCheckOut(String message, String token, DataGetCheckOut data) {
        this.message = message;
        this.token = token;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DataGetCheckOut getData() {
        return data;
    }

    public void setData(DataGetCheckOut data) {
        this.data = data;
    }
}
