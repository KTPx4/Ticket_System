package services.response.account;

import model.account.Account;
import model.ticket.MyPending;

public class ResponseAccount {
    private String message;
    private Account data;


    public ResponseAccount() {
    }

    public ResponseAccount(String message, Account data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Account getData() {
        return data;
    }

    public void setData(Account data) {
        this.data = data;
    }
}
