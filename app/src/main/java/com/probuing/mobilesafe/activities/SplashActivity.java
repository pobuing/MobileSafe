package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.probuing.mobilesafe.R;
import com.probuing.mobilesafe.utils.StreamUtils;
import com.probuing.mobilesafe.utils.UIUtils;

import org.json.JSONObject;

import java.io.File;
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
                            //下载apk方法
                            downLoad(downloadurl);
                        }
                    });
                    builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            msg.what = LOAD_MAINUI;
                            loadMainUI();
                        }
                    });
                    //处理返回键
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            //点击返回键进入主界面
                            loadMainUI();
                        }
                    });
                    builder.show();
                    break;
            }
        }
    };
    private TextView tv_info;

    private void downLoad(String downLoadUrl) {
        HttpUtils utils = new HttpUtils();
        utils.download(downLoadUrl, "/mnt/sdcard/temp.apk", new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
//                <action android:name="android.intent.action.VIEW" />
//                <category android:name="android.intent.category.DEFAULT" />
//                <data android:scheme="content" />
//                <data android:scheme="file" />
//                <data android:mimeType="application/vnd.android.package-archive" />
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.apk")), "application/vnd.android.package-archive");
                startActivityForResult(intent,0);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                UIUtils.showToast(SplashActivity.this, "下载失败");
                System.out.println("错误信息为" + s);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                tv_info.setText(current + "/" + total);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0)
        {
            //安装界面的执行
            loadMainUI();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
        tv_info = ((TextView) findViewById(R.id.tv_splash_info));
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
