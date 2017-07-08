package com.bupt.androidsip.sip;

import com.bupt.androidsip.entity.sip.SipEvent;
import com.bupt.androidsip.entity.sip.SipFailure;
import com.bupt.androidsip.entity.User;

/**
 * Created by xusong on 2017/7/8.
 * About:收到系统消息，包括很多
 */

public interface SipSystemListener {

    void onAddFriendSuccess(User newFriend);

    void onAddFriendFailed(User f, SipFailure failure);

    void onReceiveFriendInvite(User from);

    void onFriendInfoChange(User friend);

    void onServerEvent(SipEvent event);

}
