package com.bupt.androidsip.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.view.View;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.activity.TabActivity;
import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.entity.EventConst;
import com.bupt.androidsip.entity.Message;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.mananger.UserManager;
import com.bupt.androidsip.util.VibratorUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

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
    public static int unreadTotal;

    private static List<Chat> chatList = new ArrayList<>();
    ChatListAdapter chatListAdapter = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_chat_list, null);
        ButterKnife.bind(this, v);

        initdata();

        chatListAdapter = new ChatListAdapter(getActivity(), R.layout.item_frag_chat_list, chatList);

        listView.setAdapter(chatListAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: 2017/7/1 触发点击
            EventBus.getDefault().postSticky(new EventConst.NewMessage());

        });
        EventBus.getDefault().register(this);
        return v;

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void removeUnread(EventConst.Unread unread) {
        if (unread.removeAll == false)
            return;
        for (int i = 0; i < chatList.size(); i++) {
            chatList.get(i).removeUnread();
            chatListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void addNewMessage(EventConst.NewMessage newMsg) {
        Message msg = new Message();
        chatList.get((int) (1 + Math.random() * (4 - 1 + 1)) - 1).addMessage(msg);
        chatListAdapter.notifyDataSetChanged();
        VibratorUtils.Vibrate(getActivity(), 200);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void addUnread(EventConst.Unread unread) {
//        //// TODO: 06/07/2017 为合适的chatlist中的元素添加unread 参数要更改
//        chatListAdapter.notifyDataSetChanged();
//        unreadTotal = 0;
//        for (int i = 0; i < chatList.size(); i++)
//            unreadTotal += chatList.get(i).getUnread();
//        if (unreadTotal != 0)
//            EventBus.getDefault().postSticky(new EventConst.Unread(unreadTotal));
//    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void initdata() {
        user = UserManager.getInstance().getUser();

        // TODO: 2017/7/1 动态展示用户头像
        switch (user.head) {
            case 0:
                break;
            case 1:
                headImage.setImageDrawable(getResources().getDrawable(R.drawable.xusong, null));
                break;
        }

        chatList.add(new Chat("高远", "xusong", 1, "快画领域模型"));
        chatList.add(new Chat("王昊阳", "xusong", 1, "躺好，等吃饭"));
        chatList.add(new Chat("马飞飞", "xusong", 1, "你们再说啥？"));
        chatList.add(new Chat("栾迎凯", "xusong", 0, "啦啦啦不会"));


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
                holder.time = (TextView) convertView.findViewById(R.id.last_chat_time);
                holder.badge = new QBadgeView(context).bindTarget(convertView.
                        findViewById(R.id.last_chat_time));
                holder.badge.setBadgeTextSize(13, true);
                holder.badge.setGravityOffset(0, 0, true);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.badge.setBadgeNumber(chat.getUnread());
            holder.badge.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                @Override
                public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                    if (dragState == STATE_SUCCEED) {
                        targetView.setPadding(0, 5, 0, 5);
                        badge.setBadgeNumber(0);
                        chat.removeUnread();  //去除未读
                        unreadTotal = 0;
                        for (int i = 0; i < chatList.size(); i++)
                            unreadTotal += chatList.get(i).getUnread();

                        EventBus.getDefault().postSticky(new EventConst.Unread(unreadTotal));
                    }
                }
            });

            //为聊天列表中的每一项赋值
            //holder.friendHead.setImageDrawable(null);
            holder.friendName.setText(chat.getLeftName());
            holder.lastChat.setText(chat.getLastChat());
            //不在线的头像显示灰色
            if (chat.onlineStatue == 0) {
//                Drawable wrappedDrawable = DrawableCompat.wrap(context.getResources().getDrawable(R.drawable.xusong));
//                DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
//                holder.friendHead.setImageDrawable(wrappedDrawable);
                convertView.findViewById(R.id.item_chat_head_offline).setVisibility(View.VISIBLE);

            } else {
                convertView.findViewById(R.id.item_chat_head_offline).setVisibility(View.INVISIBLE);

            }

            //为没有已读的框的time调整padding
            if (chat.getUnread() == 0)
                holder.time.setPadding(0, 10, 0, 10);
            else
                holder.time.setPadding(0, 10, 80, 10);

            //设置tab上的未读信息数
            unreadTotal = 0;
            for (int i = 0; i < chatList.size(); i++)
                unreadTotal += chatList.get(i).getUnread();
            if (unreadTotal != 0)
                EventBus.getDefault().postSticky(new EventConst.Unread(unreadTotal));

            return convertView;

        }

//        @Override
//        public void notifyDataSetChanged() {
//            //设置总未读
//            unreadTotal = 0;
//            for (int i = 0; i < chatList.size(); i++) {
//                unreadTotal += chatList.get(i).getUnread();
//            }
//            EventBus.getDefault().postSticky(new EventConst.Unread(unreadTotal));
//            Log.d("total", "" + unreadTotal);
//        }

        static class ViewHolder {
            ImageView friendHead;
            TextView friendName;
            TextView lastChat;
            TextView time;
            Badge badge;
        }
    }
}
