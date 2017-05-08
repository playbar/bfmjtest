package com.baofeng.mj.util.publicutil;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Created by hanyang on 2016/5/22.
 * 次数转换
 */
public class NumFormatUtil {
    /**
     * 游戏下载次数转换
     */
    public static String formatCount(String countStr) {
        if (TextUtils.isEmpty(countStr)) {
            return "0";
        }
        int count = 0;
        try {
            count = Integer.parseInt(countStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return formatCount(count);
    }

    /**
     * 游戏下载次数转换
     */
    public static String formatCount(int count) {
        String res = "";
        double b = 10000 * 10000f;
        double h = 10000f;
        double c = (double) count;
        DecimalFormat df = new DecimalFormat("0.00");

        if (c >= b) {
            res = df.format(c / b).replace(".00", "") + "亿";
        } else if (c >= h) {
            res = df.format(c / h).replace(".00", "") + "万";
        } else {
            res += count + "";
        }

        return res;
    }

}
