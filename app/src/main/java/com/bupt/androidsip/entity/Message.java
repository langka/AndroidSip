package com.bupt.androidsip.entity;

/**
 * Created by vita-nove on 05/07/2017.
 */

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = -6240488099748291325L;
    public int rightAvatar;
    public int leftAvatar;
    public String content;
    public String time;
    public int fromOrTo;// 0 是收到的消息，1是发送的消息
    public int ID;

    @Override
    public String toString() {
        return "MessageEntity [rightAvatar=" + rightAvatar
                + ", leftAvatar=" + leftAvatar + ", content=" + content
                + ", time=" + time + ", fromOrTo=" + fromOrTo + "]";
    }
}

