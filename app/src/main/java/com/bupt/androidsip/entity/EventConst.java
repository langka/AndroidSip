package com.bupt.androidsip.entity;

/**
 * Created by xusong on 2017/7/1.
 * About:
 */

public class EventConst {
    public static class BaseEvent {

    }

    public static class RemoveAll {
        private boolean removeAll;

        public boolean isRemoveAll() {
            return removeAll;
        }

        public RemoveAll(boolean removeAll) {
            this.removeAll = removeAll;
        }
    }

    public static class RemoveOne {
        private int howMany;
        private int ID;

        public RemoveOne(int howMany, int ID) {
            this.howMany = howMany;

            this.ID = ID;
        }

        public int getHowMany() {
            return howMany;
        }

        public int getID() {
            return ID;
        }
    }

    public static class Unread {
        private int howMany;

        public int getID() {
            return ID;
        }

        private int ID;

        public Unread(int howMany, int ID) {
            this.howMany = howMany;
            this.ID = ID;
        }

        public int getHowMany() {
            return howMany;
        }


    }


    public static class NewMsg {
        private User user;
        private String msg;
        private long time;

        public User getUser() {
            return user;
        }

        public String getMsg() {
            return msg;
        }

        public long getTime() {
            return time;
        }

        public NewMsg(User user, String msg, long time) {
            this.user = user;
            this.msg = msg;
            this.time = time;
        }
    }

    public static class LastMsg {
        private int ID;
        private String msg;

        public int getID() {
            return ID;
        }

        public String getMsg() {
            return msg;
        }

        public LastMsg(int ID, String msg) {
            this.ID = ID;
            this.msg = msg;
        }
    }

}