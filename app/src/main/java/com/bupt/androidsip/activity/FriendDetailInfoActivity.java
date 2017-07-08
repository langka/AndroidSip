package com.bupt.androidsip.activity;

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

import static com.bupt.androidsip.R.id.frag_sex;
import static com.bupt.androidsip.R.id.item_profile_righttext;
import static com.bupt.androidsip.R.id.item_profile_text;

/**
 * Created by acer on 2017/7/5.
 */

public class FriendDetailInfoActivity extends BaseActivity  {
    @BindView(R.id.frag_head_image)
    RelativeLayout headimageContainer;

    @BindView(R.id.frag_friend_nickname)
    RelativeLayout nicknameContainer;

    @BindView(R.id.frag_friend_personalize)
    RelativeLayout personalizeContainer;

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

    @BindView(R.id.frag_friend_detail_info_goback)
    RelativeLayout gobackContainer;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail_info);
        ButterKnife.bind(this);
        initView();
    }

    private View.OnClickListener accountOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frag_age:
                    showText("您无法更改好友的个人资料！");
                    break;
                case R.id.frag_birthday:
                    showText("您无法更改好友的个人资料！");
                    break;
                case R.id.frag_constellation:
                    showText("您无法更改好友的个人资料！");
                    break;
                case R.id.frag_location:
                    showText("您无法更改好友的个人资料！");
                    break;
                case R.id.frag_friend_detail_info_goback:
                    //返回上一页
                    break;
            }
        }
    };

    private void initRowView(View v, int imgId, String text, String detail) {
        ImageView icon = (ImageView) v.findViewById(R.id.item_profile_icon);
        TextView textView = (TextView) v.findViewById(item_profile_text);
        TextView detailView = (TextView) v.findViewById(item_profile_righttext);
        icon.setImageDrawable(getResources().getDrawable(imgId));
        textView.setText(text);
        detailView.setText(detail);
    }

    private void initView() {
        ageContainer.setOnClickListener(accountOnClick);
        birthdayContainer.setOnClickListener(accountOnClick);
        constellationContainer.setOnClickListener(accountOnClick);
        locationContainer.setOnClickListener(accountOnClick);
        gobackContainer.setOnClickListener(accountOnClick);

        TextView nameView = (TextView) nicknameContainer.findViewById(R.id.frag_friend_nickname);
        nameView.setText("徐日天天");

        TextView personalizeView = (TextView) personalizeContainer.findViewById(R.id.frag_friend_personalize);
        personalizeView.setText("我不爱吃西瓜!!!");

        ImageView sexView = (ImageView) sexContainer.findViewById(R.id.frag_friend_sex);
        //sexView.setImageDrawable("drawable/profile_icon_male.png");设置性别的图片

        ImageView headimageView = (ImageView) headimageContainer.findViewById(R.id.frag_head_image);
        //sexView.setImageDrawable("drawable/profile_icon_male.png");设置头像的图片

        initRowView(ageContainer, R.drawable.ic_access_time_black_30dp, "年龄", "21");
        initRowView(birthdayContainer, R.drawable.ic_cake_black_30dp, "生日",
                "2月3日");
        initRowView(constellationContainer, R.drawable.ic_star_border_black_30dp,
                "星座", "双鱼座");
        initRowView(locationContainer, R.drawable.ic_business_black_30dp, "所在地", "北京-海淀区");
    }

}
