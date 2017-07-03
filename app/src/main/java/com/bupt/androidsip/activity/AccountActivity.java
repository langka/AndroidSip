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

import static com.bupt.androidsip.R.id.frag_account_id;
import static com.bupt.androidsip.R.id.frag_description;
import static com.bupt.androidsip.R.id.frag_email;
import static com.bupt.androidsip.R.id.frag_registration_time;
import static com.bupt.androidsip.R.id.frag_sex;
import static com.bupt.androidsip.R.id.item_profile_righttext;
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
                            TextView tv = (TextView) v.findViewById(R.id.frag_nickname).
                                    findViewById(R.id.item_profile_righttext);
                            tv.setText(e);
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
                            TextView tv = (TextView) v.findViewById(R.id.frag_description).
                                    findViewById(R.id.item_profile_righttext);
                            tv.setText(e);
                            showText("保存成功！");
                        }
                    });
                    break;
                case R.id.frag_sex:
                    showBottomDialog(Arrays.asList("男", "女"), Arrays.asList(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // TODO: 02/07/2017 添加更改性别的操作
                            TextView tv = (TextView) v.findViewById(R.id.frag_sex).
                                    findViewById(R.id.item_profile_righttext);
                            tv.setText("男");
                            showText("保存成功！");
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView tv = (TextView) v.findViewById(R.id.frag_sex).
                                    findViewById(R.id.item_profile_righttext);
                            tv.setText("女");
                            showText("保存成功！");
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
        enableLeftImage(R.drawable.ic_arrow_back_24px, e -> finish());
        initData();
        initView();
    }

    private void initData() {
        TextView nameView = (TextView) nicknameContainer.findViewById(R.id.frag_nickname).
                findViewById(R.id.item_profile_righttext);
        nameView.setText("徐日天");
        TextView descriptionView = (TextView) descriptionContainer.
                findViewById(R.id.frag_description).findViewById(R.id.item_profile_righttext);
        descriptionView.setText("我爱吃西瓜!!!");
        TextView sexView = (TextView) sexContainer.findViewById(frag_sex).
                findViewById(R.id.item_profile_righttext);
        sexView.setText("女");
        TextView accountIdView = (TextView) accountIdContainer.findViewById(frag_account_id).
                findViewById(R.id.item_profile_righttext);
        accountIdView.setText("233");
        TextView emailView = (TextView) emailContainer.findViewById(frag_email).
                findViewById(R.id.item_profile_righttext);
        emailView.setText("songxu@bupt.edu.cn");
        TextView registrationView = (TextView) registrationTimeContainer.
                findViewById(frag_registration_time).
                findViewById(R.id.item_profile_righttext);
        registrationView.setText("2017.2.2");

        setTitle("账号信息");
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
        nicknameContainer.setOnClickListener(accountOnClick);
        descriptionContainer.setOnClickListener(accountOnClick);
        sexContainer.setOnClickListener(accountOnClick);
        accountIdContainer.setOnClickListener(accountOnClick);
        emailContainer.setOnClickListener(accountOnClick);
        registrationTimeContainer.setOnClickListener(accountOnClick);

        initRowView(nicknameContainer, R.drawable.ic_exposure_plus_1_black_30dp, "昵称", "徐日天");
        initRowView(descriptionContainer, R.drawable.ic_wb_incandescent_black_30dp, "个性签名",
                "我爱吃西瓜");
        initRowView(sexContainer, R.drawable.ic_face_black_30dp, "性别", "男");
        initRowView(accountIdContainer, R.drawable.ic_perm_identity_black_30dp, "账号ID", "233");
        initRowView(emailContainer, R.drawable.ic_email_black_30dp, "注册邮箱",
                "songxu@bupt.edu.cn");
        initRowView(registrationTimeContainer, R.drawable.ic_access_time_black_30dp,
                "注册时间", "2017.1.1");
    }
}
