package com.bupt.androidsip.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.mananger.UserManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xusong on 2017/7/2.
 * About:
 */

public class MessageFragment extends BaseFragment {

    @BindView(R.id.frag_chat_my_head)
    ImageView headImage;
    @BindView(R.id.frag_friend_list)
    ListView listView;

    User user;

    private List<Chat> chatList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_chat_list, null);
        ButterKnife.bind(this, v);


        initdata();

       ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), R.layout.item_frag_chat_list, chatList);

        listView.setAdapter(chatListAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: 2017/7/1 触发点击

        });
        return v;

    }

    void initdata() {
        user = UserManager.getInstance().getUser();

        // TODO: 2017/7/1 动态展示用户头像
        switch (user.head){
            case 0:
                break;
            case 1:
                headImage.setImageDrawable(getResources().getDrawable(R.drawable.xusong,null));
                break;
        }

        chatList.add(new Chat("高远","xusong",1,"快画领域模型"));
        chatList.add(new Chat("王昊阳","xusong",1,"躺好，等吃饭"));
        chatList.add(new Chat("马飞飞","xusong",1,"你们再说啥？"));
        chatList.add(new Chat("栾迎凯","xusong",0,"啦啦啦不会"));


    }

    static class ChatListAdapter extends ArrayAdapter<Chat> {

        private int resourceId;

        private Context context;
        public ChatListAdapter(Context context, int textViewResourceId, List<Chat> objects) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Chat chat = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
                holder = new ViewHolder();
                holder.friendHead = (ImageView) convertView.findViewById(R.id.item_friend_head_img);
                holder.friendName = (TextView) convertView.findViewById(R.id.item_friend_name);
                holder.lastChat = (TextView) convertView.findViewById(R.id.item_friend_lastchat);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //为聊天列表中的每一项赋值
            //holder.friendHead.setImageDrawable(null);
            holder.friendName.setText(chat.getName());
            holder.lastChat.setText(chat.getLastChat());
            //不在线的头像显示灰色
            if(chat.onlineStatue == 0){
//                Drawable wrappedDrawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.xusong));
//                DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
//                holder.friendHead.setImageDrawable(wrappedDrawable);
                convertView.findViewById(R.id.item_chat_head_offline).setVisibility(View.VISIBLE);

            }else{
                convertView.findViewById(R.id.item_chat_head_offline).setVisibility(View.INVISIBLE);

            }

            return convertView;

        }

        static class ViewHolder {
            ImageView friendHead;
            TextView friendName;
            TextView lastChat;


        }
    }
}
