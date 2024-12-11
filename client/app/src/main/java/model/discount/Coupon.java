package model.discount;

public class Coupon {
    private String _id;
    private String code;
    private String name;
    private String desc;
    private String type;
    private int maxDiscount;
    private int count;
    private int percentDiscount;
    private Boolean isValid;

    public Coupon() {
    }

    public Coupon(String _id, String code, String name, String desc, String type, int maxDiscount, int count, int percentDiscount, Boolean isValid) {
        this._id = _id;
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.maxDiscount = maxDiscount;
        this.count = count;
        this.percentDiscount = percentDiscount;
        this.isValid = isValid;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(int maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPercentDiscount() {
        return percentDiscount;
    }

    public void setPercentDiscount(int percentDiscount) {
        this.percentDiscount = percentDiscount;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
