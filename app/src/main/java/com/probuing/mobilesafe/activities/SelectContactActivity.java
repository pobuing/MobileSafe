package com.probuing.mobilesafe.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.probuing.mobilesafe.R;
import com.probuing.mobilesafe.domain.ContactInfo;
import com.probuing.mobilesafe.engine.ContactInfoParser;

import java.util.List;

public class SelectContactActivity extends Activity {
    private List<ContactInfo> infos;
    private ListView lv;
    private ContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        infos = ContactInfoParser.findAll(this);
        lv = ((ListView) findViewById(R.id.lv_contacts));
        adapter = new ContactsAdapter();
        lv.setAdapter(adapter);
        //设置ListView点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("phone",infos.get(position).getPhone());
                setResult(0,data);
                finish();
            }
        });
    }
    /**
     * 适配器内部类
     * */
    private class ContactsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return infos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null){
                //布局转化为视图
                convertView=View.inflate(SelectContactActivity.this,R.layout.item_contact,null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            ContactInfo contactInfo = infos.get(position);
            holder.tv_item_name.setText(contactInfo.getName());
            holder.tv_item_phone.setText(contactInfo.getPhone());
            return convertView;
        }
        //内部容器类
        private class ViewHolder{
            private final TextView tv_item_name;
            private final TextView tv_item_phone;

            public ViewHolder(View v) {
                tv_item_name = ((TextView) v.findViewById(R.id.tv_item_name));
                tv_item_phone = ((TextView) v.findViewById(R.id.tv_item_phone));
            }
        }
    }
}
