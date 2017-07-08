package com.bupt.androidsip.sip;

import com.bupt.androidsip.entity.sip.SipFailure;
import com.bupt.androidsip.entity.sip.SipResponse;

/**
 * Created by xusong on 2017/7/8.
 * About:
 */

public interface SipNetListener {
    void onSuccess(SipResponse response);//成功回调

    void onFailure(SipFailure failure);//失败回调
}
