package com.bupt.androidsip.entity.response;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.sip.SipChat;

import java.util.List;

/**
 * Created by xusong on 2017/7/8.
 * About:
 */

public class SipLoginResponse extends BaseResponse{
    public User self;
    public List<User> friends;
    public List<SipChat> groups;
}
