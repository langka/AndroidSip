package com.bupt.androidsip.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by WHY on 2017/7/1.
 */

public class Chat implements Parcelable {

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
    public int ID;
    public ArrayList<Message> messages;
    private int unread;

    public int describeContents() {
        //几乎所有情况都返回0，仅在当前对象中存在文件描述符时返回1
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(leftAvatar);
        out.writeList(messages);
    }

    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public Chat(Parcel parcel) {
        this.leftAvatar = parcel.readString();
        this.messages = parcel.readArrayList(null);
    }


    public Chat() {
    }

    public Chat(String leftName, String leftAvatar, int onlineStatue, String lastChat, int ID) {
        this.leftName = leftName;
        this.leftAvatar = "@drawable/xusong";
        this.onlineStatue = onlineStatue;
        this.lastChat = lastChat;
        this.unread = (int) (1 + Math.random() * (10 - 1 + 1));
        this.ID = ID;
    }

    public void setLastChat(String msg) {
        lastChat = msg;
        ++unread;
    }

    public void setLastMsgWithUnread(String msg) {
        lastChat = msg;
        ++unread;
    }

    public void addMsgToList(Message msg) {
        if (messages == null)
            messages = new ArrayList<>();
        messages.add(msg);
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
