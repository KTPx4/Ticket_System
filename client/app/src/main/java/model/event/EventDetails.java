package model.event;

import java.util.List;

public class EventDetails {
    private String _id;
    private String name;
    private String location;
    private String desc;
    private List<String> type;
    private List<String> tag;
    private boolean isTicketPosition;
    private PriceRange priceRange;
    private DateRange date;
    private boolean isHotEvent;
    private String image;

    public EventDetails() {
    }

    public EventDetails(String _id, String name, String location, String desc, List<String> type, List<String> tag, boolean isTicketPosition, PriceRange priceRange, DateRange date, boolean isHotEvent, String image) {
        this._id = _id;
        this.name = name;
        this.location = location;
        this.desc = desc;
        this.type = type;
        this.tag = tag;
        this.isTicketPosition = isTicketPosition;
        this.priceRange = priceRange;
        this.date = date;
        this.isHotEvent = isHotEvent;
        this.image = image;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

    public boolean isTicketPosition() {
        return isTicketPosition;
    }

    public void setTicketPosition(boolean ticketPosition) {
        isTicketPosition = ticketPosition;
    }

    public PriceRange getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(PriceRange priceRange) {
        this.priceRange = priceRange;
    }

    public DateRange getDate() {
        return date;
    }

    public void setDate(DateRange date) {
        this.date = date;
    }

    public boolean isHotEvent() {
        return isHotEvent;
    }

    public void setHotEvent(boolean hotEvent) {
        isHotEvent = hotEvent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
