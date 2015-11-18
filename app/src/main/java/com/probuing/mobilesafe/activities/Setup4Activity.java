package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.probuing.mobilesafe.R;

public class Setup4Activity extends BaseSetupActivity {

    private Class<Setup3Activity> pre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        pre = Setup3Activity.class;
    }

    @Override
    protected void showNext() {
        startActivityAndFinishSelf(LostFindActivity.class);
    }

    @Override
    protected void showPre() {
        startActivityAndFinishSelf(pre);
    }


}
