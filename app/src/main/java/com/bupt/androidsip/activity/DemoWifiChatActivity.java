package com.bupt.androidsip.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.mananger.WifiDirectManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xusong on 2017/7/5.
 * About:
 */

public class DemoWifiChatActivity extends BaseActivity {
    @BindView(R.id.wifi_chat_board)
    TextView board;
    @BindView(R.id.wifi_chat_edit)
    EditText editText;
    @BindView(R.id.wifi_chat_send)
    TextView send;

    WifiDirectManager wifiDirectManager;
    WifiDirectManager.WifiChatManager chatManager;

    Handler handler ;

    public static void Start(Context context) {
        Intent intent = new Intent(context, DemoWifiChatActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_chat);
        ButterKnife.bind(this);
        wifiDirectManager = WifiDirectManager.getInstance(this);
        chatManager = wifiDirectManager.getChatManager();
        handler = new Handler();
        chatManager.registerChatListener(s -> board.setText(s),handler);

        send.setOnClickListener(v -> {
            chatManager.sendMessage(editText.getText().toString());
        });


    }
}
