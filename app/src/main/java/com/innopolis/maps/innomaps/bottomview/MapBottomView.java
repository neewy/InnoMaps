package com.innopolis.maps.innomaps.bottomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

/**
 * Created by Nikolay on 13.03.2016.
 */
public class MapBottomView extends View {

    public boolean isShown = false;
    private int titleColor;
    private int counter;
    private int alpha;
    private Context context;
    private String titleText;
    private String descText;
    private Paint paint;
    private Canvas canvas;
    private Handler mHandler;
    private Bitmap closeIcon;

    public MapBottomView(Context context) {
        super(context);
        this.context = context;
        paint = new Paint();
        mHandler = new Handler();
        counter = 0;
        alpha = 0;
        titleText = "";
        descText = "";
        titleColor = 0;
        Drawable drawable = getResources().getDrawable(R.drawable.ic_clear_24dp);
        closeIcon = Utils.drawableToBitmap(drawable);
    }

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
        Drawable drawable = getResources().getDrawable(R.drawable.ic_clear_24dp);
        closeIcon = Utils.drawableToBitmap(drawable);
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
        canvas.drawBitmap(closeIcon, bounds.right - 70, endY + 35, paint);
        if (counter < bounds.bottom) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    counter += 10;
                    if (alpha < 245) alpha += 10;
                    invalidate();
                }
            }, 1L);
        } else {
            this.canvas = canvas;
            isShown = true;
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

    private String TAG = MapBottomView.class.getSimpleName();
    float initialX, initialY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //mGestureDetector.onTouchEvent(event);

        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            Rect bounds = new Rect(canvas.getClipBounds());
            initialX = event.getX();
            initialY = event.getY();
            if (initialX > bounds.right - 100 && initialX < bounds.right && initialY > bounds.top && initialY < bounds.top + 100) {
                Toast.makeText(context, "Close!", Toast.LENGTH_LONG).show();
            }
        }
        return super.onTouchEvent(event);
    }


}
