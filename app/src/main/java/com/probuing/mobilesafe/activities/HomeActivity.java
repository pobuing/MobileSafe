package com.probuing.mobilesafe.activities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.probuing.mobilesafe.R;
import com.probuing.mobilesafe.utils.Md5Utils;
import com.probuing.mobilesafe.utils.UIUtils;


public class HomeActivity extends Activity {

    private GridView gv_home;
    //首页面的功能名
    private String[] names = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    //首页面的功能名
    private int[] icons = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings};
    private SharedPreferences sp;
    private EditText et_pwd_confirm;
    private EditText et_pwd;
    private Button btn_ok;
    private Button btn_cancel;
    private Button btn_enter_ok;
    private Button btn_enter_cancel;
    private EditText et_enter_pwd;
    private AlertDialog dialog;
    private Context app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        app = HomeActivity.this;
        init();
        setonClick();
    }

    private void init() {
        gv_home = ((GridView) findViewById(R.id.gv_home));
        gv_home.setAdapter(new HomeAdapter());
        sp = getSharedPreferences("config", MODE_PRIVATE);
    }

    /**
     * 判断是否设置过密码
     */
    private boolean isSetupPwd() {
        String password = sp.getString("password", null);
        if (TextUtils.isEmpty(password)) {
            return false;
        } else {
            return true;
        }
    }

    /*设置点击事件的方法*/
    private void setonClick() {
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //判断是否设置过密码，显示输入密码对话框
                        if (isSetupPwd()) {
                            //设置过密码显示输入密码对话框
                            showEnterPwdDialog();
                        } else {
                            //没有设置过密码，显示设置密码对话框
                            showSetupPwdDialog();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 显示设置密码对话框
     */
    private void showSetupPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_setup_pwd, null);
        et_pwd_confirm = ((EditText) view.findViewById(R.id.et_pwd_confirm));
        et_pwd = ((EditText) view.findViewById(R.id.et_pwd));
        btn_ok = ((Button) view.findViewById(R.id.btn_ok));
        btn_cancel = ((Button) view.findViewById(R.id.btn_cancel));
        //设置点击事件
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_pwd.getText().toString().trim();
                String pwd_confirm = et_pwd_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
                    UIUtils.showToast(HomeActivity.this, "密码不能为空");
                    return;
                }

                if (!pwd.equals(pwd_confirm)) {
                    UIUtils.showToast(HomeActivity.this, "两次密码不一致");
                    return;
                }
                SharedPreferences.Editor editor = sp.edit();
                //存储密码
                editor.putString("password", Md5Utils.encode(pwd));
                //提交编辑
                editor.commit();
                //进入下层界面
                jumpActivity(LostFindActivity.class);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        });
        builder.setView(view);
        dialog = builder.show();
    }

    /**
     * 显示输入密码的对话框
     */
    private void showEnterPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_enter_pwd, null);
        btn_enter_ok = ((Button) view.findViewById(R.id.btn_ok));
        btn_enter_cancel = ((Button) view.findViewById(R.id.btn_cancel));
        et_enter_pwd = ((EditText) view.findViewById(R.id.et_enter_pwd));
        //确定按钮的点击事件
        btn_enter_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取用户输入的密码
                String enter_pwd = et_enter_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(enter_pwd)) {
                    UIUtils.showToast(HomeActivity.this, "密码不能为空");
                    return;
                }
                //2.获取用户原来保存的密码
                String savePwd = sp.getString("password", "");

                //3.检查是否一致
                if (Md5Utils.encode(enter_pwd).equals(savePwd)) {
                    //TODO 密码一致进入手机防盗界面
                    jumpActivity(LostFindActivity.class);
                    //隐藏对话框
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } else {
                    UIUtils.showToast(HomeActivity.this, "密码错误");
                }
            }
        });
        btn_enter_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        builder.setView(view);
        dialog = builder.show();
    }

    /**
     * @param cls
     */
    private void jumpActivity(Class<?> cls) {
        Intent intent = new Intent(app, cls);
        startActivity(intent);
    }

    /**
     * 创建GriaView的适配器
     */
    private class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(HomeActivity.this, R.layout.item_home_grid, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((ViewHolder) convertView.getTag());
            }
            holder.tv_home_name.setText(names[position]);
            holder.iv_home_icon.setImageResource(icons[position]);
            return convertView;
        }

        /**
         * 内部容器类
         */
        class ViewHolder {
            private ImageView iv_home_icon;
            private TextView tv_home_name;

            public ViewHolder(View v) {
                iv_home_icon = (ImageView) v.findViewById(R.id.iv_home_icon);
                tv_home_name = ((TextView) v.findViewById(R.id.tv_homeitem_name));
            }
        }
    }
}
