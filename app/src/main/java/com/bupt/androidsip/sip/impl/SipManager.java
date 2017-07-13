package com.bupt.androidsip.sip.impl;

import android.app.FragmentManager;
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
import android.javax.sip.ServerTransaction;
import android.javax.sip.SipException;
import android.javax.sip.SipFactory;
import android.javax.sip.SipListener;
import android.javax.sip.SipProvider;
import android.javax.sip.SipStack;
import android.javax.sip.TimeoutEvent;
import android.javax.sip.TransactionTerminatedEvent;
import android.javax.sip.TransactionUnavailableException;
import android.javax.sip.TransportNotSupportedException;
import android.javax.sip.address.Address;
import android.javax.sip.address.AddressFactory;
import android.javax.sip.address.SipURI;
import android.javax.sip.address.URI;
import android.javax.sip.header.CSeqHeader;
import android.javax.sip.header.CallIdHeader;
import android.javax.sip.header.ContactHeader;
import android.javax.sip.header.ContentTypeHeader;
import android.javax.sip.header.FromHeader;
import android.javax.sip.header.HeaderFactory;
import android.javax.sip.header.MaxForwardsHeader;
import android.javax.sip.header.RouteHeader;
import android.javax.sip.header.SupportedHeader;
import android.javax.sip.header.ToHeader;
import android.javax.sip.header.ViaHeader;
import android.javax.sip.message.MessageFactory;
import android.javax.sip.message.Request;
import android.javax.sip.message.Response;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;
import android.webkit.ClientCertRequest;
import android.widget.Toast;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.response.SipAcceptResponse;
import com.bupt.androidsip.entity.response.SipAddResponse;
import com.bupt.androidsip.entity.response.SipDeclineResponse;
import com.bupt.androidsip.entity.response.SipLoginResponse;
import com.bupt.androidsip.entity.response.SipSearchResponse;
import com.bupt.androidsip.entity.response.SipSendMsgResponse;
import com.bupt.androidsip.entity.sip.SipFailure;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.entity.sip.SipSystemMessage;
import com.bupt.androidsip.mananger.DBManager;
import com.bupt.androidsip.sip.ISipService;
import com.bupt.androidsip.sip.SipMessageListener;
import com.bupt.androidsip.sip.SipNetListener;
import com.bupt.androidsip.sip.SipSystemListener;
import com.bupt.androidsip.util.IpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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

    public static final String SERVICE_ADD = "add_friend";
    public static final String SERVICE_CHAT = "private_chat";
    public static final String SERVICE_DECLINE = "decline_friend";
    public static final String SERVICE_ACC = "acc_friend";
    public static final String SERVICE_SEARCH = "search";

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

    public SipProfile getSipProfile() {
        return sipProfile;
    }

    Context context;

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
    AtomicLong seq = new AtomicLong(1);

    private void setUpDialog() {

    }

    private void send200Ok(RequestEvent event) {
        Response response;
        try {
            response = messageFactory.createResponse(200,
                    event.getRequest());
            new Thread(() -> {
                try {
                    ServerTransaction serverTransaction = event
                            .getServerTransaction();
                    if (serverTransaction == null) {
                        serverTransaction = sipProvider
                                .getNewServerTransaction(event.getRequest());
                    }
                    serverTransaction.sendResponse(response);
                } catch (SipException | InvalidArgumentException e) {
                    e.printStackTrace();
                }
            }).start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessageResponse(ResponseEvent event, SipNetListener<SipSendMsgResponse> listener) {
        if (event.getResponse().getStatusCode() == 200) {
            handler.post(() -> listener.onSuccess(new SipSendMsgResponse()));

        } else handler.post(() -> listener.onFailure(new SipFailure("status code != 200")));
        ;

    }

    private void processLoginResponse(ResponseEvent event, SipNetListener<SipLoginResponse> listener) {
        int status = event.getResponse().getStatusCode();
        if (status == 200) {
            try {
                byte[] dd = event.getResponse().getRawContent();
                String x = new String(dd);
                JSONObject object = new JSONObject(x);
                JSONArray array = object.getJSONArray("users");
                SipLoginResponse response = new SipLoginResponse();
                JSONObject me = array.getJSONObject(0);
                response.self = User.createFromJson(me);
                List<User> friends = new ArrayList<>();
                try {
                    for (int i = 0; i < array.length(); i++) {//每个人它自己也会是自己的好友
                        friends.add(User.createFromJson(array.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                response.friends = friends;
                List<SipMessage> messages = new ArrayList<>();
                try {
                    JSONArray msgs = object.getJSONArray("msg");
                    for (int i = 0; i < msgs.length(); i++) {
                        messages.add(SipMessage.createFromJson(msgs.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                response.offlineMessages = messages;
                handler.post(() -> listener.onSuccess(response));
                sendSubScribeForFriendState();
                establishDialog();
            } catch (JSONException e) {
                handler.post(() -> listener.onFailure(new SipFailure("无法解析的json串!")));
                e.printStackTrace();
            }
        } else if (status == 401) {
            byte[] reasonb = (byte[]) event.getResponse().getContent();
            String reason = new String(reasonb);
            handler.post(() -> listener.onFailure(new SipFailure(reason)));
        }
    }

    private void processAddFriendResponse(ResponseEvent event, SipNetListener<SipAddResponse> listener) {
        if (event.getResponse().getStatusCode() == 200) {
            handler.post(() -> listener.onSuccess(new SipAddResponse()));
        } else {
            byte[] dd = event.getResponse().getRawContent();
            String x = new String(dd);
            try {
                JSONObject jsonObject = new JSONObject(x);
                String reason = jsonObject.getString("reason");
                handler.post(() -> listener.onFailure(new SipFailure(reason)));
            } catch (JSONException e) {
                handler.post(() -> listener.onFailure(new SipFailure("异常故障，请稍后重试")));
                e.printStackTrace();
            }

        }
    }

    private void processDeclineFriendResponse(ResponseEvent event, SipNetListener<SipDeclineResponse> listener) {
        if (event.getResponse().getStatusCode() == 200) {
            handler.post(() -> listener.onSuccess(new SipDeclineResponse()));
        } else {
            byte[] dd = event.getResponse().getRawContent();
            String x = new String(dd);
            try {
                JSONObject jsonObject = new JSONObject(x);
                String reason = jsonObject.getString("reason");
                handler.post(() -> listener.onFailure(new SipFailure(reason)));
            } catch (JSONException e) {
                handler.post(() -> listener.onFailure(new SipFailure("异常故障，请稍后重试")));
                e.printStackTrace();
            }

        }
    }

    private void processAcceptFriendResponse(ResponseEvent event, SipNetListener<SipAcceptResponse> listener) {
        if (event.getResponse().getStatusCode() == 200) {
            handler.post(() -> listener.onSuccess(new SipAcceptResponse()));
        } else {
            byte[] dd = event.getResponse().getRawContent();
            String x = new String(dd);
            try {
                JSONObject jsonObject = new JSONObject(x);
                String reason = jsonObject.getString("reason");
                handler.post(() -> listener.onFailure(new SipFailure(reason)));
            } catch (JSONException e) {
                handler.post(() -> listener.onFailure(new SipFailure("异常故障，请稍后重试")));
                e.printStackTrace();
            }

        }
    }

    private void processSearchResponse(ResponseEvent event, SipNetListener<SipSearchResponse> listener) {
        byte[] dd = event.getResponse().getRawContent();
        String x = new String(dd);
        try {
            JSONObject jsonObject = new JSONObject(x);
            if (event.getResponse().getStatusCode() == 200) {
                SipSearchResponse res = new SipSearchResponse();
                try {
                    JSONArray users = jsonObject.getJSONArray("users");
                    List<User> applied = new ArrayList<>();
                    for (int i = 0; i < users.length(); i++) {
                        User s = User.createFromJson(users.getJSONObject(i));
                        applied.add(s);
                    }
                    res.users = applied;
                    handler.post(() -> listener.onSuccess(res));
                } catch (JSONException e) {
                    handler.post(() -> listener.onFailure(new SipFailure("异常故障，请稍后重试")));
                    e.printStackTrace();
                }
            } else {
                try {
                    String reason = jsonObject.getString("reason");
                    handler.post(() -> listener.onFailure(new SipFailure(reason)));
                } catch (JSONException e) {
                    handler.post(() -> listener.onFailure(new SipFailure("异常故障，请稍后重试")));
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            handler.post(() -> listener.onFailure(new SipFailure("fatal:json解析异常!")));
            e.printStackTrace();
        }
    }

    private void processInviteResponse(ResponseEvent event) {
        if (event.getResponse().getStatusCode() == 200) {
            Response response = event.getResponse();
            CSeqHeader cseq = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
            ClientTransaction tid = event.getClientTransaction();
            if (tid != null)
                dialog = tid.getDialog();
            else dialog = event.getDialog();
            dialog = event.getDialog();
            handler.post(() -> Toast.makeText(context, "建立与服务器会话", Toast.LENGTH_SHORT).show());
            if (dialog != null) {
                new Thread(() -> {
                    try {
                        Request r = tid.createAck();
                        event.getDialog().sendAck(r);
                    } catch (SipException e) {
                        handler.post(() -> Toast.makeText(context, "ACK异常", Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                    }
                }).start();

            }
        } else {
            byte[] dd = event.getResponse().getRawContent();
            String x = new String(dd);
            try {
                JSONObject jsonObject = new JSONObject(x);
                String reason = jsonObject.getString("reason");
                handler.post(() -> Toast.makeText(context, reason, Toast.LENGTH_SHORT).show());
            } catch (JSONException e) {
                handler.post(() -> Toast.makeText(context, "FATAL:无法解析服务器消息格式！", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }

        }
    }

    private SipListener sipListener = new SipListener() {


        @Override
        public void processRequest(RequestEvent requestEvent) {
            handler.post(() -> Toast.makeText(context, "收到request", Toast.LENGTH_SHORT).show());
            Request request = requestEvent.getRequest();
            byte[] dd = request.getRawContent();
            String x = new String(dd);
            try {
                switch (request.getMethod()) {
                    case Request.MESSAGE:
                        JSONObject object = new JSONObject(x);
                        send200Ok(requestEvent);
                        String service = object.getString("service");
                        if (service.equals(SERVICE_CHAT)) {//这是一条普通的message
                            SipMessage sipMessage = SipMessage.createFromJson(object);
                            DBManager.getInstance(context).save(sipMessage);
                            handler.post(() -> messageListener.onNewMessage(sipMessage));
                        } else {//这是一条系统消息
                            // TODO: 2017/7/13
                            SipSystemMessage message = SipSystemMessage.createFromJson(service, object);
                            DBManager.getInstance(context).saveEvent(message);
                            handler.post(() -> systemListener.onNewSystemEvent(message));
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //handler.post(() -> messageListener.onNewMessage(sipMessage));
        }

        //对于发送消息，回调走这里，应当发送回主线程
        @Override
        public void processResponse(ResponseEvent responseEvent) {
            Log.d(TAG, "收到response");
            int status = responseEvent.getResponse().getStatusCode();
            if(responseEvent.getClientTransaction().getRequest().getMethod().equals(Request.BYE)){
                handler.post(()-> Toast.makeText(context,"disconnected",Toast.LENGTH_SHORT).show());
                return;
            }
            if (status >= 200) {
                CSeqHeader header = (CSeqHeader) responseEvent.getResponse().getHeader("CSeq");
                long local = header.getSeqNumber();
                
                TaskListener listener = taskListeners.remove(local);
                if (listener != null) {
                    switch (listener.type) {
                        case MESSAGE:
                            processMessageResponse(responseEvent, listener.listener);
                            break;
                        case LOGIN:
                            processLoginResponse(responseEvent, listener.listener);
                            break;
                        case SUBFRIEND:
                            if (status == 200)
                                handler.post(() -> listener.listener.onSuccess(null));
                            else handler.post(() -> listener.listener.onFailure(null));
                        case ADDFRIEND:
                            processAddFriendResponse(responseEvent, listener.listener);
                        case DECLINEFRIEND:
                            processDeclineFriendResponse(responseEvent, listener.listener);
                        case ACCFRIEND:
                            processAcceptFriendResponse(responseEvent, listener.listener);
                        case SEARCH:
                            processSearchResponse(responseEvent, listener.listener);
                        case INVITE:
                            processInviteResponse(responseEvent);
                    }
                }
            } else {
                // handler.post(() -> Toast.makeText(context, "正在处理", Toast.LENGTH_SHORT).show());
            }
        }

        @Override
        public void processTimeout(TimeoutEvent timeoutEvent) {
            long local = timeoutEvent.getClientTransaction().getDialog().getLocalSeqNumber();
            TaskListener listener = taskListeners.remove(local);
            if (listener != null)
                handler.post(() -> listener.listener.onFailure(new SipFailure("消息超时！")));
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
        this.context = context;
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
                        Log.d(TAG, "executor ready to work");
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
            handler.post(() -> {
                Log.d(TAG, "HANDLER SUCCESS");
                Toast.makeText(context, "sip stack successfully init", Toast.LENGTH_SHORT).show();
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
//        properties.setProperty("android.javax.sip.OUTBOUND_PROXY", sipProfile.getRemoteEndpoint()
//                + "/" + sipProfile.getTransport());
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
            String localIp = IpUtil.getIPAddress(context);
            sipProfile.setLocalIp(localIp);
            if (localIp != null) {
                udpListeningPoint = sipStack.createListeningPoint(sipProfile.getLocalIp(), sipProfile.getLocalPort(), sipProfile.getTransport());
                sipProvider = sipStack.createSipProvider(udpListeningPoint);
                sipProvider.addSipListener(sipListener);
                stackState = StackState.READY;
                requestBuilder = new SipRequestBuilder(addressFactory, sipProvider, messageFactory,
                        headerFactory, sipProfile);
            } else {
                Toast.makeText(context, "没有网", Toast.LENGTH_SHORT).show();
                stackState = StackState.ERROR;
            }
        } catch (PeerUnavailableException | ObjectInUseException | TooManyListenersException |
                TransportNotSupportedException |
                InvalidArgumentException e) {
            stackState = StackState.ERROR;
        }


    }

    private void dealWithTask(SipTask task) {
        task.r.run();
    }

    private void sendSubScribeForFriendState() {
        SipNetListener listener = new SipNetListener() {
            @Override
            public void onSuccess(Object response) {
                handler.post(() -> Toast.makeText(context, "订阅好友动态成功", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(SipFailure failure) {
                handler.post(() -> Toast.makeText(context, "订阅好友动态失败", Toast.LENGTH_SHORT).show());
            }
        };
        long current = seq.getAndIncrement();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", 0);
            Request request = requestBuilder.buildSubscribe(jsonObject, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.SUBFRIEND));
            dealRequest(request, SipTaskType.SUBFRIEND, listener);
        } catch (ParseException | InvalidArgumentException | JSONException e) {
            handler.post(() -> listener.onFailure(new SipFailure("sip消息格式有误")));
            e.printStackTrace();
        }
    }

    private void establishDialog() {
        long current = seq.getAndIncrement();
        try {
            JSONObject jsonObject = new JSONObject();
            Request request = requestBuilder.buildInvite(jsonObject, current);
            taskListeners.put(current, new TaskListener(null, SipTaskType.INVITE));
            ClientTransaction trans = sipProvider.getNewClientTransaction(request);
            //dialog = trans.getDialog();
            try {
                taskQueue.put(new SipTask(() -> {
                    try {
                        trans.sendRequest();
                    } catch (SipException e) {
                        handler.post(() -> Toast.makeText(context, "FATAL:SIP ERROR", Toast.LENGTH_SHORT).show());
                        e.printStackTrace();
                    }
                }, SipTaskType.INVITE));
            } catch (InterruptedException e) {
                handler.post(() -> Toast.makeText(context, "FATAL:SIP ERROR", Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        } catch (ParseException | InvalidArgumentException e) {
            handler.post(() -> Toast.makeText(context, "解析失败", Toast.LENGTH_SHORT).show());
            e.printStackTrace();
        } catch (TransactionUnavailableException e) {
            handler.post(() -> Toast.makeText(context, "FATAL:TRANSCTION UNAVAILABLE", Toast.LENGTH_SHORT).show());
            e.printStackTrace();
        }
    }

    public void bye() {
        if (dialog != null) {
            try {
                Request request = dialog.createRequest(Request.BYE);
                dealRequest(request, SipTaskType.BYE, new SipNetListener() {
                    @Override
                    public void onSuccess(Object response) {
                       handler.post(()-> Toast.makeText(context,"disconnected",Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onFailure(SipFailure failure) {
                        handler.post(()-> Toast.makeText(context,"can't disconenct",Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (SipException e) {
                e.printStackTrace();
            }
        }
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


    public void sendMessageL(String to, String msg) {
        new Thread(() -> {
            try {

                SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
                Address fromNameAddress = addressFactory.createAddress(from);
                fromNameAddress.setDisplayName(sipProfile.getSipUserName());
                FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress, "androidsip");
                String username = to.substring(to.indexOf(':') + 1, to.indexOf('@'));
                String address = to.substring(to.indexOf('@') + 1);

                SipURI toAddress = addressFactory.createSipURI(username, address);
                Address toNameAddress = addressFactory.createAddress(toAddress);
                toNameAddress.setDisplayName(username);

                ToHeader toHeader = headerFactory.createToHeader(toNameAddress, "he");

                SipURI requestURI = addressFactory.createSipURI(username, address);
                requestURI.setTransportParam("udp");

                ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
                ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalIp(),
                        sipProfile.getLocalPort(), "udp", "branch1");
                viaHeaders.add(viaHeader);

                CallIdHeader callIdHeader = sipProvider.getNewCallId();

                CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1,
                        Request.MESSAGE);

                MaxForwardsHeader maxForwards = headerFactory
                        .createMaxForwardsHeader(70);

                ContentTypeHeader contentTypeHeader = headerFactory
                        .createContentTypeHeader("text", "plain");

                Request request = messageFactory.createRequest(requestURI,
                        Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                        toHeader, viaHeaders, maxForwards, contentTypeHeader, msg.getBytes());

                SipURI contactURI = addressFactory.createSipURI(sipProfile.getSipUserName(),
                        sipProfile.getLocalIp());
                contactURI.setPort(sipProfile.getLocalPort());
                Address contactAddress = addressFactory.createAddress(contactURI);
                contactAddress.setDisplayName(sipProfile.getSipUserName());
                ContactHeader contactHeader = headerFactory.createContactHeader(contactAddress);
                request.addHeader(contactHeader);


                System.out.println(request);
                try {
                    ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
                    transaction.sendRequest();
                    //this.sipProvider.sendRequest(request);
                } catch (SipException e) {
                    e.printStackTrace();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            }


        }).start();
    }

    /**
     * 以下为对ISipService的实现，主动事件
     */
    @Override
    public void register(String name, String password, SipNetListener listener) {

    }

    @Override
    public void login(int id, String password, SipNetListener listener) {
        long current = seq.getAndIncrement();
        Request request = null;
        try {
            request = requestBuilder.buildLogin(id, password, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.LOGIN));
            dealRequest(request, SipTaskType.LOGIN, listener);
        } catch (ParseException | InvalidArgumentException e) {
            listener.onFailure(new SipFailure("无法解析sip message格式"));
            e.printStackTrace();
        }
    }

    @Override
    public void addFriend(int id, SipNetListener listener) {
        long current = seq.getAndIncrement();
        Request request = null;
        try {
            request = requestBuilder.buildAddFriend(id, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.ADDFRIEND));
            dealRequest(request, SipTaskType.ADDFRIEND, listener);
        } catch (ParseException | InvalidArgumentException | JSONException e) {
            listener.onFailure(new SipFailure("无法解析sip message格式"));
            e.printStackTrace();
        }
    }

    @Override
    public void declineFriendInvite(int id, SipNetListener listener) {
        long current = seq.getAndIncrement();
        Request request = null;
        try {
            request = requestBuilder.buildAddFriend(id, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.DECLINEFRIEND));
            dealRequest(request, SipTaskType.DECLINEFRIEND, listener);
        } catch (ParseException | InvalidArgumentException | JSONException e) {
            listener.onFailure(new SipFailure("无法解析sip message格式"));
            e.printStackTrace();
        }
    }

    @Override
    public void acceptFriendInvite(int id, SipNetListener listener) {
        long current = seq.getAndIncrement();
        Request request = null;
        try {
            request = requestBuilder.buildAccFriend(id, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.ACCFRIEND));
            dealRequest(request, SipTaskType.ACCFRIEND, listener);
        } catch (ParseException | InvalidArgumentException | JSONException e) {
            listener.onFailure(new SipFailure("无法解析sip message格式"));
            e.printStackTrace();
        }

    }

    //将普通message放入队列
    @Override
    public void sendMessage(SipMessage message, SipNetListener<SipSendMsgResponse> listener) {
        long current = seq.getAndIncrement();
        DBManager.getInstance(context).save(message);
        try {
            Request request = requestBuilder.buildMessage(message, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.MESSAGE));
            dealRequest(request, SipTaskType.MESSAGE, listener);
        } catch (ParseException | InvalidArgumentException e) {
            handler.post(() -> listener.onFailure(new SipFailure("sip消息格式有误")));
            e.printStackTrace();
        }
    }


    @Override
    public void getUserInfo(int id, SipNetListener listener) {

    }

    @Override
    public void modifyUserInfo(User info) {

    }

    @Override
    public void searchUsers(String key, SipNetListener<SipSearchResponse> listener) {
        long current = seq.getAndIncrement();
        SipMessage message = new SipMessage();
        try {
            Request request = requestBuilder.buildSearch(key, current);
            taskListeners.put(current, new TaskListener(listener, SipTaskType.SEARCH));
            dealRequest(request, SipTaskType.SEARCH, listener);
        } catch (ParseException | InvalidArgumentException e) {
            handler.post(() -> listener.onFailure(new SipFailure("sip消息格式有误")));
            e.printStackTrace();
        }
    }

    private void dealRequest(Request request, SipTaskType taskType, SipNetListener listener) {
        try {
            taskQueue.put(new SipTask(() -> {
                try {

                    ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
                    transaction.sendRequest();

                } catch (SipException e) {
                    handler.post(() -> listener.onFailure(new SipFailure("Sip访问出现异常")));
                    e.printStackTrace();
                }
            }, taskType));
        } catch (InterruptedException e) {
            listener.onFailure(new SipFailure("无法放置入消息队列"));
            e.printStackTrace();
        }

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
        MESSAGE, LOGIN, SUBFRIEND, ADDFRIEND, DECLINEFRIEND, ACCFRIEND, INVITE, BYE,
        SEARCH
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
