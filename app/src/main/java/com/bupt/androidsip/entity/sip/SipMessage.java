package com.bupt.androidsip.entity.sip;

import com.bupt.androidsip.entity.User;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by xusong on 2017/7/6.
 * About:
 */

public class SipMessage {
    public int type;//0 normal   1 picture   2 file
    public long createTime;//消息创建时间
    public long comeTime;//消息到达时间
    public String content;//内容，如果是图片为url，如果是文本就是文本内容
    public int belong;//所属的chat,没有群聊就不管这个域
    public int from;//发送方
    public List<Integer> to;//目标成员，如果是单聊就是那个人id，群聊时，belong和to应当都有值

    public static SipMessage createFromJson(JSONObject object){
        return null;
    }

}
