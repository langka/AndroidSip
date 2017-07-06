package com.bupt.androidsip.entity;

/**
 * Created by xusong on 2017/7/1.
 * About:
 */

public class EventConst {
    public static class BaseEvent {

    }


    public static class Unread {
        private int howMany;

        public int getID() {
            return ID;
        }

        private int ID;

        public Unread(int howMany) {
            this.howMany = howMany;
            removeAll = false;
            removeOne = false;
        }

        public Unread(int howMany, boolean removeAll, boolean removeOne, int ID) {
            this.howMany = howMany;
            this.removeAll = removeAll;
            this.removeOne = removeOne;
            this.ID = ID;
        }

        public int getHowMany() {
            return howMany;
        }

        public boolean isRemoveAll() {
            return removeAll;
        }

        private boolean removeAll;

        public boolean isRemoveOne() {
            return removeOne;
        }

        private boolean removeOne;


    }


    public static class NewMsg {
        private int ID;
        private String msg;

        public int getID() {
            return ID;
        }

        public String getMsg() {
            return msg;
        }

        public NewMsg(int ID, String msg) {
            this.ID = ID;
            this.msg = msg;
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