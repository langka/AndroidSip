package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.response.SipLoginResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xusong on 2017/5/17.
 * About:用户管理！
 */

public class UserManager {
    private User user = new User();
    public List<User> allPeople = new ArrayList<>();//这是本次所知道的全部user,应当在登录成功后初始化

    public User getUser() {
        return user;
    }

    private static UserManager instance = new UserManager();

    private UserManager() {
    }

    public void initUser(SipLoginResponse response) {
        user = response.self;
        user.friends = response.friends;
        allPeople = response.friends;
    }

    public static UserManager getInstance() {
        return instance;
    }

    //根据id查询user
    public User searchUser(int id) {
        for (User u : allPeople) {
            if (u.id == id)
                return u;
        }
        return null;
    }

    // TODO: 2017/7/12 删除好友
    public boolean deleteFriend(int id) {
        user.friends.remove(user.friends.get(id));
        return false;
    }


}
