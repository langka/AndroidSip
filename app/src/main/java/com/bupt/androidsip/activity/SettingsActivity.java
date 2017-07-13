package com.bupt.androidsip.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.mananger.ActivityManager;
import com.bupt.androidsip.mananger.ChatManager;
import com.bupt.androidsip.mananger.DBManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bupt.androidsip.R.id.item_profile_righttext;
import static com.bupt.androidsip.R.id.item_profile_text;

/**
 * Created by vita-nove on 01/07/2017.
 */

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.frag_vip)
    RelativeLayout vipContainer;

    @BindView(R.id.frag_notifications)
    RelativeLayout notificationsContainer;

    @BindView(R.id.frag_push_enter)
    RelativeLayout pushEnterContainer;

    @BindView(R.id.frag_clear_chat_history)
    RelativeLayout clearChatHistoryContainer;

    @BindView(R.id.frag_change_password)
    RelativeLayout changePasswordContainer;

    @BindView(R.id.frag_logout)
    RelativeLayout logoutContainer;
    //这里有问题
    ChatManager chatManager = ChatManager.getChatManager();
    DBManager dbManager = null;
    SharedPreferences pref = null;
    SharedPreferences.Editor editor = null;

    boolean isShock = true;
    boolean pushEnterToSend = true;

    private View.OnClickListener settingsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frag_vip:
                    showText("会员服务暂未推出。");
                    break;
                case R.id.frag_notifications:
                    showBottomDialog(Arrays.asList("震动", "静音"), Arrays.asList(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.putBoolean("isShock", isShock);
                            editor.commit();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.putBoolean("isShock", !isShock);
                            editor.commit();
                        }
                    }));
                    break;
                case R.id.frag_push_enter:
                    showBottomDialog(Arrays.asList("开启", "关闭"), Arrays.asList(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.putBoolean("pushEnterToSend", pushEnterToSend);
                            editor.commit();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editor.putBoolean("pushEnterToSend", !pushEnterToSend);
                            editor.commit();
                        }
                    }));
                    break;
                case R.id.frag_clear_chat_history:
                    //删除内存中的缓存消息
                    chatManager.removeAllChat();
                    //删除本地服务器上的缓存消息
                    for (int i = 0; i < chatManager.getChatList().size(); ++i) {
                        //下面得到的ID是与自己聊天的那个人的ID
                        dbManager.delete(chatManager.getChatList().get(i).ID);
                    }
                    showText("清理成功！");
                    break;
                case R.id.frag_change_password:
                    showInputDialog("变更", "请输入新密码", e -> {
                        if (e.trim().isEmpty())
                            showText("密码为空");
                        else {
                            // TODO: 02/07/2017 真正保存这些内容
                            showText("变更成功！");
                        }
                    });
                    break;
                case R.id.frag_logout:
//                    finish(ActivityManager.getActivityManager().getList().get(0));
                    ActivityManager.getActivityManager().exit();
                    break;
            }
        }
    };

    public static void Start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getHeaderDivider().setVisibility(View.GONE);
        enableLeftImage(R.drawable.ic_arrow_back_24px, e -> finish());
        setTitle("设置");
        pref = getSharedPreferences("MySettings", MODE_PRIVATE);
        editor = pref.edit();
        dbManager = DBManager.getInstance(this);
        ActivityManager.getActivityManager().addActivity(this);
        initView();
    }


    private void initRowView(View v, int imgId, String text, String detail) {
        ImageView icon = (ImageView) v.findViewById(R.id.item_profile_icon);
        TextView textView = (TextView) v.findViewById(item_profile_text);
        TextView detailView = (TextView) v.findViewById(item_profile_righttext);
        icon.setImageDrawable(getResources().getDrawable(imgId));
        textView.setText(text);
        detailView.setText(detail);
    }

    private void initView() {
        vipContainer.setOnClickListener(settingsOnClick);
        notificationsContainer.setOnClickListener(settingsOnClick);
        pushEnterContainer.setOnClickListener(settingsOnClick);
        clearChatHistoryContainer.setOnClickListener(settingsOnClick);
        changePasswordContainer.setOnClickListener(settingsOnClick);
        logoutContainer.setOnClickListener(settingsOnClick);

        initRowView(vipContainer, R.drawable.ic_hourglass_empty_black_30dp, "办理会员", "");
        initRowView(notificationsContainer, R.drawable.ic_notifications_30px, "通知方式", "");
        initRowView(pushEnterContainer, R.drawable.ic_subdirectory_arrow_left_black_30dp,
                "按回车发送", "");
        initRowView(clearChatHistoryContainer, R.drawable.ic_verified_user_30px, "清理聊天记录", "");
        initRowView(changePasswordContainer, R.drawable.ic_cached_30px, "更改密码", "");
        initRowView(logoutContainer, R.drawable.ic_sentiment_very_dissatisfied_black_30dp,
                "注销登录", "走好不送");
    }


}
