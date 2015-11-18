package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.probuing.mobilesafe.R;

import org.w3c.dom.Text;

public class Setup2Activity extends BaseSetupActivity {

    //上一个activity
    private Class<Setup1Activity> pre;
    private Class<Setup3Activity> next;
    private TelephonyManager tm;
    private ImageView iv_setup2_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        pre = Setup1Activity.class;
        next = Setup3Activity.class;
        iv_setup2_status = ((ImageView) findViewById(R.id.iv_setup2_status));
        //获取电话管理器
        tm = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        String savedSim = sp.getString("sim", null);
        //回显绑定信息
        if (TextUtils.isEmpty(savedSim)) {
            iv_setup2_status.setImageResource(R.drawable.unlock);
        } else {
            iv_setup2_status.setImageResource(R.drawable.lock);
        }
    }

    @Override
    protected void showNext() {
        String savedSim = sp.getString("sim", null);
        if (TextUtils.isEmpty(savedSim)) {
            Toast.makeText(Setup2Activity.this, "请先绑定sim卡", Toast.LENGTH_SHORT).show();
            return;
        } else {
            startActivityAndFinishSelf(next);
        }
    }

    @Override
    protected void showPre() {
        startActivityAndFinishSelf(pre);
    }

    /**
     * 绑定或者解绑Sim卡
     *
     * @param v
     */
    public void bindUnbindSim(View v) {
        //判断是否绑定过
        String savedSim = sp.getString("sim", null);
        SharedPreferences.Editor edit = sp.edit();
        if (TextUtils.isEmpty(savedSim)) {
            //获取sim卡的序列号
            String simserial = tm.getSimSerialNumber();
            //存储序列号到sharedpreference
            edit.putString("sim", simserial);
            edit.commit();
            Toast.makeText(Setup2Activity.this, "绑定成功", Toast.LENGTH_SHORT).show();
            //设置图标
            iv_setup2_status.setImageResource(R.drawable.lock);
        } else {
            edit.putString("sim", null);
            edit.commit();
            Toast.makeText(Setup2Activity.this, "解绑成功", Toast.LENGTH_SHORT).show();
            iv_setup2_status.setImageResource(R.drawable.unlock);
        }
    }

}
