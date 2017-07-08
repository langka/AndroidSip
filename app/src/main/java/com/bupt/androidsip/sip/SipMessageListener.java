package com.bupt.androidsip.sip;

import com.bupt.androidsip.entity.sip.SipMessage;

/**
 * Created by xusong on 2017/7/8.
 * About:
 */

public interface SipMessageListener {

    void onNewMessage(SipMessage message);

}
