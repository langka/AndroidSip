package com.bupt.androidsip.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Chat;
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

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.frag_message, null);
//        return v;
//    }

    @BindView(R.id.frag_friend_head)
    ImageView headImage;
    @BindView(R.id.frag_friend_list)
    ListView listView;

    private List<Chat> chatList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_chat_list, null);
        ButterKnife.bind(this, v);

        initdata();

        // TODO: 2017/7/1 动态展示用户头像，获取用户URL
        //headImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_18px));

        // TODO: 2017/7/1 list 动态展示
       ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), R.layout.item_frag_chat_list, chatList);

        listView.setAdapter(chatListAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: 2017/7/1 触发点击

        });
        return v;

    }

    void initdata() {
        //chatList = UserManager.getInstance().getUser().chatList;
    }

    static class ChatListAdapter extends ArrayAdapter<Chat> {

        private int resourceId;

        public ChatListAdapter(Context context, int textViewResourceId, List<Chat> objects) {
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
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

            holder.friendHead.setImageURI(null);
            holder.friendName.setText(chat.getName());
            holder.lastChat.setText(chat.getLastChat());

            return convertView;

        }

        static class ViewHolder {
            ImageView friendHead;
            TextView friendName;
            TextView lastChat;
        }
    }
}
