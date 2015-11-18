package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.probuing.mobilesafe.R;

public class LostFindActivity extends Activity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到系统的设置
        sp = getSharedPreferences("config",MODE_PRIVATE);
        if (isFinishSetup()){
        setContentView(R.layout.activity_lost_find);

        }else{
            //没有设置过，进入设置向导界面
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //关闭当前手机防盗界面
            finish();
        }
    }

    /**
     * 判断用户是否设置过完成向导
     * @return
     */
    private boolean isFinishSetup() {
        return sp.getBoolean("finishsetup",false);
    }
    /**
     * 重新进入设置向导
     */
    public void reEntrySetup(View v)
    {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        //关闭当前手机防盗界面
        finish();
    }
}
