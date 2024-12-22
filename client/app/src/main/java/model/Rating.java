package model;

import java.util.List;

import model.account.Account;
import modules.DateConverter;

public class Rating {
    private String _id;
    private Account account;
    private String event;
    private int rating;
    private String comment;
    private String createdAt;
    private List<FileRating> file;

    public Rating() {
    }


    public Rating(String _id, Account account, String event, int rating, String comment, String createdAt, List<FileRating> file) {
        this._id = _id;
        this.account = account;
        this.event = event;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
        this.file = file;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return DateConverter.convertToHCM(createdAt);
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<FileRating> getFile() {
        return file;
    }

    public void setFile(List<FileRating> file) {
        this.file = file;
    }

    public class FileRating{
        private String typeFile;
        private String name;

        public FileRating() {
        }

        public FileRating(String typeFile, String name) {
            this.typeFile = typeFile;
            this.name = name;
        }

        public String getTypeFile() {
            return typeFile;
        }

        public void setTypeFile(String typeFile) {
            this.typeFile = typeFile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
