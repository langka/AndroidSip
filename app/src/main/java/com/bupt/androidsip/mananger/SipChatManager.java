package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.sip.SipChat;

import java.util.List;

/**
 * Created by vita-nove on 08/07/2017.
 */

public class SipChatManager {
    private List<SipChat> groups;

    public List<SipChat> getSipChat() {
        return groups;
    }

    private static SipChatManager instance = new SipChatManager();

    private SipChatManager() {
        groups = null;
    }

    public void setSipChat(List<SipChat> groups) {
        this.groups = groups;
    }

    public static SipChatManager getInstance() {
        return instance;
    }
}

