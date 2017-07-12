package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.response.SipLoginResponse;

import java.util.List;

/**
 * Created by xusong on 2017/5/17.
 * About:用户管理！
 */

public class UserManager {
    private User user = new User();

    public List<User> frs;//这是本次所知道的全部user,应当在登录成功后初始化

    public User getUser() {
        return user;
    }

    private static UserManager instance = new UserManager();

    private UserManager() {
    }

    public void initUser(SipLoginResponse response) {
        user = response.self;
        frs = response.friends;
    }

    public static UserManager getInstance() {
        return instance;
    }

    //根据id查询user
    public User searchUser(int id) {
        for (User u : frs) {
            if (u.id == id)
                return u;
        }
        return null;
    }

}
