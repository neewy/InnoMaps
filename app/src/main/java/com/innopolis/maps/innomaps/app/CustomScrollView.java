package com.innopolis.maps.innomaps.app;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;


public class CustomScrollView extends NestedScrollView {

    private OnVisibilityChangedListener mVisibilityListener;

    public interface OnVisibilityChangedListener {
        void visibilityChanged(int visibility);
    }

    public void setVisibilityListener(OnVisibilityChangedListener listener) {
        this.mVisibilityListener = listener;
    }

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (mVisibilityListener != null)
            mVisibilityListener.visibilityChanged(visibility);
    }
}
