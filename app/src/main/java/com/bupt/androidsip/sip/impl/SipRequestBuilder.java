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

import com.bupt.androidsip.entity.sip.SipMessage;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by xusong on 2017/7/8.
 * About:
 */

public class SipRequestBuilder {

    final String SERVER_ADD="";
    AddressFactory addressFactory;
    SipProvider sipProvider;
    MessageFactory messageFactory;
    HeaderFactory headerFactory;
    SipProfile sipProfile;
    public SipRequestBuilder(AddressFactory addressFactory, SipProvider sipProvider, MessageFactory messageFactory,
                             HeaderFactory headerFactory,SipProfile sipProfile) {
        this.addressFactory = addressFactory;
        this.sipProvider = sipProvider;
        this.messageFactory = messageFactory;
        this.headerFactory = headerFactory;
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

    public Request buildMessage(SipMessage message,long seq) throws ParseException, InvalidArgumentException {
        SipURI from = addressFactory.createSipURI(sipProfile.getSipUserName(), sipProfile.getLocalEndpoint());
        Address fromNameAddress = addressFactory.createAddress(from);
        // fromNameAddress.setDisplayName(sipUsername);
        FromHeader fromHeader = headerFactory.createFromHeader(fromNameAddress,
                "Tzt0ZEP92");

        URI toAddress = addressFactory.createURI(SERVER_ADD);
        Address toNameAddress = addressFactory.createAddress(toAddress);
        // toNameAddress.setDisplayName(username);
        ToHeader toHeader =headerFactory.createToHeader(toNameAddress, null);

        URI requestURI = addressFactory.createURI(SERVER_ADD);
        // requestURI.setTransportParam("udp");

        ArrayList<ViaHeader> viaHeaders = createViaHeader();

        CallIdHeader callIdHeader = sipProvider.getNewCallId();

        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(seq,
                Request.MESSAGE);

        MaxForwardsHeader maxForwards = headerFactory
                .createMaxForwardsHeader(70);

        Request request = messageFactory.createRequest(requestURI,
                Request.MESSAGE, callIdHeader, cSeqHeader, fromHeader,
                toHeader, viaHeaders, maxForwards);
        SupportedHeader supportedHeader =headerFactory
                .createSupportedHeader("replaces, outbound");
        request.addHeader(supportedHeader);

        SipURI routeUri = addressFactory.createSipURI(null, sipProfile.getRemoteIp());
        routeUri.setTransportParam(sipProfile.getTransport());
        routeUri.setLrParam();
        routeUri.setPort(sipProfile.getRemotePort());

        Address routeAddress = addressFactory.createAddress(routeUri);
        RouteHeader route = headerFactory.createRouteHeader(routeAddress);
        request.addHeader(route);
        ContentTypeHeader contentTypeHeader = headerFactory
                .createContentTypeHeader("text", "plain");
        request.setContent(MessageToString(message), contentTypeHeader);
        System.out.println(request);
        return request;
        //ClientTransaction transaction = sipManager.sipProvider
        //		.getNewClientTransaction(request);
        // Send the request statefully, through the client transaction.
        //transaction.sendRequest();
    }

//    public Request buildInvite(SipManager sipManager, String to, int port) {
//
//        try {
//            SipURI from = sipManager.addressFactory.createSipURI(sipManager.getSipProfile().getSipUserName(), sipManager.getSipProfile().getLocalEndpoint());
//            Address fromNameAddress = sipManager.addressFactory.createAddress(from);
//            //fromNameAddress.setDisplayName(sipUsername);
//            FromHeader fromHeader = sipManager.headerFactory.createFromHeader(fromNameAddress,
//                    "Tzt0ZEP92");
//            URI toAddress = sipManager.addressFactory.createURI(to);
//            Address toNameAddress = sipManager.addressFactory.createAddress(toAddress);
//            // toNameAddress.setDisplayName(username);
//            ToHeader toHeader = sipManager.headerFactory.createToHeader(toNameAddress, null);
//
//            URI requestURI = sipManager.addressFactory.createURI(to);
//            // requestURI.setTransportParam("udp");
//
//            ArrayList<ViaHeader> viaHeaders = sipManager.createViaHeader();
//
//            CallIdHeader callIdHeader = sipManager.sipProvider.getNewCallId();
//
//            CSeqHeader cSeqHeader = sipManager.headerFactory.createCSeqHeader(1l,
//                    Request.INVITE);
//
//            MaxForwardsHeader maxForwards = sipManager.headerFactory
//                    .createMaxForwardsHeader(70);
//
//            Request callRequest = sipManager.messageFactory.createRequest(requestURI,
//                    Request.INVITE, callIdHeader, cSeqHeader, fromHeader,
//                    toHeader, viaHeaders, maxForwards);
//            SupportedHeader supportedHeader = sipManager.headerFactory
//                    .createSupportedHeader("replaces, outbound");
//            callRequest.addHeader(supportedHeader);
//            addCustomHeaders(callRequest, sipManager);
//
//            SipURI routeUri = sipManager.addressFactory.createSipURI(null, sipManager.getSipProfile().getRemoteIp());
//            routeUri.setTransportParam(sipManager.getSipProfile().getTransport());
//            routeUri.setLrParam();
//            routeUri.setPort(sipManager.getSipProfile().getRemotePort());
//
//            Address routeAddress = sipManager.addressFactory.createAddress(routeUri);
//            RouteHeader route = sipManager.headerFactory.createRouteHeader(routeAddress);
//            callRequest.addHeader(route);
//            // Create ContentTypeHeader
//            ContentTypeHeader contentTypeHeader = sipManager.headerFactory
//                    .createContentTypeHeader("application", "sdp");
//
//            // Create the contact name address.
//            SipURI contactURI = sipManager.addressFactory.createSipURI(sipManager.getSipProfile().getSipUserName(), sipManager.getSipProfile().getLocalIp());
//            contactURI.setPort(sipManager.sipProvider.getListeningPoint(sipManager.getSipProfile().getTransport())
//                    .getPort());
//
//            Address contactAddress = sipManager.addressFactory.createAddress(contactURI);
//
//            // Add the contact address.
//            //contactAddress.setDisplayName(fromName);
//
//            ContactHeader contactHeader = sipManager.headerFactory.createContactHeader(contactAddress);
//            callRequest.addHeader(contactHeader);
//
//            // You can add extension headers of your own making
//            // to the outgoing SIP request.
//            // Add the extension header.
//            Header extensionHeader = sipManager.headerFactory.createHeader("My-Header",
//                    "my header value");
//            callRequest.addHeader(extensionHeader);
//
//            String sdpData = "v=0\r\n"
//                    + "o=4855 13760799956958020 13760799956958020"
//                    + " IN IP4 " + sipManager.getSipProfile().getLocalIp() + "\r\n" + "s=mysession session\r\n"
//                    + "p=+46 8 52018010\r\n" + "c=IN IP4 " + sipManager.getSipProfile().getLocalIp() + "\r\n"
//                    + "t=0 0\r\n" + "m=audio " + port + " RTP/AVP 0 4 18\r\n"
//                    + "a=rtpmap:0 PCMU/8000\r\n" + "a=rtpmap:4 G723/8000\r\n"
//                    + "a=rtpmap:18 G729A/8000\r\n" + "a=ptime:20\r\n";
//            byte[] contents = sdpData.getBytes();
//
//            callRequest.setContent(contents, contentTypeHeader);
//            // You can add as many extension headers as you
//            // want.
//
//            extensionHeader = sipManager.headerFactory.createHeader("My-Other-Header",
//                    "my new header value ");
//            callRequest.addHeader(extensionHeader);
//
//            Header callInfoHeader = sipManager.headerFactory.createHeader("Call-Info",
//                    "<http://www.antd.nist.gov>");
//            callRequest.addHeader(callInfoHeader);
//            return callRequest;
//            // Create the client transaction.
//            //inviteTid = sipManager.sipProvider.getNewClientTransaction(callRequest);
//
//            //System.out.println("inviteTid = " + inviteTid);
//
//            // send the request out.
//
//            //	inviteTid.sendRequest();
//
//            //dialog = inviteTid.getDialog();
//
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//            ex.printStackTrace();
//
//        }
//        return null;
//    }
//
//    private void addCustomHeaders(Request callRequest, SipManager sipManager) {
//        // Get a set of the entries
//        Set set = sipManager.getCustomHeaders().entrySet();
//        // Get an iterator
//        Iterator i = set.iterator();
//        // Display elements
//        while (i.hasNext()) {
//            Map.Entry me = (Map.Entry) i.next();
//            try {
//                Header customHeader = sipManager.headerFactory.createHeader(me.getKey().toString(), me.getValue().toString());
//                callRequest.addHeader(customHeader);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//    }



    public ArrayList<ViaHeader> createViaHeader() {
        ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader myViaHeader;
        try {
            myViaHeader = this.headerFactory.createViaHeader(
                    sipProfile.getLocalIp(), sipProfile.getLocalPort(),
                    sipProfile.getTransport(), null);
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
    private String MessageToString(SipMessage sipMessage){
        return "";
    }

}
