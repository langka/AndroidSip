package com.bupt.androidsip.mananger;

/**
 * Created by xusong on 2017/5/17.
 * About:
 */

public interface BaseListener {
    public void onSuccess(String response);
    public void onFailure(String response);
}
