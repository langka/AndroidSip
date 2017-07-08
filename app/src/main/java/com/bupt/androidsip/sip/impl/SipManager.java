package com.bupt.androidsip.sip.impl;

import android.os.Handler;
import android.util.Log;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.sip.ISipService;
import com.bupt.androidsip.sip.SipMessageListener;
import com.bupt.androidsip.sip.SipNetListener;
import com.bupt.androidsip.sip.SipSystemListener;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by xusong on 2017/7/8.
 * About:这个类将会为ISIPservice提供实现！，是最重要的类
 */

public class SipManager implements ISipService {

    public static final String TAG = "sipmanagertag";
    private static SipManager sipManager;

    /**
     * 域成员
     */
    BlockingQueue<SipTask> taskQueue;
    ExecutorService executor;
    Handler handler;//为了让回调在主线程中进行，需要一个handler

    Runnable exeTask;//线程池中每一个线程执行的方法
    private volatile boolean isWorkEnd = false;
    private boolean isInitialised = false;

    SipMessageListener messageListener;
    SipSystemListener systemListener;

    /**
     * static methods
     */

    public static SipManager getSipManager(Handler handler) {
        if (sipManager == null) {
            sipManager = new SipManager(handler);
            sipManager.initialise();
        }
        return sipManager;
    }

    public static SipManager getSipManager() {

        return sipManager;
    }


    /**
     * 私有方法
     */
    private SipManager(Handler handler) {
        this.handler = handler;
        executor = Executors.newSingleThreadExecutor();//因为我们的消息不够密集，单个线程应该足够处理了
        taskQueue = new ArrayBlockingQueue<>(20, false);
    }

    //启动消息循环
    private void initialise() {
        exeTask = () -> {
            for (; !isWorkEnd; ) {
                try {
                    SipTask sipTask = taskQueue.poll(2000, TimeUnit.MILLISECONDS);
                    dealWithTask(sipTask);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isWorkEnd = true;
                }
            }
            Log.d(TAG, "EXECUTOR EXIT");
        };
        executor.submit(exeTask);
        isInitialised = true;
    }

    private void dealWithTask(SipTask task) {

    }


    /**
     * 以下为sip的响应事件
     */
    public void setMessageListener(SipMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setSystemListener(SipSystemListener systemListener) {
        this.systemListener = systemListener;
    }

    /**
     * 以下为对ISipService的实现，主动事件
     */
    @Override
    public void register(String name, String password, SipNetListener listener) {

    }

    @Override
    public void login(String name, String password, SipNetListener listener) {

    }

    @Override
    public void addFriend(int id, SipNetListener listener) {

    }

    @Override
    public void declineFriendInvite(int id, SipNetListener listener) {

    }

    @Override
    public void acceptFriendInvite(int id, SipNetListener listener) {

    }

    @Override
    public void sendMessage(SipMessage message, SipNetListener listener) {

    }

    @Override
    public void getUserInfo(int id, SipNetListener listener) {

    }

    @Override
    public void modifyUserInfo(User info) {

    }


    //因为不能在主线程访问网络，所以需要建造一个网络访问队列
    static class SipTask implements Runnable {

        @Override
        public void run() {

        }
    }

}
