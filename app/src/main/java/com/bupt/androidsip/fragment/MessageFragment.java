package com.bupt.androidsip.fragment;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.content.Intent;
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
import com.bupt.androidsip.entity.sip.SipChat;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.mananger.ChatManager;
import com.bupt.androidsip.mananger.DBManager;
import com.bupt.androidsip.mananger.SipChatManager;
import com.bupt.androidsip.mananger.UserManager;
import com.bupt.androidsip.sip.SipMessageListener;
import com.bupt.androidsip.sip.impl.SipManager;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by xusong on 2017/7/2.
 * About:
 */

public class MessageFragment extends BaseFragment {

    @BindView(R.id.frag_chat_my_head)
    ImageView headImage;
    @BindView(R.id.frag_friend_list)
    ListView listView;

    User user = UserManager.getInstance().getUser();
    UserManager userManager = UserManager.getInstance();
    public static int unreadTotal;
    public static SimpleDateFormat simpleDateFormat;

    private int myAvatar;
    private List<SipChat> groups;

    public int getUserAvatarFromID(int ID) {
        return userManager.searchUser(ID).head;
    }

    ChatManager chatManager = ChatManager.getChatManager();

    private static List<Chat> chatList = ChatManager.getChatManager().getChatList();
    ChatListAdapter chatListAdapter = null;

    boolean isShock = true;

    SipManager sipManager = SipManager.getSipManager();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_chat_list, null);
        ButterKnife.bind(this, v);
        Log.d("现在又有几个chat", chatManager.getChatList().size() + "");

        SharedPreferences pref = getActivity().getSharedPreferences("MySettings", MODE_PRIVATE);
        isShock = pref.getBoolean("isShock", true);

        simpleDateFormat = new SimpleDateFormat("HH:mm");
        initData();
        loadLocalMessage(DBManager.getInstance(getActivity()).loadAllMessages());

        chatListAdapter = new ChatListAdapter(getActivity(), R.layout.item_frag_chat_list,
                chatManager.getChatList());
        listView.setAdapter(chatListAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: 2017/7/1 触发点击
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("chat", chatManager.getChat(i));
            intent.putExtras(bundle);
            startActivity(intent);

        });

        sipManager.setMessageListener(new SipMessageListener() {
            @Override
            public void onNewMessage(SipMessage message) {
                createChatFromSipMessage(message);
            }

        });

        EventBus.getDefault().register(this);
        chatListAdapter.notifyDataSetChanged();
        return v;

    }

    public void loadLocalMessage(List<SipMessage> sipMessages) {
        if (sipMessages == null)
            return;
        for (int i = 0; i < sipMessages.size(); ++i) {
            if (sipMessages.get(i).from == userManager.getUser().id) {
                //我发给别人的
                if (!chatManager.isInList(sipMessages.get(i).to.get(0))) {
                    //不在list中，新建chat 添加到list中
                    chatManager.addChat(new Chat(userManager.searchUser(sipMessages.get(i).to.get(0)).name,
                            userManager.searchUser(sipMessages.get(i).to.get(0)).head, 1,
                            sipMessages.get(i).content, sipMessages.get(i).to.get(0)));
                    //添加此条message
                    chatManager.addMsg(chatManager.getChatList().size() - 1,
                            getChatMsgFrom(sipMessages.get(i).content, sipMessages.get(i).to.get(0),
                                    sipMessages.get(i).comeTime));
                } else {
                    chatManager.addMsg(i, getChatMsgFrom(sipMessages.get(i).content,
                            sipMessages.get(i).to.get(0), sipMessages.get(i).comeTime));
                    //直接向list中添加这个message
                }
            } else if (sipMessages.get(i).from != userManager.getUser().id) {
                //别人发给我的
                if (!chatManager.isInList(sipMessages.get(i).from)) {
                    //不在list中，新建chat
                    chatManager.addChat(new Chat(userManager.searchUser(sipMessages.get(i).from).name,
                            userManager.searchUser(sipMessages.get(i).from).head, 1,
                            sipMessages.get(i).content, sipMessages.get(i).from));
                    chatManager.addMsg(chatManager.getChatList().size() - 1,
                            getChatMsgTo(sipMessages.get(i).content, sipMessages.get(i).from,
                                    sipMessages.get(i).comeTime));
                } else {
                    chatManager.addMsg(i, getChatMsgTo(sipMessages.get(i).content,
                            sipMessages.get(i).from, sipMessages.get(i).comeTime));
                }
            }
        }
        chatManager.sortChatMessages();
    }

    public void setMyAvatar() {
        myAvatar = user.head;
    }

    public void createChatFromSipMessage(SipMessage sipMessage) {
        for (int i = 0; i < chatList.size(); ++i) {
            if (sipMessage.from == chatList.get(i).ID) {
//                chatList.get(i).messages.add(getChatMsgFrom(sipMessage.content,
//                        sipMessage.from));
                chatManager.addMsg(i, getChatMsgFrom(sipMessage.content, sipMessage.from,
                        sipMessage.comeTime));
//                chatList.get(i).setLastMsgWithUnread(sipMessage.content);
                chatManager.setLastMsgWithUnread(i, sipMessage.content);
                chatListAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new EventConst.NewMsg(userManager.searchUser(sipMessage.from),
                        sipMessage.content, sipMessage.comeTime));
                if (isShock)
                    VibratorUtils.Vibrate(getActivity(), 200);
                return;
            }
        }

