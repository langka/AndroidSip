package com.bupt.androidsip.activity;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
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

public class AccountActivity extends BaseActivity {

    //前三项可以修改，后三项不可修改
    @BindView(R.id.frag_nickname)
    RelativeLayout nicknameContainer;

    @BindView(R.id.frag_description)
    RelativeLayout descriptionContainer;

    @BindView(R.id.frag_sex)
    RelativeLayout sexContainer;

    @BindView(R.id.frag_account_id)
    RelativeLayout accountIdContainer;

    @BindView(R.id.frag_email)
    RelativeLayout emailContainer;

    @BindView(R.id.frag_registration_time)
    RelativeLayout registrationTimeContainer;


    private View.OnClickListener accountOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frag_nickname:
                    showInputDialog("保存", "请输入姓名", e -> {
                        if (e.trim().isEmpty())
                            showText("内容为空");
                        else {
                            // TODO: 02/07/2017 真正保存这些内容
                            showText("保存成功！");
                        }
                    });
                    break;
                case R.id.frag_description:
                    showInputDialog("保存", "请输入个性签名", e -> {
                        if (e.trim().isEmpty())
                            showText("内容为空");
                        else {
                            // TODO: 02/07/2017 真正保存这些内容
                            showText("保存成功！");
                        }
                    });
                    break;
                case R.id.frag_sex:
                    showBottomDialog(Arrays.asList("男", "女"), Arrays.asList(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 02/07/2017 添加更改性别的操作
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }));
                    break;
                case R.id.frag_account_id:
                    // TODO: 02/07/2017 添加一个统一的通知板
                    showText("此项无法更改。");
                    break;
                case R.id.frag_email:
                    showText("此项无法更改。");
                    break;
                case R.id.frag_registration_time:
                    showText("此项无法更改。");
                    break;
            }
        }
    };


    public static void Start(Context context) {
        Intent intent = new Intent(context, AccountActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
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
        nicknameContainer.setOnClickListener(accountOnClick);
        descriptionContainer.setOnClickListener(accountOnClick);
        sexContainer.setOnClickListener(accountOnClick);
        accountIdContainer.setOnClickListener(accountOnClick);
        emailContainer.setOnClickListener(accountOnClick);
        registrationTimeContainer.setOnClickListener(accountOnClick);

        initRowView(nicknameContainer, R.drawable.ic_account_box_36px, "昵称");
        initRowView(descriptionContainer, R.drawable.ic_account_box_36px, "个性签名");
        initRowView(sexContainer, R.drawable.ic_account_box_36px, "性别");
        initRowView(accountIdContainer, R.drawable.ic_account_box_36px, "账号ID");
        initRowView(emailContainer, R.drawable.ic_account_box_36px, "注册邮箱");
        initRowView(registrationTimeContainer, R.drawable.ic_account_box_36px, "注册时间");
    }
}
