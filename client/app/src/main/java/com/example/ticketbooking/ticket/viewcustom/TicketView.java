package com.example.ticketbooking.ticket.viewcustom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.ticketbooking.R;

public class TicketView extends FrameLayout {
    private Paint paint;
    private Path path;
    public TicketView(Context context) {
        super(context);
        init();
    }
    private void init() {
        // Tùy chỉnh các logic khởi tạo view ở đây nếu cần
//        inflate(getContext(), R.layout.ticket_view_layout, this);

    }
    public TicketView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Thiết lập Paint
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(context.getColor(R.color.back_Ticket)); // Màu nền
//        paint.setColor(Color.BLUE); // Màu nền
        paint.setStyle(Paint.Style.FILL);

        // Khởi tạo Path
        path = new Path();

        // Đảm bảo con sẽ được vẽ
        setWillNotDraw(false);
    }
    public void setFullBackgroundColor(int color) {
        paint.setColor(color); // Cập nhật màu nền
        invalidate(); // Yêu cầu vẽ lại view
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();
        float radius = 50f; // Bán kính của lỗ tròn

        // Vẽ hình chữ nhật với 4 góc vuông
        path.reset();
        path.addRect(0, 0, width, height, Path.Direction.CW);

        // Khoét lỗ tròn ở 2 bên
        path.addCircle(0, height / 2, radius, Path.Direction.CCW); // Lỗ tròn bên trái
//        path.addCircle(width, height / 2, radius, Path.Direction.CCW); // Lỗ tròn bên phải

        canvas.drawPath(path, paint);
    }
}
