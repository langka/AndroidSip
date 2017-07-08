package com.bupt.androidsip.sip;

import com.bupt.androidsip.entity.response.SipLoginResponse;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.entity.User;

/**
 * Created by xusong on 2017/7/8.
 * About:SIP 所提供的service,由最上层调用,如点击一个button
 */

public interface ISipService {

    void register(String name, String password, SipNetListener listener);

    void login(String name, String password, SipNetListener<SipLoginResponse> listener);

    void addFriend(int id, SipNetListener listener);//不需要提供我的id，可以在usermanager获得,这个成功回调值得并非已经添加好友，而是好友请求被服务器受理,
    //下面同理

    void declineFriendInvite(int id,SipNetListener listener);//拒绝一个用户好友邀请

    void acceptFriendInvite(int id,SipNetListener listener);//接受好友邀请

    void sendMessage(SipMessage message, SipNetListener listener);//发送信息，不需要提供接收方，因为sipmessage已经包含接收方和发送方

    void getUserInfo(int id, SipNetListener listener);//获取一个用户的信息

    void modifyUserInfo(User info);//将指定的用户信息更改


}
