package com.bupt.androidsip.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.activity.AccountActivity;
import com.bupt.androidsip.activity.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bupt.androidsip.R.id.item_profile_righttext;
import static com.bupt.androidsip.R.id.item_profile_text;

/**
 * Created by vita-nove on 01/07/2017.
 */

public class MeFragment extends BaseFragment {

    //上方头像姓名等
    @BindView(R.id.frag_head_image)
    ImageView headIamgeView;

    @BindView(R.id.frag_me_name)
    TextView nameTextView;

    @BindView(R.id.frag_me_sex)
    ImageView sexImageView;

    @BindView(R.id.frag_me_description)
    TextView descriptionTextView;

    //下方各横条
    @BindView(R.id.frag_me_account)
    RelativeLayout accountContainer;

    @BindView(R.id.frag_me_settings)
    RelativeLayout settingsContainer;

    //防止getActivity抛出空指针异常
    private Context context;
    private View.OnClickListener fragMeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frag_me_account:
                    AccountActivity.Start(context);
                    break;
                case R.id.frag_me_settings:
                    SettingsActivity.Start(context);
                    break;
            }
        }
    };

    //    @Subscribe
//    public void onEventMainThread(EventConst.UserInfoChangeEvent event) {
//        updateView(true);
//    }
//
//    @Subscribe
//    public void onEventMainThread(EventConst.UserLogoutEvent event) {
//        updateView(false);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_me, null);
        ButterKnife.bind(this, v);
        context = getActivity();
        initView();
//    getData();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
        accountContainer.setOnClickListener(fragMeOnClick);
        settingsContainer.setOnClickListener(fragMeOnClick);
        initRowView(accountContainer, R.drawable.ic_account_box_30px, "账号信息", "");
        initRowView(settingsContainer, R.drawable.ic_perm_data_setting_30px, "设置", "");
    }
}
