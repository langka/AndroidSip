package com.bupt.androidsip.customview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.bupt.androidsip.R;


public class RoundRelativeLayout extends RelativeLayout {
    private final Path clipPath = new Path();
    private final Path borderPath = new Path();

    private int borderColor;
    private float borderWidth;
    private float radius;

    public RoundRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        initCustomAttrs(context, attrs);
    }

    public RoundRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initCustomAttrs(context, attrs);
    }

    public RoundRelativeLayout(Context context) {
        super(context);
        init();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        clipPath.reset();
//        float insideRadius = Math.max(0, radius - borderWidth);
//        float halfBorderWidth = borderWidth / 2;
//        clipPath.addRoundRect(new RectF(borderWidth / 2 + 0.5f, borderWidth / 2 + 0.5f, w - borderWidth - 1, h - borderWidth - 1), insideRadius, insideRadius, Direction.CW);
        borderPath.reset();
        borderPath.addRoundRect(new RectF(borderWidth / 2, borderWidth / 2, w - borderWidth, h - borderWidth), radius, radius, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);

        // clip child view
        canvas.save();
        canvas.clipPath(borderPath);
        super.dispatchDraw(canvas);
        canvas.restore();

        // border
        p.setStrokeWidth(borderWidth);
        p.setColor(borderColor);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawPath(borderPath, p);
    }

    @SuppressLint("NewApi")
    private void init() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // we have to remove the hardware acceleration if we want the clip
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray att = context.obtainStyledAttributes(attrs, R.styleable.RoundRelativeLayout);
        radius = att.getDimension(R.styleable.RoundRelativeLayout_radius, 0);
        borderColor = att.getColor(R.styleable.RoundRelativeLayout_borderColor, 0);
        borderWidth = att.getDimension(R.styleable.RoundRelativeLayout_rBorderWidth, 0);
    }
}
