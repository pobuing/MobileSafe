package com.probuing.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * UI工具类
 * Created by probuing on 2015/11/15.
 */
public class UIUtils {

    public static void showToast(final Activity context, final String msg) {
        //判断是否在Ui线程中
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            //不在ui线程中，需要在主线程中进行ui
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
