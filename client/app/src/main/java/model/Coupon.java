package model;

public class Coupon {
    private String code;
    private String type;
    private long maxDiscount;
    private int percentDiscount;
    private int count;

    public Coupon() {
    }

    public Coupon(String code, String type, long maxDiscount, int percentDiscount, int count) {
        this.code = code;
        this.type = type;
        this.maxDiscount = maxDiscount;
        this.percentDiscount = percentDiscount;
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(long maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public int getPercentDiscount() {
        return percentDiscount;
    }

    public void setPercentDiscount(int percentDiscount) {
        this.percentDiscount = percentDiscount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
