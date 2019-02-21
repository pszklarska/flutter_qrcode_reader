package com.matheusvillela.flutter.plugins.qrcodereader;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

public class OverlayView extends View {

    private int backgroundColor = Color.parseColor("#c718124f");
    private int lineColor = Color.parseColor("#6cfcff");

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private int left;
    private int top;
    private int right;
    private int bottom;
    private int scannerLineHeight;

    private ValueAnimator animator;

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        left = 50;
        right = screenWidth - 50;

        int rectWidth = right - left;
        top = screenHeight / 2 - rectWidth / 2;
        bottom = top + rectWidth;
        scannerLineHeight = screenHeight / 2;

        postInvalidate();
        initAnimation();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (left != 0 && top != 0) {
            paint.setColor(backgroundColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);

            paint.setXfermode(xfermode);
            canvas.drawRect(left, top, right, bottom, paint);

            paint.setXfermode(null);
            paint.setColor(lineColor);
            paint.setStrokeWidth(5f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            int lineLength = 50;
            canvas.drawLine(left, top, left + lineLength, top, paint);
            canvas.drawLine(left, top, left, top + lineLength, paint);
            canvas.drawLine(right, top, right - lineLength, top, paint);
            canvas.drawLine(right, top, right, top + lineLength, paint);
            canvas.drawLine(left, bottom, left + lineLength, bottom, paint);
            canvas.drawLine(left, bottom, left, bottom - lineLength, paint);
            canvas.drawLine(right, bottom, right - lineLength, bottom, paint);
            canvas.drawLine(right, bottom, right, bottom - lineLength, paint);

            paint.setStrokeWidth(7f);
            canvas.drawLine(left + 20, scannerLineHeight, right - 20, scannerLineHeight, paint);
        }
    }


    void initAnimation() {
        animator = ValueAnimator.ofInt(top + 20, bottom - 20);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scannerLineHeight = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(3000);
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
        }
    }
}
