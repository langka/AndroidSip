package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.User;

/**
 * Created by xusong on 2017/5/17.
 * About:用户管理！
 */

public class UserManager {
    private User user = new User();

    public User getUser() {
        return user;
    }

    private static UserManager instance = new UserManager();

    private UserManager() {
        user = null;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static UserManager getInstance() {
        return instance;
    }


}
