package com.bupt.androidsip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.androidsip.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private View.OnClickListener settingsOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frag_vip:
                    showText("此项无法更改。");
                    break;
                case R.id.frag_notifications:
                    showBottomDialog(Arrays.asList("关闭", "开启"), Arrays.asList(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 02/07/2017 添加开关通知操作
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }));
                    break;
                case R.id.frag_push_enter:
                    showBottomDialog(Arrays.asList("关闭", "开启"), Arrays.asList(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 02/07/2017 添加更改发送方式的操作
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }));
                    break;
                case R.id.frag_clear_chat_history:
                    break;
                case R.id.frag_change_password:
                    break;
                case R.id.frag_logout:
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
        initView();
    }

    private void initRowView(View v, int imgId, String text) {
        ImageView icon = (ImageView) v.findViewById(R.id.item_profile_icon);
        TextView textView = (TextView) v.findViewById(item_profile_text);
        icon.setImageDrawable(getResources().getDrawable(imgId));
        textView.setText(text);
    }

    private void initView() {
        vipContainer.setOnClickListener(settingsOnClick);
        notificationsContainer.setOnClickListener(settingsOnClick);
        pushEnterContainer.setOnClickListener(settingsOnClick);
        clearChatHistoryContainer.setOnClickListener(settingsOnClick);
        changePasswordContainer.setOnClickListener(settingsOnClick);
        logoutContainer.setOnClickListener(settingsOnClick);

        initRowView(vipContainer, R.drawable.ic_account_box_36px, "办理会员");
        initRowView(notificationsContainer, R.drawable.ic_account_box_36px, "通知方式");
        initRowView(pushEnterContainer, R.drawable.ic_account_box_36px, "按回车发送");
        initRowView(clearChatHistoryContainer, R.drawable.ic_account_box_36px, "清理聊天记录");
        initRowView(changePasswordContainer, R.drawable.ic_account_box_36px, "更改密码");
        initRowView(logoutContainer, R.drawable.ic_account_box_36px, "注销登录");
    }


}
