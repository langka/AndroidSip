package com.bupt.androidsip.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WHY on 2017/7/1.
 */

public class User {

    public String headUrl;
    public int head;
    public String name;
    public List<Chat> chatList;

    public List<Friend> friends = Friend.fakeFriends;


    private void init() {
        headUrl = "";
        head = 1;
        name = "lyk";
//        chatList = new ArrayList<>();
//        Chat f1 = new Chat();
//        f1.headImageURL = "";
//        f1.name = "gy";
//        f1.onlineStatue = 1;
//        f1.signature = "帅帅";
//        Chat f2 = new Chat();
//        f2.headImageURL = "";
//        f2.name = "why";
//        f2.signature = "丑出丑";
//        f2.onlineStatue = 1;
//        chatList.add(f1);
//        chatList.add(f2);

    }

    public User() {
        init();
    }
}
