package com.bupt.androidsip.entity;

import com.bupt.androidsip.util.PinyinUtils;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import demo.Pinyin4jAppletDemo;

/**
 * Created by xusong on 2017/7/3.
 * About:
 */

public class Friend {
    public String getSortLetters() {
        if (sortLetters == null)
            sortLetters = PinyinUtils.getPingYin(name).toUpperCase();
        return sortLetters;
    }

    private String sortLetters;

    public String name;

    public int state;//0 offline   1 online

    public int head;
    public String headUrl;

    public String desc;

    public static List<Friend> fakeFriends;

    static {
        int j = 0;
        Random random = new Random();
        fakeFriends = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            Friend friend = new Friend();
            friend.name = "FakeUser" + i;
            int x = random.nextInt(100) % 25;
            friend.name = ((char) (x + 'A')) + friend.name;
            friend.state = random.nextInt() % 2;
            friend.desc = "fake user desc " + i;
            friend.headUrl = null;
            friend.head = random.nextInt() % 2;
            fakeFriends.add(friend);
        }
    }

}
