package model.event;

import java.util.List;

public class Event {
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
    private List<String> followers;
    private List<String> artists;
    private List<String> news;
    private List<String> accJoins;
    private List<String> file;
    private List<String> afterEvent;
    private String image;

    public Event() {
    }

    public Event(String _id, String name, String location, String desc, List<String> type, List<String> tag, boolean isTicketPosition, PriceRange priceRange, DateRange date, boolean isHotEvent, List<String> followers, List<String> artists, List<String> news, List<String> accJoins, List<String> file, List<String> afterEvent, String image) {
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
        this.followers = followers;
        this.artists = artists;
        this.news = news;
        this.accJoins = accJoins;
        this.file = file;
        this.afterEvent = afterEvent;
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

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    public List<String> getNews() {
        return news;
    }

    public void setNews(List<String> news) {
        this.news = news;
    }

    public List<String> getAccJoins() {
        return accJoins;
    }

    public void setAccJoins(List<String> accJoins) {
        this.accJoins = accJoins;
    }

    public List<String> getFile() {
        return file;
    }

    public void setFile(List<String> file) {
        this.file = file;
    }

    public List<String> getAfterEvent() {
        return afterEvent;
    }

    public void setAfterEvent(List<String> afterEvent) {
        this.afterEvent = afterEvent;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