//        chatList.add(new Chat(sipMessage.from.name, sipMessage.from.head, 1,
//                sipMessage.content, sipMessage.from.id));
        chatManager.addChat(new Chat(userManager.searchUser(sipMessage.from).name, userManager.searchUser(sipMessage.from).head, 1,
                sipMessage.content, userManager.searchUser(sipMessage.from).id));
//        chatList.get(chatList.size() - 1).messages.add(getChatMsgFrom(sipMessage.content,
//                sipMessage.from));
        chatManager.addMsg(chatManager.getChatList().size() - 1, getChatMsgFrom(sipMessage.content,
                sipMessage.from, sipMessage.comeTime));

        EventBus.getDefault().post(new EventConst.NewMsg(userManager.searchUser(sipMessage.from),
                sipMessage.content, sipMessage.comeTime));
        VibratorUtils.Vibrate(getActivity(), 200);
        if (isShock)
            chatListAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void removeAllUnread(EventConst.RemoveAll removeAll) {
        if (removeAll.isRemoveAll()) {
            for (int i = 0; i < chatList.size(); ++i)
//                chatList.get(i).removeUnread();
                chatManager.removeUnread(i);
            chatListAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void removeOneUnread(EventConst.RemoveOne removeOne) {
        for (int i = 0; i < chatList.size(); ++i) {
            if (chatList.get(i).ID == removeOne.getID()) {
//                chatList.get(i).removeUnread();
                chatManager.removeUnread(i);
                chatListAdapter.notifyDataSetChanged();
            }
        }
//        unreadTotal = 0;
//        //刷新一下当前的chatlist
//        chatList = chatManager.getChatList();
//        for (int i = 0; i < chatList.size(); i++)
//            unreadTotal += chatList.get(i).getUnread();
//        Log.d("new unread", unreadTotal + "");
        EventBus.getDefault().post(new EventConst.Unread(chatManager.getTotalUnread(),
                removeOne.getID()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setLastMsg(EventConst.LastMsg lastMsg) {
        for (int i = 0; i < chatList.size(); ++i)
            if (chatList.get(i).ID == lastMsg.getID())
//                chatList.get(i).setLastChat(lastMsg.getMsg());
                chatManager.setLastMsgWithUnread(i, lastMsg.getMsg());
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addNewMsg(Message msg) {
        for (int i = 0; i < chatList.size(); ++i)
            if (chatList.get(i).ID == msg.ID)
//                chatList.get(i).messages.add(msg);
                chatManager.addMsg(i, msg);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void initData() {
        user = UserManager.getInstance().getUser();
        groups = SipChatManager.getInstance().getSipChat();
        if (groups == null)
            groups = new ArrayList<>();

        for (int i = 0; i < groups.size(); ++i) {
//            chatList.add(fromSipChatToChat(groups.get(i)));
            chatManager.addChat(fromSipChatToChat(groups.get(i)));
        }
        setMyAvatar();
    }

    public Chat fromSipChatToChat(SipChat sipChat) {
        Chat chat = new Chat();
        if (sipChat.users.size() > 1) {
            chat.leftAvatar = R.drawable.ic_group_blue_90dp;
            chat.leftName = sipChat.users.size() + "人群聊";
            chat.latestTime = sipChat.latestTime;
            chat.lastMessage = "本地存储方案还没实现";
            chat.onlineStatue = 1;
            chat.ID = sipChat.id;
        } else {
            chat.leftAvatar = userManager.searchUser(sipChat.users.get(0)).head;
            chat.leftName = userManager.searchUser(sipChat.users.get(0)).name;
            chat.latestTime = sipChat.latestTime;
            chat.lastMessage = "本地存储方案还没实现";
            chat.onlineStatue = 1;
            chat.ID = sipChat.id;
            //  08/07/2017 聊天记录本地储存读取
        }

        return chat;
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


            holder.time.setText(simpleDateFormat.format(chat.latestTime));
            holder.badge.setBadgeNumber(chat.getUnread());
            holder.badge.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                @Override
                public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                    if (dragState == STATE_SUCCEED) {
                        targetView.setPadding(0, 5, 0, 5);
                        badge.setBadgeNumber(0);
                        ChatManager.getChatManager().removeUnread(position);
//                        chat.removeUnread();  //去除未读
//                        unreadTotal = 0;
//                        for (int i = 0; i < chatList.size(); i++)
//                            unreadTotal += chatList.get(i).getUnread();

                        EventBus.getDefault().post(new EventConst.Unread(
                                ChatManager.getChatManager().getTotalUnread(), chat.ID));
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
//            unreadTotal = 0;
//            for (int i = 0; i < chatList.size(); i++)
//                unreadTotal += chatList.get(i).getUnread();
//            if (unreadTotal != 0)
            EventBus.getDefault().post(new EventConst.Unread(
                    ChatManager.getChatManager().getTotalUnread(), chat.ID));

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

    private Message getChatMsgFrom(String message, int ID, long time) {
        Message msg = new Message();
        msg.content = message;
        msg.fromOrTo = 0;
        msg.rightAvatar = myAvatar;
        msg.leftAvatar = getUserAvatarFromID(ID);
        msg.time = time;
        return msg;
    }

    private Message getChatMsgTo(String message, int ID, long time) {
        Message msg = new Message();
        msg.content = message;
        msg.fromOrTo = 1;
        msg.rightAvatar = myAvatar;
        msg.leftAvatar = getUserAvatarFromID(ID);
        msg.time = time;
        return msg;
    }
}
