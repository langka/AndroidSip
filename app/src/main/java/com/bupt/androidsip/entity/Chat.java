package com.bupt.androidsip.entity;

import com.bupt.androidsip.mananger.UserManager;


import com.bupt.androidsip.entity.sip.SipChat;

import android.os.Parcel;
import android.os.Parcelable;

import com.bupt.androidsip.R;
import com.bupt.androidsip.mananger.UserManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.bupt.androidsip.R.drawable.xusong;

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
     * <<<<<<< HEAD
     * 在线状态
     * 0：离线
     * 1：在线
     * 2：忙碌
     * @String lastMessage
     * 最后一句
     * <p>
     * 注意，chat没有name这个域！！！使用sipchat 代替 name
     */

    public SipChat sipChat;
    public String name;
    //    public String headImageURL;
    public String lastMessage;
    public String leftName;
    public int leftAvatar;
    public int rightAvatar;
    public int onlineStatue;
    public int ID;
    public ArrayList<Message> messages;
    public int unread;
    public long latestTime;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(ID);
        out.writeString(leftName);
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
        this.ID = parcel.readInt();
        this.leftName = parcel.readString();
        this.messages = parcel.readArrayList(null);
    }


    public Chat() {
    }

    public Chat(String leftName, int leftAvatar, int onlineStatue, String lastChat, int ID) {
        this.leftName = leftName;
        this.leftAvatar = leftAvatar;
        this.onlineStatue = onlineStatue;
        this.lastMessage = lastChat;
//        this.unread = (int) (1 + Math.random() * (10 - 1 + 1));
        this.unread = 0;
        this.ID = ID;
        this.messages = new ArrayList<>();
        this.rightAvatar = UserManager.getInstance().getUser().head;
    }

//    public void setLastChat(String msg) {
//        lastMessage = msg;
//    }
//
//    public void setLastMsgWithUnread(String msg) {
//        lastMessage = msg;
//        ++unread;
//    }
//
//    public void addMsgToList(Message msg) {
//        if (messages == null)
//            messages = new ArrayList<>();
//        messages.add(msg);
//    }

    public int getUnread() {
        return unread;
    }

    public void removeUnread() {
        unread = 0;
    }

    public String getLeftName() {
        return leftName;
    }

    public int getLeftAvatar() {
        return leftAvatar;
    }

    public int getOnlineStatue() {
        return onlineStatue;
    }

    public String getLastMessage() {
        return lastMessage;
    }

}
