package com.bupt.androidsip.mananger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bupt.androidsip.receiver.WiFiDirectReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by xusong on 2017/7/4.
 * About:
 */

public class WifiDirectManager {

    private static final String TAG = "WIFIDIRECTMANAGER";
    private static WifiDirectManager instance;
    WifiP2pManager p2pManager;
    WifiP2pManager.Channel channel;
    Context context;
    IntentFilter filter;

    BroadcastReceiver receiver;


    WifiListener wifiListener;
    WifiChatManager chatManager;


    public WifiChatManager getChatManager() {
        return chatManager;
    }

    public void prepare() {
        if (receiver == null) {
            filter = new IntentFilter();
            filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);       // WiFi P2P是否可用
            filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);       // peers列表发生变化
            filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);  // WiFi P2P连接发生变化
            filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION); // WiFi P2P设备信息发生变化(比如更改了设备名)
            receiver = new WiFiDirectReceiver(new WiFiDirectReceiver.OnReceiveListener() {
                @Override
                public void OnFindPeers() {
                    p2pManager.requestPeers(channel, peers -> {
                        wifiListener.onFindDevices(peers);
                    });
                }

                @Override
                public void onDisConnected() {
                    Toast.makeText(context,"连接断开",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConnected() {
                    p2pManager.requestConnectionInfo(channel, info -> {//广播告诉我连接成功，现在应当准备好socket了
                        InetAddress address = info.groupOwnerAddress;
                        if (info.groupFormed && info.isGroupOwner) {
                            executeServerWork(socket -> {
                                chatManager = WifiChatManager.createChatManager(0, socket);
                                wifiListener.onChatPrepared();
                            });
                        } else if (info.groupFormed) {
                            executeClientWork(address, socket -> {
                                chatManager = WifiChatManager.createChatManager(1, socket);
                                wifiListener.onChatPrepared();
                            });
                        }

                    });
                }

                @Override
                public void onP2PDisabled() {
                    Toast.makeText(context, "P2P disabled,可以检查是否开了wifi", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onP2PEnabled() {
                    Toast.makeText(context, "P2P enabled,请稍后", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPeersChanged() {
                    p2pManager.requestPeers(channel, peers -> {
                        wifiListener.onFindDevices(peers);
                    });
                }
            });
        }
        context.registerReceiver(receiver, filter);
    }

    public void release() {
        context.unregisterReceiver(receiver);
    }

    public void setWifiListener(WifiListener wifiListener) {
        this.wifiListener = wifiListener;
    }

    private WifiDirectManager(Context context) {
        this.context = context;
        p2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = p2pManager.initialize(context, context.getMainLooper(), null);

    }


    public static WifiDirectManager getInstance(Context context) {
        if (instance == null) {
            instance = new WifiDirectManager(context);
        }
        return instance;
    }

    public void beginSearch() {//开始查找周围的wifi
        wifiListener.onSearchBegin();
        p2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "SUCCESSSS");
                p2pManager.requestPeers(channel, peers -> {
                    Toast.makeText(context, "PEERS AVAILABLE", Toast.LENGTH_SHORT).show();
                    wifiListener.onFindDevices(peers);
                });
                wifiListener.onSuccessStart();
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "FAILLLL");
            }
        });
    }

    private void executeServerWork(OnSocketConnectedListener listener) {
        Thread serverThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(6666);
                Socket s = serverSocket.accept();
                listener.onSocketConnected(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    private void executeClientWork(InetAddress address, OnSocketConnectedListener listener) {
        Thread clientThread = new Thread(() -> {
            Socket socket = new Socket();
            try {
                socket.bind(null);
                socket.connect(new InetSocketAddress(address.getHostAddress(), 6666), 8000);
                listener.onSocketConnected(socket);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
        });
        clientThread.start();
    }

    public void connect(WifiP2pDevice device) {
        Log.d(TAG, "开始 链接");
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        p2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // 连接成功
                Toast.makeText(context, "与设备" + device.deviceName + "连接成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int arg0) {
                // 连接失败
                Toast.makeText(context, "与设备" + device.deviceName + "连接失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    private interface OnSocketConnectedListener {
        void onSocketConnected(Socket socket);
    }

    public interface WifiListener {
        void onSearchBegin();

        void onSuccessStart();//成功intialised

        void onFailStart();//初始化失败

        void OnSearchEnd();

        void onFindDevices(WifiP2pDeviceList devices);

        void onChatPrepared();
    }

    public static class WifiChatManager {
        int type;//0服务器，1客户端
        Socket socket;
        OutputStream os;
        InputStream is;
        Handler handler;

        int state = 0;//0代表流可用

        public static WifiChatManager createChatManager(int type, Socket socket) {
            return new WifiChatManager(type, socket);
        }

        public WifiChatManager(int type, Socket socket) {
            this.type = type;
            this.socket = socket;
            try {
                os = socket.getOutputStream();
                is = socket.getInputStream();
            } catch (IOException e) {
                state = 1;
                e.printStackTrace();
            }
            new Thread(() -> {
                byte[] buffer = new byte[1024];
                for (; ; ) {//一个线程始终在读
                    if (state == 0) {
                        try {
                            int len = is.read(buffer);
                            if (len == -1)
                                break;
                            else {
                                byte[] b = new byte[len];
                                System.arraycopy(buffer, 0, b, 0, len);
                                String msg = new String(b);
                                if (handler != null)
                                    handler.post(() -> listener.onNewMessage(msg));
                            }
                        } catch (IOException e) {
                            state = 1;
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }).start();
        }

        MessageListener listener;

        public void registerChatListener(MessageListener listener, Handler handler) {
            this.listener = listener;
            this.handler = handler;
        }

        public void sendMessage(String s) {
            if (state == 0) {
                new Thread(() -> {//现成资源浪费很严重,只需要1个线程就行的，现在不管
                    try {
                        os.write(s.getBytes());
                        os.flush();
                    } catch (IOException e) {
                        state = 1;
                        e.printStackTrace();
                    }
                }).start();

            }
        }

    }

    public interface MessageListener {
        void onNewMessage(String message);
    }

}
