package com.bupt.androidsip.sip;

/**
 * Created by xusong on 2017/7/8.
 * About:
 */

public interface SipNetListener<T> {
    void onSuccess(T response);//成功回调

    void onFailure(T failure);//失败回调
}
