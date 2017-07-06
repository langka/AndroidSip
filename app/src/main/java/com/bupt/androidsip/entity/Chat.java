package com.bupt.androidsip.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by WHY on 2017/7/1.
 */

public class Chat {

    /**
     * @String leftName：
     * 用户名
     * @String leftAvatar
     * 头像的URL
     * @int onlineStatue
     * 在线状态
     * 0：离线
     * 1：在线
     * 2：忙碌
     * @String lastChat
     * 最后一句
     */
    public String leftName;
    public String leftAvatar;
    public int onlineStatue;
    public String lastChat;
    public List chats;
    public LinkedList<Message> messages;
    private int unread;

    public Chat() {
    }

    public Chat(String leftName, String leftAvatar, int onlineStatue, String lastChat) {
        this.leftName = leftName;
        this.leftAvatar = "@drawable/xusong";
        this.onlineStatue = onlineStatue;
        this.lastChat = lastChat;
        this.unread = (int) (1 + Math.random() * (10 - 1 + 1));
    }

    public void addMessage(Message msg) {
        if (messages == null)
            messages = new LinkedList<>();
        messages.add(msg);
        lastChat = msg.toString();
        ++unread;
    }

    public int getUnread() {
        return unread;
    }

    public void removeUnread() {
        unread = 0;
    }

    public String getLeftName() {
        return leftName;
    }

    public String getLeftAvatar() {
        return leftAvatar;
    }

    public int getOnlineStatue() {
        return onlineStatue;
    }

    public String getLastChat() {
        return lastChat;
    }
}
