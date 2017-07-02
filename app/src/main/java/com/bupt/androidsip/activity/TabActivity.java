package com.bupt.androidsip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bupt.androidsip.R;
import com.bupt.androidsip.fragment.FriendFragment;
import com.bupt.androidsip.fragment.MeFragment;
import com.bupt.androidsip.fragment.MessageFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xusong on 2017/7/2.
 * About:
 */

public class TabActivity extends BaseActivity {

    @BindView(R.id.tab_frag_container)
    RelativeLayout fragContainer;
    @BindView(R.id.tab_message_container)
    LinearLayout messageContainer;
    @BindView(R.id.tab_friends_container)
    LinearLayout friendContainer;
    @BindView(R.id.tab_me_container)
    LinearLayout meContainer;

    List<Fragment> fragmentList;
    int currentFrag = -1;
    FragmentManager fragmentManager;

    long exitTime = 0;


    public static void Start(Context context){
        Intent  intent = new Intent(context,TabActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ButterKnife.bind(this);
        initData();
        initView();

    }

    //初始化fragments，并准备显示第一个
    private void initData() {
        fragmentManager = getSupportFragmentManager();
        fragmentList = new ArrayList<>();
        fragmentList.add(new MessageFragment());
        fragmentList.add(new FriendFragment());
        fragmentList.add(new MeFragment());
        currentFrag = 0;//当前第x号被选中
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.tab_frag_container, fragmentList.get(0), "msg");
        ft.add(R.id.tab_frag_container, fragmentList.get(1), "friend");
        ft.add(R.id.tab_frag_container, fragmentList.get(2), "me");
        ft.hide(fragmentList.get(1));
        ft.hide(fragmentList.get(2));
        ft.commit();//显示消息
    }

    //初始化onclicklisteners
    private void initView() {
        messageContainer.setOnClickListener(e -> updateView(0));
        friendContainer.setOnClickListener(e -> updateView(1));
        meContainer.setOnClickListener(e -> updateView(2));
    }

    private void updateView(int index) {
        if (index == currentFrag)
            return;
        else {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.hide(fragmentList.get(currentFrag)).show(fragmentList.get(index));
            currentFrag = index;
            ft.commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - exitTime > 3000) {
                exitTime = System.currentTimeMillis();
                showText("再按一次退出程序");
                return false;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
