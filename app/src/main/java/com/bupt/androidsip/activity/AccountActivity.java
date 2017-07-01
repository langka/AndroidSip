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
                    break;
                case R.id.frag_description:
                    break;
                case R.id.frag_sex:
                    break;
                case R.id.frag_account_id:
                    break;
                case R.id.frag_email:
                    break;
                case R.id.frag_registration_time:
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
