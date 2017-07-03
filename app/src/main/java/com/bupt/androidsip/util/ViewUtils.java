package com.bupt.androidsip.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by xusong on 2017/7/3.
 * About:
 */

public class ViewUtils {
    public static int dp2px(Context context, int dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }
}
