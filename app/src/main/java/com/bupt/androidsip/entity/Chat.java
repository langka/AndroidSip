package com.bupt.androidsip.entity;

import com.bupt.androidsip.entity.sip.SipChat;

/**
 * Created by WHY on 2017/7/1.
 */

public class Chat {

    /**
     * @String name：
     *  用户名
     * @String headImageURL
     *  头像的URL
     * @int onlineStatue
     *  在线状态
     *      0：离线
     *      1：在线
     *      2：忙碌
     * @String lastMessage
     *  最后一句
     *
     *  注意，chat没有name这个域！！！使用sipchat 代替 name
     */

    public SipChat sipChat;
    public String name;
    public String headImageURL;
    public int onlineStatue;
    public String lastMessage;



    public Chat() {
    }

    public Chat(String name, String headImageURL, int onlineStatue, String lastChat) {
        this.name = name;

        this.headImageURL = "@drawable/xusong";

        this.onlineStatue = onlineStatue;
        this.lastMessage = lastChat;
    }

    public String getName() {
        return name;
    }

    public String getHeadImageURL() {
        return headImageURL;
    }

    public int getOnlineStatue() {
        return onlineStatue;
    }

    public String getLastMessage() {
        return lastMessage;
    }
}
