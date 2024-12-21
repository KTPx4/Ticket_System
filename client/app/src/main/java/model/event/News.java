package model.event;

public class News {
    private String _id;
    private String event;
    private String title;
    private String content;
    private String createdAt;

    public News(String _id, String event, String title, String content, String createdAt) {
        this._id = _id;
        this.event = event;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
    }

    public News() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
