package model.ticket;

public class MyTicket {
    private String id;
    private String type;
    private String location;
    private String position;

    public MyTicket() {
    }

    public MyTicket(String id, String type, String location, String position) {
        this.id = id;
        this.type = type;
        this.location = location;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
