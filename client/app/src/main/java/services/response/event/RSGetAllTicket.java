package services.response.event;

import java.util.List;

public class RSGetAllTicket {
    private List<LocationInfo> data;

    public RSGetAllTicket() {
    }

    public RSGetAllTicket(List<LocationInfo> data) {
        this.data = data;
    }

    public List<LocationInfo> getData() {
        return data;
    }

    public void setData(List<LocationInfo> data) {
        this.data = data;
    }

    public static class LocationInfo{
        private List<TypeInfo> types;
        private String location;

        public LocationInfo() {
        }

        public LocationInfo(List<TypeInfo> types, String location) {
            this.types = types;
            this.location = location;
        }

        public List<TypeInfo> getTypes() {
            return types;
        }

        public void setTypes(List<TypeInfo> types) {
            this.types = types;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static class TypeInfo{
        private String type;
        private List<TicketInfo> tickets;
        private long price;

        public TypeInfo() {
        }

        public TypeInfo(String type, List<TicketInfo> tickets, long price) {
            this.type = type;
            this.tickets = tickets;
            this.price = price;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<TicketInfo> getTickets() {
            return tickets;
        }

        public void setTickets(List<TicketInfo> tickets) {
            this.tickets = tickets;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }
    }

    public static class TicketInfo{

        private String _id;
        private int position;
        private String desc;
        private boolean isAvailable;
        private String accBuy;

        public TicketInfo() {
        }

        public TicketInfo(String _id, int position, String desc, boolean isAvailable, String accBuy) {
            this._id = _id;
            this.position = position;
            this.desc = desc;
            this.isAvailable = isAvailable;
            this.accBuy = accBuy;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public void setAvailable(boolean available) {
            isAvailable = available;
        }

        public String getAccBuy() {
            return accBuy;
        }

        public void setAccBuy(String accBuy) {
            this.accBuy = accBuy;
        }
    }

}
