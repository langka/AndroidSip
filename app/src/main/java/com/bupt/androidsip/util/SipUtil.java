package com.bupt.androidsip.util;

import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.mananger.UserManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by xusong on 2017/7/8.
 * About:
 */

public class SipUtil {

    public static JSONObject getDefaultParams() {//获取default的传送json串
        JSONObject object = new JSONObject();
        User user = UserManager.getInstance().getUser();
        try {
            if (user != null) {
                object.put("mid", user.id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * @param chat    所属的chat
     * @param message message内容
     * @return 用于发送的sipMessage
     */
    public static SipMessage createStringMessage(Chat chat, String message) {
        SipMessage sipMessage = new SipMessage();
        sipMessage.type = 0;
        // sipMessage.belong = chat.sipChat.id;
        sipMessage.to = new ArrayList<>();
        //因为现在只有私聊 所以直接add sipChat对象暂时不用
        sipMessage.to.add(chat.ID);
        sipMessage.content = message;
        sipMessage.from = UserManager.getInstance().getUser().id;
        sipMessage.createTime = System.currentTimeMillis();
        return sipMessage;
    }


}
