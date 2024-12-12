package services.response.order;

import model.account.Account;
import model.ticket.MyPending;

public class DataGetCheckOut {
    private String Type;
    private Account User;
    private MyPending BuyTicket;
    private long Price;
    private long Discount;
    private long CouponDiscount;
    private long FinalPrice;
    private String Order;

    public DataGetCheckOut() {
    }


    public DataGetCheckOut(String type, Account user, MyPending buyTicket) {
        Type = type;
        User = user;
        BuyTicket = buyTicket;
    }

    public DataGetCheckOut(String type, Account user, MyPending buyTicket, long price, long discount, long couponDiscount, long finalPrice) {
        Type = type;
        User = user;
        BuyTicket = buyTicket;
        Price = price;
        Discount = discount;
        CouponDiscount = couponDiscount;
        FinalPrice = finalPrice;
    }

    public DataGetCheckOut(String type, Account user, MyPending buyTicket, long price, long discount, long couponDiscount, long finalPrice, String order) {
        Type = type;
        User = user;
        BuyTicket = buyTicket;
        Price = price;
        Discount = discount;
        CouponDiscount = couponDiscount;
        FinalPrice = finalPrice;
        Order = order;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public long getPrice() {
        return Price;
    }

    public void setPrice(long price) {
        Price = price;
    }

    public long getDiscount() {
        return Discount;
    }

    public void setDiscount(long discount) {
        Discount = discount;
    }

    public long getCouponDiscount() {
        return CouponDiscount;
    }

    public void setCouponDiscount(long couponDiscount) {
        CouponDiscount = couponDiscount;
    }

    public long getFinalPrice() {
        return FinalPrice;
    }

    public void setFinalPrice(long finalPrice) {
        FinalPrice = finalPrice;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Account getUser() {
        return User;
    }

    public void setUser(Account user) {
        User = user;
    }

    public MyPending getBuyTicket() {
        return BuyTicket;
    }

    public void setBuyTicket(MyPending buyTicket) {
        BuyTicket = buyTicket;
    }
}
