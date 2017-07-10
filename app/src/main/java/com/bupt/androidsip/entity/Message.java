package com.bupt.androidsip.entity;

/**
 * Created by vita-nove on 05/07/2017.
 */

import com.bupt.androidsip.mananger.UserManager;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = -6240488099748291325L;
    public int rightAvatar;
    public int leftAvatar;
    public String content;
    public long time;
    public int fromOrTo;// 0 是收到的消息，1是发送的消息
    public int ID;

    public Message(String msg) {
        this.content = msg;
    }

    public Message() {
        rightAvatar = UserManager.getInstance().getUser().head;
    }

    @Override
    public String toString() {
        return "MessageEntity [rightAvatar=" + rightAvatar
                + ", leftAvatar=" + leftAvatar + ", content=" + content
                + ", time=" + time + ", fromOrTo=" + fromOrTo + "]";
    }
}

