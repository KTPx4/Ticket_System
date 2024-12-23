package services.response.history;

import java.util.List;

import model.Rating;

public class RSGetAll {
    private String message;
    private List<Rating> data;
    private RSRatingEvent event;
    private RSRatingPagination pagination;

    public RSGetAll() {
    }

    public RSGetAll(String message, List<Rating> data, RSRatingEvent event, RSRatingPagination pagination) {
        this.message = message;
        this.data = data;
        this.event = event;
        this.pagination = pagination;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Rating> getData() {
        return data;
    }

    public void setData(List<Rating> data) {
        this.data = data;
    }

    public RSRatingEvent getEvent() {
        return event;
    }

    public void setEvent(RSRatingEvent event) {
        this.event = event;
    }

    public RSRatingPagination getPagination() {
        return pagination;
    }

    public void setPagination(RSRatingPagination pagination) {
        this.pagination = pagination;
    }

    public class RSRatingEvent{
        private String _id;
        private String name;
        private String location;
        private String date;
        private float rating;
        private boolean hasPost;

        public RSRatingEvent() {
        }

        public RSRatingEvent(String _id, String name, String location, String date, float rating, boolean hasPost) {
            this._id = _id;
            this.name = name;
            this.location = location;
            this.date = date;
            this.rating = rating;
            this.hasPost = hasPost;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public boolean isHasPost() {
            return hasPost;
        }

        public void setHasPost(boolean hasPost) {
            this.hasPost = hasPost;
        }
    }

    public class RSRatingPagination{

        private int currentPage;
        private int totalPages;
        private int totalCount;
        private boolean hasNextPage;
        private boolean hasPrevPage;

        public RSRatingPagination() {
        }

        public RSRatingPagination(int currentPage, int totalPages, int totalCount, boolean hasNextPage, boolean hasPrevPage) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalCount = totalCount;
            this.hasNextPage = hasNextPage;
            this.hasPrevPage = hasPrevPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public boolean isHasNextPage() {
            return hasNextPage;
        }

        public void setHasNextPage(boolean hasNextPage) {
            this.hasNextPage = hasNextPage;
        }

        public boolean isHasPrevPage() {
            return hasPrevPage;
        }

        public void setHasPrevPage(boolean hasPrevPage) {
            this.hasPrevPage = hasPrevPage;
        }
    }
}
