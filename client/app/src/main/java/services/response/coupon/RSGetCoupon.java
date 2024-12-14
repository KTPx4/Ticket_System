package services.response.coupon;

import java.util.List;

import model.Coupon;

public class RSGetCoupon {
    private int length;
    private String message;
    private List<Coupon> data;
    private long point;

    public RSGetCoupon() {
    }

    public RSGetCoupon(int length, String message, List<Coupon> data, long point) {
        this.length = length;
        this.message = message;
        this.data = data;
        this.point = point;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) {
        this.point = point;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Coupon> getData() {
        return data;
    }

    public void setData(List<Coupon> data) {
        this.data = data;
    }
}
