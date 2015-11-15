package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.probuing.mobilesafe.R;
import com.probuing.mobilesafe.utils.StreamUtils;
import com.probuing.mobilesafe.utils.UIUtils;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends Activity {

    private static final int LOAD_MAINUI = 1;
    private static final int SHOW_UPDATE_DIALOG = 2;
    private TextView tv_splash_version;
    private PackageManager packageManager;
    //客户端versionCode
    private int clientversionCode;
    //服务器的versionCode
    private int serviceVersionCode;
    //描述信息
    private String desc;
    //下载地址
    private String downloadurl;

    //消息处理器
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case LOAD_MAINUI:
                    loadMainUI();
                    break;
                case SHOW_UPDATE_DIALOG:
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("更新提醒");
                    builder.setMessage(desc);
                    builder.setPositiveButton("立刻更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("下载" + downloadurl);
                        }
                    });
                    builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            msg.what = LOAD_MAINUI;
                            handler.sendMessage(msg);
                        }
                    });
                    builder.show();
                    break;
            }
        }
    };

    private void loadMainUI() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_splash_version = ((TextView) findViewById(R.id.tv_splash_version));
        //创建包管理器
        packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            clientversionCode = packageInfo.versionCode;
            //设置版本信息到界面上
            tv_splash_version.setText(versionName);
            checkVersion();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接服务器检查版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //检查执行时间，用于判断启动页面的显示时间
                    URL url = new URL(getResources().getString(R.string.serverurl));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //设置访问方式
                    conn.setRequestMethod("GET");
                    //超时时间
                    conn.setConnectTimeout(5000);
                    //设置响应的结果码
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        InputStream is = conn.getInputStream();
                        String json = StreamUtils.readStream(is);
                        if (TextUtils.isEmpty(json)) {
                            //服务器解析json失败
                            UIUtils.showToast(SplashActivity.this, "失败");
                        } else {
                            JSONObject jsobj = new JSONObject(json);
                            downloadurl = jsobj.getString("downloadurl");
                            serviceVersionCode = jsobj.getInt("version");
                            desc = jsobj.getString("desc");
                            //判断versionCode是否相同
                            if (clientversionCode == serviceVersionCode) {
                                //相同进入主界面
                                msg.what = LOAD_MAINUI;
                            } else {
                                //不同
                                //相同进入主界面
                                msg.what = SHOW_UPDATE_DIALOG;
                            }
                        }
                    } else {
                        UIUtils.showToast(SplashActivity.this,
                                "错误2015, 服务器状态码错误,请联系客服");
                        msg.what = LOAD_MAINUI;
                    }

                } catch (Exception e) {
                    msg.what = LOAD_MAINUI;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    long dtime = endTime - startTime;
                    if (dtime > 2000) {
                        handler.sendMessage(msg);
                    } else {
                        handler.sendMessageDelayed(msg, (2000 - dtime));
                    }
                }
            }
        }.start();
    }
}
