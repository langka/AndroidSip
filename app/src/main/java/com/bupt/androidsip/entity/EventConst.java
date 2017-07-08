package com.bupt.androidsip.entity;

/**
 * Created by xusong on 2017/7/1.
 * About:
 */

public class EventConst {
    public static class BaseEvent {

    }

    public static class RemoveAll {
        private boolean removaAll;

        public boolean isRemovaAll() {
            return removaAll;
        }

        public RemoveAll(boolean removaAll) {
            this.removaAll = removaAll;
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