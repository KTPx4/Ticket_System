package model.event;

import modules.DateConverter;

public class DateRange {
    private String start;
    private String end;

    public DateRange() {
    }

    public DateRange(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return DateConverter.convertToHCM(start);
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return DateConverter.convertToHCM(end);
//        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
