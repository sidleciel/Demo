package com.rhodes.demo.wrapper;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;

/**
 * Created by xiet on 2015/6/10.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class Wrapper12 {
    public static float getAxisValue(MotionEvent event, int axis) {
        return event.getAxisValue(axis);
    }
}
