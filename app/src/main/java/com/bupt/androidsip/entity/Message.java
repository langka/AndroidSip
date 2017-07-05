package com.bupt.androidsip.entity;

/**
 * Created by vita-nove on 04/07/2017.
 */

public class Message {
    private String name;
    private int headImageURL;
    private String message;
    private boolean isFromMe;

    public boolean isFromMe() {
        return isFromMe;
    }

    public String getName() {
        return name;
    }

    public int getHeadImageURL() {
        return headImageURL;
    }

    public String getMessage() {
        return message;
    }

    public Message() {
    }

    public Message(String name, int headImageURL, String message, boolean isFromMe) {
        this.name = name;
        this.headImageURL = headImageURL;
        this.message = message;
        this.isFromMe = isFromMe;
    }


}
