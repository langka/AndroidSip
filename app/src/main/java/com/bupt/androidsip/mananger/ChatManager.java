package com.bupt.androidsip.mananger;

import android.content.Context;

import com.bupt.androidsip.entity.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.String.valueOf;

/**
 * Created by vita-nove on 07/07/2017.
 */

public class ChatManager {
    private static ChatManager instance = new ChatManager();
    private static int key = 0;

    private ChatManager() {
        chatList = new ArrayList<>();
    }

    public static ChatManager GetInstance() {
        return instance;
    }

    private ArrayList<Chat> chatList;
}
