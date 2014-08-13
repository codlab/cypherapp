package eu.codlab.cyphersend.ui.view.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kevinleperf on 11/08/14.
 */
public class SwipableViewPager extends ViewPager {
    private boolean _is_swipe_enabled;

    public SwipableViewPager(Context context) {
        super(context);
    }

    public SwipableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._is_swipe_enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this._is_swipe_enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this._is_swipe_enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    /**
     * Custom method to enable or disable swipe
     *
     * @param is_swipe_enabled true to enable swipe, false otherwise
     */
    public void setPagingEnabled(boolean is_swipe_enabled) {
        _is_swipe_enabled = is_swipe_enabled;
    }
}
