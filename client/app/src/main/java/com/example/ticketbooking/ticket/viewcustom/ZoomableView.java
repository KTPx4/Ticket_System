package com.example.ticketbooking.ticket.viewcustom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

public class ZoomableView extends ViewGroup {

    private float scale = 1f; // Mức zoom mặc định
    private ScaleGestureDetector scaleDetector;
    private float lastTouchX, lastTouchY;
    private float focusX = 0, focusY = 0; // Tọa độ kéo (pan)
    private float minScale = 0.6f, maxScale = 2.5f; // Giới hạn zoom
    private float maxPanX = 1200f, maxPanY = 900f; // Phạm vi kéo tối đa

    public ZoomableView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Chặn các sự kiện touch cho các View con khi zoom hoặc pan
        return scaleDetector.isInProgress() || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event); // Xử lý zoom

        // Xử lý kéo (pan)
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = event.getX();
                lastTouchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - lastTouchX;
                float dy = event.getY() - lastTouchY;

                // Cập nhật tọa độ kéo
                focusX += dx;
                focusY += dy;

                // Điều chỉnh phạm vi kéo
                focusX = Math.max(Math.min(focusX, getWidth() - getWidth() * scale + maxPanX), -maxPanX);
                focusY = Math.max(Math.min(focusY, getHeight() - getHeight() * scale + maxPanY), -maxPanY);

                lastTouchX = event.getX();
                lastTouchY = event.getY();

                invalidate(); // Vẽ lại
                break;
        }
        return true;
    }



    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();

        // Áp dụng zoom và kéo
        canvas.scale(scale, scale);
        canvas.translate(focusX / scale, focusY / scale);

        super.dispatchDraw(canvas);

        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Điều chỉnh tọa độ touch theo zoom và pan
        float adjustedX = (ev.getX() - focusX) / scale;
        float adjustedY = (ev.getY() - focusY) / scale;

        // Thay đổi tọa độ của sự kiện trước khi chuyển đến View con
        ev.setLocation(adjustedX, adjustedY);

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            // Layout mỗi view con từ góc trên cùng bên trái
            child.layout(0, 0, childWidth, childHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Đo lường tất cả các view con
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            scale = Math.max(minScale, Math.min(scale, maxScale)); // Giới hạn zoom
            invalidate();
            return true;
        }
    }
}
