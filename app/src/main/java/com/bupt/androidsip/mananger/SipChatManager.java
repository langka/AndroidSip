package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.sip.SipChat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vita-nove on 08/07/2017.
 */

public class SipChatManager {
    private List<SipChat> groups = new ArrayList<>();

    public List<SipChat> getSipChat() {
        return groups;
    }

    private static SipChatManager instance = new SipChatManager();

    private SipChatManager() {
    }

    public void setSipChat(List<SipChat> groups) {
        if (groups == null || groups.size() == 0)
            return;
        this.groups = groups;
    }

    public static SipChatManager getInstance() {
        return instance;
    }
}

