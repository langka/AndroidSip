package com.bupt.androidsip.fragment;

import android.content.Context;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.activity.ChatActivity;
import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.entity.EventConst;
import com.bupt.androidsip.entity.Message;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.mananger.UserManager;
import com.bupt.androidsip.util.VibratorUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private SimpleDateFormat simpleDateFormat;

    public void setMyAvatar() {
        // myAvatar = UserManager.getInstance().getUser().head;
        myAvatar = R.drawable.xusong;
    }

    private int myAvatar;

    public int getUserAvatarFromID(int ID) {
        return R.drawable.xusong;
    }


    private static List<Chat> chatList = new ArrayList<>();
    ChatListAdapter chatListAdapter = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_chat_list, null);
        ButterKnife.bind(this, v);

        simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        initdata();

        setMyAvatar();
        chatListAdapter = new ChatListAdapter(getActivity(), R.layout.item_frag_chat_list, chatList);

        listView.setAdapter(chatListAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: 2017/7/1 触发点击
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("chat", chatList.get(i));
            intent.putExtras(bundle);
            startActivity(intent);

        });
        EventBus.getDefault().register(this);
        return v;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void removeAllUnread(EventConst.RemoveAll removeAll) {
        Log.d("get from fragment", "aaaa");
        if (removeAll.isRemoveAll()) {
            for (int i = 0; i < chatList.size(); ++i)
                chatList.get(i).removeUnread();
            chatListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void removeOneUnread(EventConst.RemoveOne removeOne) {
        for (int i = 0; i < chatList.size(); ++i) {
            if (chatList.get(i).ID == removeOne.getID()) {
                chatList.get(i).removeUnread();
                chatListAdapter.notifyDataSetChanged();
            }
        }
        unreadTotal = 0;
        for (int i = 0; i < chatList.size(); i++)
            unreadTotal += chatList.get(i).getUnread();
        Log.d("new unread", unreadTotal + "");
        EventBus.getDefault().post(new EventConst.Unread(unreadTotal, removeOne.getID()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setLastMsg(EventConst.LastMsg lastMsg) {
        for (int i = 0; i < chatList.size(); ++i)
            if (chatList.get(i).ID == lastMsg.getID())
                chatList.get(i).setLastChat(lastMsg.getMsg());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addNewMsg(Message msg) {
        for (int i = 0; i < chatList.size(); ++i)
            if (chatList.get(i).ID == msg.ID)
                chatList.get(i).messages.add(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveNewMsg(EventConst.NewMsg newMsg) {
        for (int i = 0; i < chatList.size(); ++i) {
            if (chatList.get(i).ID == newMsg.getID()) {
                chatList.get(i).messages.add(getChatMsgFrom(newMsg.getMsg(), newMsg.getID()));
                chatList.get(i).setLastMsgWithUnread(newMsg.getMsg());
                chatListAdapter.notifyDataSetChanged();
                VibratorUtils.Vibrate(getActivity(), 200);
                return;
            }
        }
        chatList.add(new Chat("name", R.id.avatar_left, 1, newMsg.getMsg(), newMsg.getID()));
        chatList.get(chatList.size() - 1).messages.add(getChatMsgFrom(newMsg.getMsg(), newMsg.getID()));
        VibratorUtils.Vibrate(getActivity(), 200);
        chatListAdapter.notifyDataSetChanged();
    }


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

        chatList.add(new Chat("高远", R.drawable.xusong, 1, "快画领域模型", 0));
        chatList.add(new Chat("王昊阳", R.drawable.xusong, 1, "躺好，等吃饭", 1));
        chatList.add(new Chat("马飞飞", R.drawable.xusong, 1, "你们再说啥？", 2));
        chatList.add(new Chat("栾迎凯", R.drawable.xusong, 0, "啦啦啦不会", 3));
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

                        EventBus.getDefault().post(new EventConst.Unread(unreadTotal, chat.ID));
                    }
                }
            });

            //为聊天列表中的每一项赋值
            //holder.friendHead.setImageDrawable(null);


            holder.friendName.setText(chat.getLeftName());
            holder.lastChat.setText(chat.getLastMessage());
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
                EventBus.getDefault().post(new EventConst.Unread(unreadTotal, chat.ID));

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

    private Message getChatMsgFrom(String message, int ID) {
        Message msg = new Message();
        msg.content = message;
        msg.fromOrTo = 0;
        msg.rightAvatar = myAvatar;
        msg.leftAvatar = getUserAvatarFromID(ID);
        msg.time = simpleDateFormat.format(new Date());
        return msg;
    }

    private Message getChatMsgTo(String message, int ID) {
        Message msg = new Message();
        msg.content = message;
        msg.fromOrTo = 1;
        msg.rightAvatar = myAvatar;
        msg.leftAvatar = getUserAvatarFromID(ID);
        msg.time = simpleDateFormat.format(new Date());
        return msg;
    }
}
