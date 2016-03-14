package com.innopolis.maps.innomaps.bottomview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.innopolis.maps.innomaps.R;

/**
 * Created by Nikolay on 13.03.2016.
 */
public class MapBottomView extends View {

    private int titleColor;
    private int counter;
    private int alpha;
    private String titleText;
    private String descText;
    private Paint paint;
    Handler mHandler;

    public MapBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        mHandler = new Handler();
        counter = 0;
        alpha = 0;
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.MapBottomView, 0, 0);
        try {
            titleText = (array.getString(R.styleable.MapBottomView_titleText) == null) ? "" : array.getString(R.styleable.MapBottomView_titleText);
            descText = (array.getString(R.styleable.MapBottomView_descText) == null) ? "" : array.getString(R.styleable.MapBottomView_descText);
            titleColor = array.getInteger(R.styleable.MapBottomView_titleColor, 0);
        } finally {
            array.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Rect bounds = new Rect(canvas.getClipBounds());
        int endY = bounds.bottom - counter;
        Rect rect = new Rect();
        rect.set(bounds.left, endY, bounds.right, bounds.bottom);
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawRect(rect, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(35);
        paint.setARGB(alpha, 0, 0, 0);
        canvas.drawText(titleText, 20, endY + 60, paint);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(20);
        canvas.drawText(descText, 20, endY + 120, paint);
        if (counter < bounds.bottom) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    counter+=10;
                    if (alpha < 245) alpha +=10;
                    invalidate();
                }
            }, 1L);
        } else {
            alpha = 255;
            invalidate();
        }
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        invalidate();
        requestLayout();
    }

    public String getDescText() {
        return descText;
    }

    public void setDescText(String descText) {
        this.descText = descText;
        invalidate();
        requestLayout();
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        invalidate();
        requestLayout();
    }
}
