package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.Friend;
import com.bupt.androidsip.entity.User;

import java.util.List;

/**
 * Created by vita-nove on 08/07/2017.
 */

public class FriendManager {
    private List<User> friends;

    public List<User> getFriends() {
        return friends;
    }

    private static FriendManager instance = new FriendManager();

    private FriendManager() {
        friends = null;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public static FriendManager getInstance() {
        return instance;
    }

    //加好友
    //删好友
}
