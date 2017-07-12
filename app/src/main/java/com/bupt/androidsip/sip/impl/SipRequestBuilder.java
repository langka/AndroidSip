package com.bupt.androidsip.sip.impl;

import android.javax.sip.InvalidArgumentException;
import android.javax.sip.SipProvider;
import android.javax.sip.address.Address;
import android.javax.sip.address.AddressFactory;
import android.javax.sip.address.SipURI;
import android.javax.sip.address.URI;
import android.javax.sip.header.CSeqHeader;
import android.javax.sip.header.CallIdHeader;
import android.javax.sip.header.ContentTypeHeader;
import android.javax.sip.header.ExpiresHeader;
import android.javax.sip.header.FromHeader;
import android.javax.sip.header.HeaderFactory;
import android.javax.sip.header.MaxForwardsHeader;
import android.javax.sip.header.RouteHeader;
import android.javax.sip.header.SupportedHeader;
import android.javax.sip.header.ToHeader;
import android.javax.sip.header.ViaHeader;
import android.javax.sip.message.MessageFactory;
import android.javax.sip.message.Request;
import android.util.Log;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.mananger.UserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by xusong on 2017/7/8.
 * About:
 */

public class SipRequestBuilder {



    // TODO: 2017/7/11 服务器端的地址
    final String SipServerEndPoint = "192.168.137.1:5060";

    final String TAG = "siprequestbuilder";

    final String SERVER_ADD = "";
    AddressFactory addressFactory;
    SipProvider sipProvider;
    MessageFactory messageFactory;
    HeaderFactory headerFactory;
    SipProfile sipProfile;

    public SipRequestBuilder(AddressFactory addressFactory, SipProvider sipProvider, MessageFactory messageFactory,
                             HeaderFactory headerFactory, SipProfile sipProfile) {
        this.addressFactory = addressFactory;
        this.sipProvider = sipProvider;
        this.messageFactory = messageFactory;
        this.headerFactory = headerFactory;
        this.sipProfile = sipProfile;
    }

    public Request buildRegister(SipManager sipManager) throws ParseException, InvalidArgumentException {
        // Create addresses and via header for the request
        Address fromAddress = addressFactory.createAddress("sip:"
                + sipProfile.getSipUserName() + "@"
                + sipProfile.getRemoteIp());
        fromAddress.setDisplayName(sipProfile.getSipUserName());
        Address toAddress = addressFactory.createAddress("sip:"
                + sipProfile.getSipUserName() + "@"
                + sipProfile.getRemoteIp());
        toAddress.setDisplayName(sipProfile.getSipUserName());
        Address contactAddress = createContactAddress();
        ArrayList<ViaHeader> viaHeaders = createViaHeader();
        URI requestURI = addressFactory.createAddress(
                "sip:" + sipProfile.getRemoteEndpoint()).getURI();
        final Request request = messageFactory.createRequest(requestURI,
                Request.REGISTER, sipProvider.getNewCallId(),
                headerFactory.createCSeqHeader(1l, Request.REGISTER),
                headerFactory.createFromHeader(fromAddress, "c3ff411e"),
                headerFactory.createToHeader(toAddress, null), viaHeaders,
                headerFactory.createMaxForwardsHeader(70));
        request.addHeader(headerFactory.createContactHeader(contactAddress));
        ExpiresHeader eh = headerFactory.createExpiresHeader(300);
        request.addHeader(eh);
        System.out.println(request.toString());
        return request;

    }

    //构造单聊的信息
    public Request buildMessage(SipMessage message, long seq) throws ParseException, InvalidArgumentException {
        SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
        Address fromNameAddress = addressFactory.createAddress(from);
        fromNameAddress.setDisplayName(UserManager.getInstance().getUser().id+"");
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
                "Tzt0ZEP92");

        //User toUser = UserManager.getInstance().searchUser(message.to.get(0));
        String to = "sip:" + message.to.get(0)+ "@" + SipServerEndPoint;
        String username = message.to.get(0)+"";
        String address = SipServerEndPoint;

