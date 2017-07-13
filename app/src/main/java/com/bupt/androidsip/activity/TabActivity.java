package com.bupt.androidsip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.entity.EventConst;
import com.bupt.androidsip.entity.Message;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.fragment.FriendFragment;
import com.bupt.androidsip.fragment.MeFragment;
import com.bupt.androidsip.fragment.MessageFragment;
import com.bupt.androidsip.mananger.ActivityManager;
import com.bupt.androidsip.mananger.ChatManager;
import com.bupt.androidsip.mananger.DBManager;
import com.bupt.androidsip.mananger.EventManager;
import com.bupt.androidsip.mananger.UserManager;
import com.bupt.androidsip.sip.impl.SipManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.bupt.androidsip.fragment.MessageFragment.unreadTotal;

/**
 * Created by xusong on 2017/7/2.
 * About:
 */

public class TabActivity extends BaseActivity {

    @BindView(R.id.tab_frag_container)
    RelativeLayout fragContainer;
    @BindView(R.id.tab_message_container)
    LinearLayout messageContainer;
    @BindView(R.id.tab_friends_container)
    LinearLayout friendContainer;
    @BindView(R.id.tab_me_container)
    LinearLayout meContainer;

    List<ViewGroup> bottoms;
    List<Fragment> fragmentList;
    int currentFrag = -1;
    FragmentManager fragmentManager;
    ChatManager chatManager = ChatManager.getChatManager();
    DBManager dbManager = DBManager.getInstance(this);

    long exitTime = 0;
    SipManager sipManager = SipManager.getSipManager();

    Badge badge = null;

    public static void Start(Context context) {
        Intent intent = new Intent(context, TabActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ButterKnife.bind(this);
        initData();
        initView();
        bottoms = new ArrayList<>();
        bottoms.add(messageContainer);
        bottoms.add(friendContainer);
        bottoms.add(meContainer);
        EventBus.getDefault().register(TabActivity.this);
        ActivityManager.getActivityManager().addActivity(this);
    }

    @Override
    protected void onDestroy() {
       // saveMsgToLocalDB();
        super.onDestroy();
        EventBus.getDefault().unregister(TabActivity.this);
    }

    public void saveMsgToLocalDB() {
        List<SipMessage> list = null;
        for (int i = 0; i < chatManager.getChatList().size() - 1; ++i) {
            Chat chat = chatManager.getChatList().get(i);
            for (int j = 0; j < chat.messages.size() - 1; ++j) {
                list.add(fromMsgToSipMsg(chat.messages.get(j)));
            }
        }
        if (list == null)
            return;
        dbManager.save(list);
    }

    public SipMessage fromMsgToSipMsg(Message msg) {
        SipMessage sipMessage = new SipMessage();
        sipMessage.type = 0;
        sipMessage.comeTime = msg.time;
        sipMessage.createTime = msg.time;
        sipMessage.content = msg.content;
        if (msg.fromOrTo == 0) {
            sipMessage.from = msg.ID;
            sipMessage.to.add(UserManager.getInstance().getUser().id);
        } else {
            sipMessage.from = UserManager.getInstance().getUser().id;
            sipMessage.to.add(msg.ID);
        }
        return sipMessage;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showUnreadMessage(EventConst.Unread unread) {
        if (badge == null) {
            badge = new QBadgeView(TabActivity.this).bindTarget(messageContainer.
                    findViewById(R.id.message_icon));
            badge.setBadgeNumber(unread.getHowMany());
            badge.setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                @Override
                public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                    if (dragState == STATE_SUCCEED)
                        EventBus.getDefault().post(new EventConst.RemoveAll(true));
                }
            });
        } else
            badge.setBadgeNumber(unread.getHowMany());
    }


    //初始化fragments，并准备显示第一个
    private void initData() {
        fragmentManager = getSupportFragmentManager();
        fragmentList = new ArrayList<>();
        fragmentList.add(new MessageFragment());
        fragmentList.add(new FriendFragment());
        fragmentList.add(new MeFragment());
        currentFrag = 0;//当前第x号被选中
        messageContainer.getChildAt(0).setSelected(true);
        messageContainer.getChildAt(1).setSelected(true);
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.tab_frag_container, fragmentList.get(0), "msg");
        ft.add(R.id.tab_frag_container, fragmentList.get(1), "friend");
        ft.add(R.id.tab_frag_container, fragmentList.get(2), "me");
        ft.hide(fragmentList.get(1));
        ft.hide(fragmentList.get(2));
        ft.commit();//显示消息
    }

    //初始化onclicklisteners
    private void initView() {
        messageContainer.setOnClickListener(e -> updateView(0));
        friendContainer.setOnClickListener(e -> updateView(1));
        meContainer.setOnClickListener(e -> updateView(2));
    }

    private void updateView(int index) {

        if (index == currentFrag)
            return;
        else {
            bottoms.get(currentFrag).getChildAt(0).setSelected(false);
            bottoms.get(currentFrag).getChildAt(1).setSelected(false);
            bottoms.get(index).getChildAt(0).setSelected(true);
            bottoms.get(index).getChildAt(1).setSelected(true);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.hide(fragmentList.get(currentFrag)).show(fragmentList.get(index));
            currentFrag = index;

            ft.commit();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - exitTime > 3000) {
                exitTime = System.currentTimeMillis();
                showText("再按一次退出程序");
                return false;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
