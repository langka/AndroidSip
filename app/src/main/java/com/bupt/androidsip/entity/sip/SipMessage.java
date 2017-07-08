package com.bupt.androidsip.entity.sip;

import com.bupt.androidsip.entity.User;

import java.util.List;

/**
 * Created by xusong on 2017/7/6.
 * About:
 */

public class SipMessage {
    public int type;//0 normal   1 picture   2 file
    public long startTime;//消息创建时间
    public long comeTime;//消息到达时间
    public String content;//内容，如果是图片为url，如果是文本就是文本内容
    public SipChat belong;//所属的chat
    public User from;//发送方
    public List<User> to;

}
