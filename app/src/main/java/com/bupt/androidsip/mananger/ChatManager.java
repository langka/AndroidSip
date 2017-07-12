package com.bupt.androidsip.mananger;

import android.content.Context;

import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.entity.Message;
import com.bupt.androidsip.util.SortListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.String.valueOf;

/**
 * Created by vita-nove on 07/07/2017.
 */

public class ChatManager {
    private ArrayList<Chat> chatList = new ArrayList<>();

    public ArrayList<Chat> getChatList() {
        return chatList;
    }

    private static ChatManager instance = new ChatManager();

    private ChatManager() {
    }

    public Chat getChat(int index) {
        return chatList.get(index);
    }

    public void addChat(Chat chat) {
        chatList.add(chat);
    }

    public void setChatList(ArrayList<Chat> chatList) {
        if (chatList == null || chatList.size() == 0)
            return;
        this.chatList = chatList;
    }

    public static ChatManager getChatManager() {
        return instance;
    }

    public void addMsg(int index, Message msg) {
        chatList.get(index).messages.add(msg);
    }

    public void addMsgWithUnread(int index, Message msg) {
        chatList.get(index).messages.add(msg);
        chatList.get(index).unread++;
    }

    public void setLastMsgWithUnread(int index, String content) {
        chatList.get(index).lastMessage = content;
        chatList.get(index).unread++;
    }

    public void removeUnread(int index) {
        chatList.get(index).unread = 0;
    }

    public int getTotalUnread() {
        int unread = 0;
        for (int i = 0; i < chatList.size(); ++i)
            unread += chatList.get(i).unread;
        return unread;
    }

    public boolean isInList(int ID) {
        for (int i = 0; i < chatList.size(); ++i) {
            if (chatList.get(i).ID == ID)
                return true;
        }
        return false;
    }

    public Chat getChatFromID(int ID) {
        for (int i = 0; i < chatList.size(); ++i) {
            if (chatList.get(i).ID == ID)
                return chatList.get(i);
        }
        return null;
    }

    public void removeAllChat() {
        chatList.clear();
    }

    public void sortChatMessages() {
        for (int i = 0; i < chatList.size(); ++i) {
            SortListUtil.sort(chatList.get(i).messages, "time", SortListUtil.ASC);
        }
    }

}
