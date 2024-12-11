package model.account;

public class Account {
    private String _id;
    private String name;
    private String email;
    private int point;
    private String image;
    private String address;

    public Account() {
    }

    public Account(String _id, String name, String email, int point, String image, String address) {
        this._id = _id;
        this.name = name;
        this.email = email;
        this.point = point;
        this.image = image;
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
