package modules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConverter {

    public static String convertToHCM(String isoDateString) {
        // Định dạng chuỗi nguồn (ISO 8601)
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // ISO mặc định ở UTC

        // Định dạng chuỗi đích (dd/MM/yyyy HH:mm)
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        targetFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")); // Múi giờ HCM

        try {
            // Parse chuỗi ISO 8601 thành Date
            Date date = isoFormat.parse(isoDateString);

            // Định dạng lại Date thành chuỗi mong muốn
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Trả về null nếu chuỗi không hợp lệ
        }
    }

    public static void main(String[] args) {
        String isoDateString = "2024-12-01T19:00:00.000Z";
        String formattedDate = convertToHCM(isoDateString);

        System.out.println("Ngày giờ đã chuyển đổi: " + formattedDate);
    }
}
