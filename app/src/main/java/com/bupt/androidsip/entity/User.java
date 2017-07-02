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
    public List<Friend> friendList;

    private void init() {
        headUrl = "";
        head = 1;
        name = "lyk";
        friendList = new ArrayList<>();
        Friend f1 = new Friend();
        f1.headImageURL = "";
        f1.name = "gy";
        f1.onlineStatue = "online";
        f1.signature = "帅帅";
        Friend f2 = new Friend();
        f2.headImageURL = "";
        f2.name = "why";
        f2.signature = "丑出丑";
        f2.onlineStatue = "offline";
        friendList.add(f1);
        friendList.add(f2);

    }

    public User() {
        init();
    }
}
