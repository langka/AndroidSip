package com.bupt.androidsip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.mananger.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WHY on 2017/7/1.
 */

public class FriendFragment extends BaseFragment {
    @BindView(R.id.item_friend_head_img)
    ImageView headImage;
    @BindView(R.id.frag_friend_append)
    ImageView append;
    @BindView(R.id.frag_friend_list)
    ListView listView;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_friend, null);
        ButterKnife.bind(v);

        // TODO: 2017/7/1 动态展示用户头像，获取用户URL
        headImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_18px));

        // TODO: 2017/7/1 list 动态展示



        return v;

    }

}
