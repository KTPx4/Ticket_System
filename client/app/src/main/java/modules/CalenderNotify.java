package modules;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalenderNotify {
    private Context context;

    public CalenderNotify(Context context) {
        this.context = context;
    }

    // Chuyển đổi chuỗi thời gian sang milliseconds
    private long parseStartEventToMillis(String startEvent) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date date = dateFormat.parse(startEvent);
            if (date != null) {
                return date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không thể phân tích thời gian
    }

    // Hàm thêm hoặc cập nhật sự kiện
    public void addOrUpdateEvent(String eventId, String eventName, String startEvent) {
        // Chuyển đổi startEvent thành milliseconds
        long startTimeMillis = parseStartEventToMillis(startEvent);

        // Kiểm tra nếu thời gian không hợp lệ
        if (startTimeMillis == -1) {
            Log.d("CalendarEvent", "Thời gian sự kiện không hợp lệ!");
            return;
        }

        ContentResolver cr = context.getContentResolver();
        Uri eventsUri = CalendarContract.Events.CONTENT_URI;

        // Kiểm tra sự tồn tại của sự kiện dựa trên TITLE và DTSTART
        String selection = CalendarContract.Events.TITLE + " = ? AND " + CalendarContract.Events.DTSTART + " = ?";
        String[] selectionArgs = new String[]{eventName, String.valueOf(startTimeMillis)};
        Cursor cursor = cr.query(eventsUri, null, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Sự kiện đã tồn tại, kiểm tra hoặc thêm nhắc nhở
            @SuppressLint("Range") long existingEventId = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events._ID));
            checkOrAddReminder(cr, String.valueOf(existingEventId));
            cursor.close();
            return;
        }

        // Nếu sự kiện không tồn tại, thêm mới
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // Lịch mặc định
        values.put(CalendarContract.Events.TITLE, eventName);
        values.put(CalendarContract.Events.DESCRIPTION, "Sự kiện tự động từ ứng dụng");
        values.put(CalendarContract.Events.DTSTART, startTimeMillis);
        values.put(CalendarContract.Events.DTEND, startTimeMillis + 60 * 60 * 1000); // Kéo dài 1 giờ
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Ho_Chi_Minh");

        Uri eventUri = cr.insert(eventsUri, values);
        if (eventUri != null) {
            // Sau khi thêm, lấy _ID từ URI để thêm nhắc nhở
            long eventIdGenerated = Long.parseLong(eventUri.getLastPathSegment());
            addReminder(cr, String.valueOf(eventIdGenerated), 12 * 60); // Nhắc trước 12 giờ
            Log.d("CalendarEvent", "Thêm sự kiện thành công: " + eventUri.toString());
        } else {
            Log.d("CalendarEvent", "Thêm sự kiện thất bại.");
        }

        if (cursor != null) cursor.close();
    }

    // Kiểm tra hoặc thêm nhắc nhở cho sự kiện
    private void checkOrAddReminder(ContentResolver cr, String eventId) {
        Uri remindersUri = CalendarContract.Reminders.CONTENT_URI;

        // Kiểm tra xem đã có nhắc nhở hay chưa
        String selection = CalendarContract.Reminders.EVENT_ID + " = ?";
        String[] selectionArgs = new String[]{eventId};
        Cursor cursor = cr.query(remindersUri, null, selection, selectionArgs, null);

        if (cursor == null || !cursor.moveToFirst()) {
            // Không có nhắc nhở, thêm mới
            addReminder(cr, eventId, 12 * 60);
        }

        if (cursor != null) cursor.close();
    }

    // Thêm nhắc nhở cho sự kiện
    private void addReminder(ContentResolver cr, String eventId, int minutesBefore) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, eventId);
        values.put(CalendarContract.Reminders.MINUTES, minutesBefore);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        Uri reminderUri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        if (reminderUri != null) {
            Log.d("CalendarEvent", "Thêm nhắc nhở thành công: " + reminderUri.toString());
        } else {
            Log.d("CalendarEvent", "Thêm nhắc nhở thất bại.");
        }
    }

    // Hàm kiểm tra thêm sự kiện test
    public void testAddEvent() {
        ContentResolver cr = context.getContentResolver();
        long startTime = System.currentTimeMillis() + 3600000; // 1 giờ sau hiện tại
        long endTime = startTime + 3600000; // Kéo dài 1 giờ

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // Lịch mặc định
        values.put(CalendarContract.Events.TITLE, "Sự kiện kiểm tra");
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Ho_Chi_Minh");

        Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        if (eventUri != null) {
            Log.d("CalendarEvent", "Thêm sự kiện kiểm tra thành công với URI: " + eventUri.toString());
        } else {
            Log.d("CalendarEvent", "Thêm sự kiện kiểm tra thất bại.");
        }
    }
}
