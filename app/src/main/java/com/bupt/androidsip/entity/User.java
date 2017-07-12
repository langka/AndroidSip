package com.bupt.androidsip.entity;

import com.bupt.androidsip.util.PinyinUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WHY on 2017/7/1.
 */

public class User {

    public String description;
    public int head;
    public String name;
    public String sex;
    public String email;
    public long registerTime;
    public int id;


    public String headUrl;
    public int state = 0;

    public List<User> friends = Friend.fakeFriends;


    private void init() {
        head = 1;
        name = "lyk";
//        chatList = new ArrayList<>();
//        Chat f1 = new Chat();
//        f1.leftAvatar = "";
//        f1.leftName = "gy";
//        f1.onlineStatue = 1;
//        f1.signature = "帅帅";
//        Chat f2 = new Chat();
//        f2.leftAvatar = "";
//        f2.leftName = "why";
//        f2.signature = "丑出丑";
//        f2.onlineStatue = 1;
//        chatList.add(f1);
//        chatList.add(f2);


    }

    public User() {
        init();
    }


    public String getSortLetters() {
        if (sortLetters == null)
            sortLetters = PinyinUtils.getPingYin(name).toUpperCase();
        return sortLetters;
    }

    private String sortLetters;


    public static User createFromJson(JSONObject object) {
        User user = new User();
        try {
            user.name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            user.name = "???";
        }
        try {
            user.head = object.getInt("head");
        } catch (JSONException e) {
            e.printStackTrace();
            user.head = 1;
        }
        try {
            user.description = object.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            user.description="";
        }
        try {
            user.email = object.getString("email");
        } catch (JSONException e) {
            user.email="";
            e.printStackTrace();
        }
        try {
            user.sex = object.getString("sex");
        } catch (JSONException e) {
            user.sex="男";
            e.printStackTrace();
        }
        try {
            user.id = object.getInt("id");
        } catch (JSONException e) {
            user.id = -1;
            e.printStackTrace();
        }
        return user;
    }
}
