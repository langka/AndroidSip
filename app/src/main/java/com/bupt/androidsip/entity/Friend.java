package com.bupt.androidsip.entity;

/**
 * Created by WHY on 2017/7/1.
 */

public class Friend {
    public String name;
    public String headImageURL;
    public String onlineStatue;
    public String signature;

    public Friend() {
    }

    public Friend(String name, String headImageURL, String onlineStatue, String signature) {
        this.name = name;
        this.headImageURL = headImageURL;
        this.onlineStatue = onlineStatue;
        this.signature = signature;
    }


    public String getName() {
        return name;
    }

    public String getHeadImageURL() {
        return headImageURL;
    }

    public String getOnlineStatue() {
        return onlineStatue;
    }

    public String getSignature() {
        return signature;
    }
}