        URI toAddress = addressFactory.createURI(to);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);

        ToHeader toHeader = headerFactory.createToHeader(toNameAddress, "hh");

        SipURI requestURI = addressFactory.createSipURI(username, address);
        requestURI.setTransportParam("udp");

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalIp(), sipProfile.getLocalPort()
                , "udp", "xs");
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq,
                Request.MESSAGE);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        Request request = messageFactory.createRequest(requestURI,
                Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        SupportedHeader supportedHeader = headerFactory
                .createSupportedHeader("replaces, outbound");
        request.addHeader(supportedHeader);

        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("text", "plain");
        request.setContent(MessageToString(message), contentTypeHeader);
        System.out.println(request);
        return request;
    }


    //这个就是注册
    public Request buildLogin(int id, String pwd, long seq) throws ParseException, InvalidArgumentException {
        SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
        Address fromNameAddress = addressFactory.createAddress(from);
        fromNameAddress.setDisplayName(id+"");
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
                "androidSip");
        String to = "sip:" + "Server" + "@" + SipServerEndPoint;
        String username = "Server";
        String address = to.substring(to.indexOf('@') + 1);

        URI toAddress = addressFactory.createSipURI(username, address);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);


        ToHeader toHeader = headerFactory.createToHeader(toNameAddress, "ha");

        SipURI requestURI = addressFactory.createSipURI(username, address);
        requestURI.setTransportParam("udp");

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalIp(),
                sipProfile.getLocalPort(), "udp", "branch1");
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq,
                Request.REGISTER);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        Request request = messageFactory.createRequest(requestURI,
                Request.REGISTER, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        SupportedHeader supportedHeader = headerFactory
                .createSupportedHeader("replaces, outbound");
        request.addHeader(supportedHeader);

        ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("text", "plain");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("password", pwd);
        } catch (JSONException e) {
            Log.d(TAG, "json pwd name异常");
            e.printStackTrace();
        }
        request.setContent(jsonObject.toString(), contentTypeHeader);
        System.out.println(request);
        return request;
    }

    //监听好友动态
    public Request buildSubscribe(JSONObject type,long seq) throws ParseException, InvalidArgumentException {
        SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
        Address fromNameAddress = addressFactory.createAddress(from);
        fromNameAddress.setDisplayName(sipProfile.getSipUserName());
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
                "Tzt0ZEP92");
        String to = "sip:" + "Server" + "@" + SipServerEndPoint;
        String username = "Server";
        String address = SipServerEndPoint;

        URI toAddress = addressFactory.createURI(to);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);

        ToHeader toHeader = headerFactory.createToHeader(toNameAddress, "hh");

        SipURI requestURI = addressFactory.createSipURI(username, address);
        requestURI.setTransportParam("udp");

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalIp(), sipProfile.getLocalPort()
                , "udp", "xs");
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq,
                Request.SUBSCRIBE);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        Request request = messageFactory.createRequest(requestURI,
                Request.SUBSCRIBE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        SupportedHeader supportedHeader = headerFactory
                .createSupportedHeader("replaces, outbound");
        request.addHeader(supportedHeader);

        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("text", "plain");
        request.setContent(type.toString(), contentTypeHeader);
        System.out.println(request);
        return request;
    }

    //添加好友
    public Request buildAddFriend(int target,long seq) throws ParseException, InvalidArgumentException, JSONException {
        SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
        Address fromNameAddress = addressFactory.createAddress(from);
        fromNameAddress.setDisplayName(sipProfile.getSipUserName());
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
                "Tzt0ZEP92");
        String to = "sip:" + "Server" + "@" + SipServerEndPoint;
        String username = "Server";
        String address = SipServerEndPoint;

        URI toAddress = addressFactory.createURI(to);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);

        ToHeader toHeader = headerFactory.createToHeader(toNameAddress, "hh");

        SipURI requestURI = addressFactory.createSipURI(username, address);
        requestURI.setTransportParam("udp");

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalIp(), sipProfile.getLocalPort()
                , "udp", "xs");
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq,
                Request.MESSAGE);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        Request request = messageFactory.createRequest(requestURI,
                Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        SupportedHeader supportedHeader = headerFactory
                .createSupportedHeader("replaces, outbound");
        request.addHeader(supportedHeader);

        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("text", "plain");
        JSONObject object = new JSONObject();
        object.put("service","add_friend");
        object.put("to",target);
        request.setContent(object, contentTypeHeader);
        System.out.println(request);
        return request;
    }


    //拒绝好友
    public Request buildDeclineFriend(int target,long seq) throws ParseException, InvalidArgumentException, JSONException {
        SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
        Address fromNameAddress = addressFactory.createAddress(from);
        fromNameAddress.setDisplayName(sipProfile.getSipUserName());
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
                "Tzt0ZEP92");
        String to = "sip:" + "Server" + "@" + SipServerEndPoint;
        String username = "Server";
        String address = SipServerEndPoint;

        URI toAddress = addressFactory.createURI(to);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);

        ToHeader toHeader = headerFactory.createToHeader(toNameAddress, "hh");

        SipURI requestURI = addressFactory.createSipURI(username, address);
        requestURI.setTransportParam("udp");

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalIp(), sipProfile.getLocalPort()
                , "udp", "xs");
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq,
                Request.MESSAGE);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        Request request = messageFactory.createRequest(requestURI,
                Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        SupportedHeader supportedHeader = headerFactory
                .createSupportedHeader("replaces, outbound");
        request.addHeader(supportedHeader);

        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("text", "plain");
        JSONObject object = new JSONObject();
        object.put("service","decline_friend");
        object.put("to",target);
        request.setContent(object, contentTypeHeader);
        System.out.println(request);
        return request;
    }

    public Request buildAccFriend(int target,long seq) throws ParseException, InvalidArgumentException, JSONException {
        SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
        Address fromNameAddress = addressFactory.createAddress(from);
        fromNameAddress.setDisplayName(sipProfile.getSipUserName());
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
                "Tzt0ZEP92");
        String to = "sip:" + "Server" + "@" + SipServerEndPoint;
        String username = "Server";
        String address = SipServerEndPoint;

        URI toAddress = addressFactory.createURI(to);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        toNameAddress.setDisplayName(username);

        ToHeader toHeader = headerFactory.createToHeader(toNameAddress, "hh");

        SipURI requestURI = addressFactory.createSipURI(username, address);
        requestURI.setTransportParam("udp");

        ArrayList<ViaHeader> viaHeaders = new ArrayList<>();
        ViaHeader viaHeader = headerFactory.createViaHeader(sipProfile.getLocalIp(), sipProfile.getLocalPort()
                , "udp", "xs");
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq,
                Request.MESSAGE);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        Request request = messageFactory.createRequest(requestURI,
                Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        SupportedHeader supportedHeader = headerFactory
                .createSupportedHeader("replaces, outbound");
        request.addHeader(supportedHeader);

        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("text", "plain");
        JSONObject object = new JSONObject();
        object.put("service","acc_friend");
        object.put("to",target);
        request.setContent(object, contentTypeHeader);
        System.out.println(request);
        return request;
    }

    public ArrayList<ViaHeader> createViaHeader() {
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader myViaHeader;
        try {
            myViaHeader = this.headerFactory.createViaHeader(
                    sipProfile.getLocalIp(), sipProfile.getLocalPort(),
                    sipProfile.getTransport(), "XS");
            myViaHeader.setRPort();
            viaHeaders.add(myViaHeader);
        } catch (ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return viaHeaders;
    }




    public Address createContactAddress() {
        try {
            return this.addressFactory.createAddress("sip:"
                    + sipProfile.getSipUserName() + "@"
                    + sipProfile.getLocalEndpoint() + ";transport=udp"
                    + ";registering_acc=" + sipProfile.getRemoteIp());
        } catch (ParseException e) {
            return null;
        }
    }

    // TODO: 2017/7/8
    private String MessageToString(SipMessage sipMessage) {
        JSONObject object = new JSONObject();
        try {
            object.put("service","private_chat");
            object.put("from", sipMessage.from);
            JSONArray array = new JSONArray();
            for (int i = 0; i < sipMessage.to.size(); i++) {
                array.put(sipMessage.to.get(i));
            }
            object.put("to", array.get(0));
            object.put("create_time", sipMessage.createTime);
            object.put("belong", sipMessage.belong);
            object.put("type", sipMessage.type);
            object.put("content", sipMessage.content);
        } catch (JSONException e) {
            Log.d(TAG, "error message trans to json ");
            e.printStackTrace();
        }
        return object.toString();
    }

}
