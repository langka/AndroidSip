package com.bupt.androidsip.sip.impl;

import android.content.Context;
import android.javax.sip.ClientTransaction;
import android.javax.sip.Dialog;
import android.javax.sip.DialogTerminatedEvent;
import android.javax.sip.IOExceptionEvent;
import android.javax.sip.InvalidArgumentException;
import android.javax.sip.ListeningPoint;
import android.javax.sip.ObjectInUseException;
import android.javax.sip.PeerUnavailableException;
import android.javax.sip.RequestEvent;
import android.javax.sip.ResponseEvent;
import android.javax.sip.SipException;
import android.javax.sip.SipFactory;
import android.javax.sip.SipListener;
import android.javax.sip.SipProvider;
import android.javax.sip.SipStack;
import android.javax.sip.TimeoutEvent;
import android.javax.sip.TransactionTerminatedEvent;
import android.javax.sip.TransactionUnavailableException;
import android.javax.sip.TransportNotSupportedException;
import android.javax.sip.address.AddressFactory;
import android.javax.sip.header.HeaderFactory;
import android.javax.sip.message.MessageFactory;
import android.javax.sip.message.Request;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.response.SipSendMsgResponse;
import com.bupt.androidsip.entity.sip.SipFailure;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.sip.ISipService;
import com.bupt.androidsip.sip.SipMessageListener;
import com.bupt.androidsip.sip.SipNetListener;
import com.bupt.androidsip.sip.SipSystemListener;
import com.bupt.androidsip.util.IpUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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

    Runnable exeTask1;//线程池中每一个线程执行的方法
    Runnable exeTask2;
    Runnable exeTask3;
    Runnable exeTask4;
    Runnable exeTask5;
    Runnable runner;

    private volatile boolean isWorkEnd = false;

    private boolean isInitialised = false;

    CountDownLatch initLock;
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
    private SipRequestBuilder requestBuilder;

    //对于一个序号message线程，要阻塞至它的response成功返回
    ConcurrentHashMap<Long, TaskListener> taskListeners;
    AtomicLong seq = new AtomicLong(0);


    private SipListener sipListener = new SipListener() {


        @Override
        public void processRequest(RequestEvent requestEvent) {

        }

        //对于发送消息，回调走这里，应当发送回主线程
        @Override
        public void processResponse(ResponseEvent responseEvent) {
            long local = responseEvent.getDialog().getLocalSeqNumber();
            TaskListener listener = taskListeners.get(local);
            //responseEvent.getResponse()
            int status = responseEvent.getResponse().getStatusCode();
            if (status == 200) {
                // TODO: 2017/7/8  完善response
                if (listener != null) {
                    if (listener.type == SipTaskType.MESSAGE)
                        handler.post(() -> {
                            listener.listener.onSuccess(new SipSendMsgResponse());
                        });
                }
            } else {
                if (listener != null) {
                    if (listener.type == SipTaskType.MESSAGE)
                        handler.post(() -> {
                            listener.listener.onFailure(new SipFailure("response不为OK，什么情况？" + status));
                        });
                }
            }
        }

        @Override
        public void processTimeout(TimeoutEvent timeoutEvent) {

        }

        @Override
        public void processIOException(IOExceptionEvent ioExceptionEvent) {

        }

        @Override
        public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

        }

        @Override
        public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

        }
    };

    /**
     * static methods
     */

    public static void prepareManager(Handler handler, Context context) {
        if (sipManager == null) {
            sipManager = new SipManager(handler, context);
            sipManager.initialise(context);
        }
    }

    public static SipManager getSipManager() {

        return sipManager;
    }


    /**
     * 私有方法
     */
    private SipManager(Handler handler, Context context) {
        this.handler = handler;
        initLock = new CountDownLatch(1);
        taskListeners = new ConcurrentHashMap<>();
        executor = Executors.newFixedThreadPool(5);//因为我们的消息不够密集，5个线程应该足够处理了
        taskQueue = new ArrayBlockingQueue<>(20, false);
    }

    //启动消息循环,并且发起异步的sip初始化过程，初始化成功就complete
    private void initialise(Context context) {
        new Thread(() -> {
            initSipStack(context);
            if (stackState == StackState.READY) {
                runner = () -> {
                    try {
                        initLock.await();
                        Log.d(TAG,"executor ready to work");
                        for (; !isWorkEnd; ) {
                            try {
                                SipTask sipTask = taskQueue.poll(2000, TimeUnit.MILLISECONDS);
                                if (sipTask != null)
                                    sipTask.r.run();
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
                exeTask1 = () -> runner.run();
                exeTask2 = () -> runner.run();
                exeTask3 = () -> runner.run();
                exeTask4 = () -> runner.run();
                exeTask5 = () -> runner.run();
                executor.submit(exeTask1);
                executor.submit(exeTask2);
                executor.submit(exeTask3);
                executor.submit(exeTask4);
                executor.submit(exeTask5);
            }
            isInitialised = true;
            handler.post(()->{
                Log.d(TAG,"HANDLER SUCCESS");
                Toast.makeText(context,"sip stack successfully init",Toast.LENGTH_SHORT).show();
            });
            initLock.countDown();
        }).start();
    }

    //初始化sip协议栈
    private void initSipStack(Context context) {
        stackState = StackState.INITIALISING;
        sipFactory = SipFactory.getInstance();
        sipFactory.resetFactory();
        sipFactory.setPathName("android.gov.nist");
        Properties properties = new Properties();
        sipProfile = new SipProfile();
        properties.setProperty("android.javax.sip.OUTBOUND_PROXY", sipProfile.getRemoteEndpoint()
                + "/" + sipProfile.getTransport());
        properties.setProperty("android.javax.sip.STACK_NAME", "AndroidSip");
        try {
            //清理出端口来，免得重复登录
            if (udpListeningPoint != null) {
                sipStack.deleteListeningPoint(udpListeningPoint);
                sipProvider.removeSipListener(sipListener);
            }
            sipStack = sipFactory.createSipStack(properties);
            Log.d(TAG, "sip stack init successfully!");
            headerFactory = sipFactory.createHeaderFactory();
            addressFactory = sipFactory.createAddressFactory();
            messageFactory = sipFactory.createMessageFactory();
            sipProfile.setLocalIp(IpUtil.getIPAddress(context));
            udpListeningPoint = sipStack.createListeningPoint(
                    sipProfile.getLocalIp(), sipProfile.getLocalPort(), sipProfile.getTransport());
            sipProvider = sipStack.createSipProvider(udpListeningPoint);
            sipProvider.addSipListener(sipListener);
            stackState = StackState.READY;
            requestBuilder = new SipRequestBuilder(addressFactory, sipProvider, messageFactory,
                    headerFactory, sipProfile);
        } catch (PeerUnavailableException | ObjectInUseException | TooManyListenersException |
                TransportNotSupportedException |
                InvalidArgumentException e) {
            stackState = StackState.ERROR;
        }


    }

    private void dealWithTask(SipTask task) {
        task.r.run();
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
        long current = seq.getAndIncrement();
        Request request = requestBuilder.buildLogin(message, current);
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

    //将普通message放入队列
    @Override
    public void sendMessage(SipMessage message, SipNetListener<SipSendMsgResponse> listener) {
        try {
            long current = seq.getAndIncrement();
            Request request = requestBuilder.buildMessage(message, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.MESSAGE));
            final ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
            try {
                taskQueue.put(new SipTask(() -> {
                    try {
                        transaction.sendRequest();
                    } catch (SipException e) {
                        listener.onFailure(new SipFailure("Sip访问出现异常"));
                        e.printStackTrace();
                    }
                }, SipTaskType.MESSAGE));
            } catch (InterruptedException e) {
                listener.onFailure(new SipFailure("Sip访问出现异常"));
                e.printStackTrace();
            }

        } catch (ParseException | InvalidArgumentException | TransactionUnavailableException e) {
            listener.onFailure(new SipFailure("无法解析的message！"));
        }
    }

    @Override
    public void getUserInfo(int id, SipNetListener listener) {

    }

    @Override
    public void modifyUserInfo(User info) {

    }


    //因为不能在主线程访问网络，所以需要建造一个网络访问队列,这里是队列里每个节点
    static class SipTask {
        Runnable r;
        SipTaskType taskType;

        public SipTask(Runnable r, SipTaskType taskType) {
            this.r = r;
            this.taskType = taskType;
        }
    }

    enum SipTaskType {
        MESSAGE;
    }

    static class TaskListener {
        SipNetListener listener;
        SipTaskType type;

        public TaskListener(SipNetListener listener, SipTaskType type) {
            this.listener = listener;
            this.type = type;
        }
    }


}
