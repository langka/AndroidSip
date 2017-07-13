package com.bupt.androidsip.sip;

import com.bupt.androidsip.entity.sip.SipEvent;
import com.bupt.androidsip.entity.sip.SipFailure;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.sip.SipSystemMessage;

/**
 * Created by xusong on 2017/7/8.
 * About:收到系统消息，包括很多
 */

public interface SipSystemListener {

    void onNewSystemEvent(SipSystemMessage message);

    void onFriendInfoChange(User friend);

    void onServerEvent(SipEvent event);

}
