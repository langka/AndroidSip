package com.bupt.androidsip.entity;

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
     * @String lastChat
     *  最后一句
     */
    public String name;
    public String headImageURL;
    public int onlineStatue;
    public String lastChat;

    public Chat() {
    }

    public Chat(String name, String headImageURL, int onlineStatue, String lastChat) {
        this.name = name;
        this.headImageURL = headImageURL;
        this.onlineStatue = onlineStatue;
        this.lastChat = lastChat;
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

    public String getLastChat() {
        return lastChat;
    }
}
