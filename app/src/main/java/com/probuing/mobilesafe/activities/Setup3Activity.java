package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.probuing.mobilesafe.R;

public class Setup3Activity extends BaseSetupActivity {
    private Class<Setup2Activity> pre;
    private Class<Setup4Activity> next;
    private EditText et_setup3_phone;

    //上一个
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        et_setup3_phone = ((EditText) findViewById(R.id.et_setup3_phone));
        pre = Setup2Activity.class;
        next = Setup4Activity.class;
    }

    @Override
    protected void showNext() {
        startActivityAndFinishSelf(next);
    }

    @Override
    protected void showPre() {
        startActivityAndFinishSelf(pre);
    }

    /**
     * 选择联系人按钮点击事件
     * @param v
     */
    public void selectContact(View v)
    {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0)
        {
            String phone = data.getStringExtra("phone");
            if(!TextUtils.isEmpty(phone))
            {
                et_setup3_phone.setText(phone);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
