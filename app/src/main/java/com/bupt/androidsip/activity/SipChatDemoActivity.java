package com.bupt.androidsip.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.sip.SipMessageListener;
import com.bupt.androidsip.sip.impl.SipManager;
import com.bupt.androidsip.sip.impl.SipProfile;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xusong on 2017/7/10.
 * About:
 */

public class SipChatDemoActivity extends BaseActivity {
    @BindView(R.id.sipchat_myip)
    TextView myIp;
    @BindView(R.id.sipdchat_target)
    EditText target;
    @BindView(R.id.sipchat_submit)
    TextView send;
    @BindView(R.id.sipchat_msg)
    TextView msg;
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sipdemo);
        ButterKnife.bind(this);
        send.setOnClickListener(v -> SipManager.getSipManager().sendMessageL(target.getText().toString(),"hello!!"));
        SipManager.getSipManager().setMessageListener(message -> {
            handler.post(()->showText(message.content));
        });
        SipProfile profile = SipManager.getSipManager().getSipProfile();
        myIp.setText(profile.getLocalIp()+":"+profile.getLocalPort());
    }
}





















































































