package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.User;

/**
 * Created by xusong on 2017/5/17.
 * About:用户管理！
 */

public class UserManager {
    public User getUser(){
        return null;
    }
    private static UserManager instance = new UserManager();
    private UserManager() {

    }
    public static UserManager getInstance(){
        return instance;
    }


}
