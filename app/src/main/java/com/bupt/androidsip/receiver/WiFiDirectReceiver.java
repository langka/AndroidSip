package com.bupt.androidsip.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.bupt.androidsip.mananger.WifiDirectManager;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.*;

/**
 * Created by xusong on 2017/7/4.
 * About:
 */

public class WiFiDirectReceiver extends BroadcastReceiver {

    public static final String TAG = "wifibroadcastreceiverrr";

    public WiFiDirectReceiver(OnReceiveListener listener) {
        this.listener = listener;
    }

    public interface OnReceiveListener {
        void OnFindPeers();//找到了Peer

        void onDisConnected();

        void onConnected();

        void onP2PDisabled();

        void onP2PEnabled();

        void onPeersChanged();//就是找到了peer
    }

    OnReceiveListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // WiFi P2P 可以使用
                    Log.d(TAG,"P2P ENABLED");
                    listener.onP2PEnabled();
                } else {
                    // WiFi P2P 不可以使用
                    Log.d(TAG,"P2P DISABLED");
                    listener.onP2PDisabled();
                }
                break;
            case WIFI_P2P_PEERS_CHANGED_ACTION:
                Log.d(TAG,"PEERS CHANGED");
                listener.onPeersChanged();
                break;
            case WIFI_P2P_CONNECTION_CHANGED_ACTION:
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
                    Log.d(TAG,"CONN CHANGED CONNECTED");
                    listener.onConnected();
                } else {
                    Log.d(TAG,"CONN CHANGED NOT CONN");
                    listener.onDisConnected();
                }

                break;
            case WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                Log.d(TAG,"this device changed");

                break;


        }
    }


}
