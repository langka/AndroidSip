package com.bupt.androidsip.entity;

/**
 * Created by WHY on 2017/7/1.
 */

public class Friend {

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
     */
    public String name;
    public String headImageURL;
    public int onlineStatue;
    public String signature;

    public Friend() {
    }

    public Friend(String name, String headImageURL,  onlineStatue, String signature) {
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

    public int getOnlineStatue() {
        return onlineStatue;
    }

    public String getSignature() {
        return signature;
    }
}
