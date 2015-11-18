package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.probuing.mobilesafe.R;

/**
 * 防盗设置的父类的Activity
 * Created by probuing on 2015/11/17.
 */
public abstract class BaseSetupActivity extends Activity {
    public SharedPreferences sp;
    //创建手势识别器
    private GestureDetector mGestyreDetctor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取配置文件的sp
        sp = getSharedPreferences("config", MODE_PRIVATE);
        //初始化手势识别器
        mGestyreDetctor = new GestureDetector(BaseSetupActivity.this, new GestureDetector.SimpleOnGestureListener(){
            /**
             *
             * @param e1 手指第一次触屏的事件
             * @param e2 手指离开屏幕一瞬间的事件
             * @param velocityX 水平方向的速度 px/s
             * @param velocityY 竖直方向的速度 px/s
             * @return true表示消费掉了 false表示接收并且处理触摸事件
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(Math.abs(velocityX)<200){
                    Log.i("MotionEvent","无效动作");
                    return true;

                }
                //根据两个触屏事件的计算，如果e2-e1>200 说明从左向右
                //e2<e1 说明从右向左
                if(e2.getRawX()-e1.getRawX()>200)
                {
                    //显示上一个界面
                    showPre();
                    overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
                    return true;
                }
                if(e1.getRawX()-e2.getRawX()>200)
                {
//                    显示下一个界面
                    showNext();
                    overridePendingTransition(R.anim.next_in, R.anim.next_out);
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 获取触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //分析手势事件，判断激活哪种手势
        mGestyreDetctor.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 抽象的下一步按钮点击事件
     *
     * @param v
     */
    public  void next(View v){
        showNext();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    protected abstract void showNext();


    /**
     * 抽象的上一步按钮点击事件
     *
     * @param v
     */
    public  void pre(View v){
        showPre();
        //Activity播放指定的动画
        overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
    }

    protected abstract void showPre();

    ;

    /**
     * 开启新的Activity并且关闭自己
     *
     * @param cls
     */
    public void startActivityAndFinishSelf(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        finish();

    }


    /**
     * 开启新的Activity
     *
     * @param cls
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

}
