package services.response.event;

import java.util.List;

import model.event.DateRange;
import model.event.Event;
import model.event.PriceRange;

public class RSGetAllEvent {
    private String message;
    private int length;
    private List<REvent> data;

    public RSGetAllEvent() {
    }

    public List<REvent> getData() {
        return data;
    }

    public void setData(List<REvent> data) {
        this.data = data;
    }

    public RSGetAllEvent(String message, int length, List<REvent> data) {
        this.message = message;
        this.length = length;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }


    public static class REvent {
        private String _id;
        private String name;

        private DateRange date;
        private String location;

        public REvent(String _id, String name, DateRange date,String location) {
            this._id = _id;
            this.name = name;
            this.date = date;
            this.location = location;
        }
        public REvent() {
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
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

        public DateRange getDate() {
            return date;
        }

        public void setDate(DateRange date) {
            this.date = date;
        }
    }
}
