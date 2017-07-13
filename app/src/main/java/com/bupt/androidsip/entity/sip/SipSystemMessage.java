package com.bupt.androidsip.entity.sip;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.sip.SipSystemListener;
import com.bupt.androidsip.sip.impl.SipManager;

import org.json.JSONObject;

/**
 * Created by xusong on 2017/7/12.
 * About:
 */

public class SipSystemMessage {
    public int type;//0：好友邀请， 1：好友请求被接受  2：好友请求被拒绝
    public User assoicatedUser;//name id
    public int state  = 1;//0已经处理，1未处理
    public int id;//不要动这个地方！！
    public static SipSystemMessage createFromJson(String service, JSONObject object){
        SipSystemMessage message = new SipSystemMessage();
        if(service.equals(SipManager.SERVICE_ACC))
            message.type = 1;
        else if(service.equals(SipManager.SERVICE_ADD))
            message.type = 0;
        else if(service.equals(SipManager.SERVICE_DECLINE))
            message.type = 2;
        message.assoicatedUser  = User.createFromJson(object);
        message.state = 1;
        return message;

    }

    public void jiashuju(int type, String name,int id, int state, int id2){
        this.type = type;
        this.assoicatedUser = new User();
        this.assoicatedUser.id = id;
        this.assoicatedUser.name = name;
        this.state = state;
        this.id = id2;
    }
}
