package com.bfmj.sdk.util;

import android.content.Context;

/**
 * Created by wangfuzheng on 2015/12/8.
 */
public class DimenUtils {
    public static int getPx(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp);
    }
}
