package com.bupt.androidsip.sip.impl;

import android.javax.sip.Dialog;
import android.javax.sip.ListeningPoint;
import android.javax.sip.ObjectInUseException;
import android.javax.sip.PeerUnavailableException;
import android.javax.sip.SipFactory;
import android.javax.sip.SipListener;
import android.javax.sip.SipProvider;
import android.javax.sip.SipStack;
import android.javax.sip.address.AddressFactory;
import android.javax.sip.header.HeaderFactory;
import android.javax.sip.message.MessageFactory;
import android.javax.sip.message.Request;
import android.os.Handler;
import android.util.Log;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.sip.ISipService;
import com.bupt.androidsip.sip.SipMessageListener;
import com.bupt.androidsip.sip.SipNetListener;
import com.bupt.androidsip.sip.SipSystemListener;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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

    Semaphore initLock;
    SipMessageListener messageListener;
    SipSystemListener systemListener;


    //以下为sip变量
    private SipProvider sipProvider;
    private HeaderFactory headerFactory;
    private AddressFactory addressFactory;
    private MessageFactory messageFactory;
    private SipFactory sipFactory;
    private ListeningPoint udpListeningPoint;
    private SipProfile sipProfile;
    private Dialog dialog;
    private Request ackRequest;
    private static SipStack sipStack;
    private volatile StackState stackState = StackState.UNINIT;

    private SipListener sipListener;

    /**
     * static methods
     */

    public static void prepareManager(Handler handler) {
        if (sipManager == null) {
            sipManager = new SipManager(handler);
            sipManager.initialise();
        }
    }

    public static SipManager getSipManager() {

        return sipManager;
    }


    /**
     * 私有方法
     */
    private SipManager(Handler handler) {
        this.handler = handler;
        initLock = new Semaphore(0);
        executor = Executors.newSingleThreadExecutor();//因为我们的消息不够密集，单个线程应该足够处理了
        taskQueue = new ArrayBlockingQueue<>(20, false);
    }

    //启动消息循环,并且发起异步的sip初始化过程，初始化成功就complete
    private void initialise() {
        new Thread(() -> {
            initSipStack();
            exeTask = () -> {
                try {
                    initLock.acquire();
                    for (; !isWorkEnd; ) {
                        try {
                            SipTask sipTask = taskQueue.poll(2000, TimeUnit.MILLISECONDS);
                            dealWithTask(sipTask);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            isWorkEnd = true;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "EXECUTOR EXIT");
            };
            executor.submit(exeTask);
            isInitialised = true;
            initLock.release();
        }).start();
    }

    //初始化sip协议栈
    private void initSipStack() {
        stackState = StackState.INITIALISING;
        sipFactory = SipFactory.getInstance();
        sipFactory.resetFactory();
        sipFactory.setPathName("android.gov.nist");
        Properties properties = new Properties();
        properties.setProperty("android.javax.sip.OUTBOUND_PROXY", sipProfile.getRemoteEndpoint()
                + "/" + sipProfile.getTransport());
        properties.setProperty("android.javax.sip.STACK_NAME", "AndroidSip");
        try {
            if (udpListeningPoint != null) {
                // Binding again
                sipStack.deleteListeningPoint(udpListeningPoint);
                sipProvider.removeSipListener(null);
            }
            sipStack = sipFactory.createSipStack(properties);
            System.out.println("createSipStack " + sipStack);
        } catch (PeerUnavailableException | ObjectInUseException e) {
            stackState = StackState.ERROR;
        }

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
