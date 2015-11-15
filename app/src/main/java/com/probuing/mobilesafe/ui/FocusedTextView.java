package com.probuing.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自定义TextView
 * Created by probuing on 2015/11/16.
 */
public class FocusedTextView extends TextView {
    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //判断是否获取焦点
    //重写获取焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
