package com.bupt.androidsip.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.androidsip.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bupt.androidsip.R.id.item_profile_righttext;
import static com.bupt.androidsip.R.id.item_profile_text;

/**
 * Created by acer on 2017/7/5.
 */

public class FriendDetailInfoActivity extends BaseActivity  {
    @BindView(R.id.frag_nickname)
    RelativeLayout nicknameContainer;

    @BindView(R.id.frag_description)
    RelativeLayout descriptionContainer;

    @BindView(R.id.frag_sex)
    RelativeLayout sexContainer;

    @BindView(R.id.frag_age)
    RelativeLayout ageContainer;

    @BindView(R.id.frag_birthday)
    RelativeLayout birthdayContainer;

    @BindView(R.id.frag_constellation)
    RelativeLayout constellationContainer;

    @BindView(R.id.frag_location)
    RelativeLayout locationContainer;

    @BindView(R.id.frag_hometown)
    RelativeLayout hometownContainer;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail_info);
        ButterKnife.bind(this);
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

        initRowView(nicknameContainer, R.drawable.ic_perm_identity_black_30dp, "昵称", "徐日天");
        initRowView(descriptionContainer, R.drawable.ic_wb_incandescent_black_30dp, "个性签名",
                "我爱吃西瓜");
        initRowView(sexContainer, R.drawable.ic_face_black_30dp, "性别", "男");
        initRowView(ageContainer, R.drawable.ic_access_time_black_30dp, "年龄", "21");
        initRowView(birthdayContainer, R.drawable.ic_cake_black_30dp, "生日",
                "2月3日");
        initRowView(constellationContainer, R.drawable.ic_star_border_black_30dp,
                "星座", "双鱼座");
        initRowView(locationContainer, R.drawable.ic_business_black_30dp, "所在地", "北京-海淀区");
        initRowView(hometownContainer, R.drawable.ic_account_balance_black_30dp, "故乡",
                "四川");
    }

}
