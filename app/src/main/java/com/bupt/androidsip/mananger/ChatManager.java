package com.bupt.androidsip.mananger;

import android.content.Context;

import com.bupt.androidsip.entity.Chat;

/**
 * Created by vita-nove on 07/07/2017.
 */

public class ChatManager {
    private static ChatManager instance;

    public Chat getChat() {
        return chat;
    }

    private Chat chat;

    public void setChat(Chat chat) {
        this.chat = chat;
    }




    private ChatManager(Context context) {

    }

    public static ChatManager GetInstance(Context context) {
        if (instance == null) {
            synchronized (ChatManager.class) {
                if (instance == null) {
                    instance = new ChatManager(context);
                }
            }
        }
        return instance;
    }
}
