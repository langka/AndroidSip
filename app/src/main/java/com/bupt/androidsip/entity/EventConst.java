package com.bupt.androidsip.entity;

/**
 * Created by xusong on 2017/7/1.
 * About:
 */

public class EventConst {
    public static class BaseEvent {

    }


    public static class Unread {
        public int howMany;

        public Unread(int howMany) {
            this.howMany = howMany;
            removeAll = false;
        }

        public Unread(int howMany, boolean removeAll) {
            this.howMany = howMany;
            this.removeAll = removeAll;
        }

        public int getHowMany() {
            return howMany;
        }

        public boolean removeAll;

    }

    public static class NewMessage {
        public String msg;
    }
}