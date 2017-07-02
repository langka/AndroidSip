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
                            // TODO: 02/07/2017 添加更改通知方式操作
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }));
                    break;
                case R.id.frag_push_enter:
                    showBottomDialog(Arrays.asList("开启", "关闭"), Arrays.asList(new View.OnClickListener() {
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
        enableLeftImage(R.drawable.ic_arrow_back_24px, e -> finish());
        setTitle("设置");
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
