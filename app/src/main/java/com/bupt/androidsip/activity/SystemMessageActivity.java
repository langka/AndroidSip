package com.bupt.androidsip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.sip.SipSystemMessage;
import com.bupt.androidsip.mananger.DBManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WHY on 2017/7/12.
 */

public class SystemMessageActivity extends BaseActivity {


    @BindView(R.id.activity_system_message)
    ListView messageList;

    List<SipSystemMessage> list = null;


    public static void Start(Context context) {
        Intent intent = new Intent(context, SystemMessageActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);
        ButterKnife.bind(this);
        getHeaderDivider().setVisibility(View.GONE);
        enableLeftImage(R.drawable.ic_arrow_back_24px, e -> finish());
        setTitle("系统消息");
        list = new ArrayList<>();
        SipSystemMessage jiashuju1 = new SipSystemMessage();
        jiashuju1.jiashuju(1,"1",5,0,0);
        SipSystemMessage jiashuju2 = new SipSystemMessage();
        jiashuju2.jiashuju(1,"2",5,0,0);
        SipSystemMessage jiashuju3 = new SipSystemMessage();
        jiashuju3.jiashuju(0,"3",5,0,0);
        SipSystemMessage jiashuju4 = new SipSystemMessage();
        jiashuju4.jiashuju(0,"4",5,0,0);
        // TODO: 2017/7/13 数据库访问有问题
//        DBManager.getInstance(this).saveEvent(jiashuju1);
//        DBManager.getInstance(this).saveEvent(jiashuju2);
//        DBManager.getInstance(this).saveEvent(jiashuju3);
//        DBManager.getInstance(this).saveEvent(jiashuju4);
        list.addAll(DBManager.getInstance(this).getAllSystemEvents());
        //list.add(jiashuju1);list.add(jiashuju2);list.add(jiashuju3);list.add(jiashuju4);
        messageList.setAdapter(new SystemMessageAdapter(this, R.layout.activity_system_message, list));
    }


    private static class SystemMessageAdapter extends ArrayAdapter<SipSystemMessage> {

        private List<SipSystemMessage> list;
        private int resourceId;
        private Context context;

        public SystemMessageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
            super(context, resource, objects);
            this.list = objects;
            this.resourceId = resource;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final SipSystemMessage sipSystemMessage = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_system_message, null);
                holder = new ViewHolder();
                holder.head = (ImageView) convertView.findViewById(R.id.item_system_message_head);
                holder.type = (TextView) convertView.findViewById(R.id.item_system_message_name);
                holder.contain = (TextView) convertView.findViewById(R.id.item_system_message_desc);
                holder.acc = (ImageView) convertView.findViewById(R.id.item_system_message_acc);
                holder.refuse = (ImageView) convertView.findViewById(R.id.item_system_message_refuse);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.contain.setText(sipSystemMessage.assoicatedUser.name + "请求添加你为好友");
            if(sipSystemMessage.state == 1) {
                holder.type.setText("好友请求");
                holder.acc.setVisibility(View.VISIBLE);
                holder.refuse.setVisibility(View.VISIBLE);
                holder.acc.setOnClickListener(view -> {
                    sipSystemMessage.state = 0;
                    sipSystemMessage.type = 1;
                    // TODO: 2017/7/13 成功的回调
                    this.notifyDataSetChanged();
                });
                holder.refuse.setOnClickListener(view -> {
                    sipSystemMessage.state = 0;
                    sipSystemMessage.type = 2;
                    // TODO: 2017/7/13 拒绝的回调

                    this.notifyDataSetChanged();
                });

            }else {
                holder.type.setText("已处理");
                holder.acc.setVisibility(View.INVISIBLE);
                holder.refuse.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }


        static class ViewHolder {
            ImageView head;
            TextView type;
            TextView contain;
            ImageView refuse;
            ImageView acc;
        }

    }
}