package com.bupt.androidsip.mananger;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.gov.nist.javax.sip.message.Content;
import android.util.Log;

import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.sip.SipChat;
import com.bupt.androidsip.entity.sip.SipMessage;
import com.bupt.androidsip.entity.sip.SipSystemMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bupt.androidsip.mananger.DBManager.SipDBHelper.INIT_SQL;

/**
 * Created by xusong on 2017/7/10.
 * About:
 */

public class DBManager {
    public static class SipDBHelper extends SQLiteOpenHelper {

        private static final String TAG = "SipDBHelper";
         static final String INIT_SQL = "create table message (_id integer primary key autoincrement,type integer," +
                "createTime long,comeTime long,content text,belong integer,fromid integer,toid integer)";
        static final String INIT_SQL2 = "create table event (_id integer primary key autoincrement,associated integer,name text,type integer,dealed integer)";
        private static final String DBNAME = "androidsip.db";
        private static final int VERSION = 1;

        public SipDBHelper(Context context) {
            super(context, DBNAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INIT_SQL);
            db.execSQL(INIT_SQL2);
            Log.d(TAG, "CREATE SUCCESS");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static DBManager instance;

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    private DBManager(Context context) {
        helper = new SipDBHelper(context);
        db = helper.getWritableDatabase();
    }


    private SipDBHelper helper;
    private SQLiteDatabase db;

    public List<SipSystemMessage> getAllSystemEvents(){
        Cursor cursor = db.rawQuery("SELECT * FROM event",null);
        ArrayList<SipSystemMessage> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            SipSystemMessage sipMessage = new SipSystemMessage();
            User user = new User();
            sipMessage.id = cursor.getInt(cursor.getColumnIndex("_id"));
            user.name = cursor.getString(cursor.getColumnIndex("name"));
            user.id = cursor.getInt(cursor.getColumnIndex("associated"));
            sipMessage.assoicatedUser = user;
            sipMessage.type = cursor.getInt(cursor.getColumnIndex("type"));
            sipMessage.state = cursor.getInt(cursor.getColumnIndex("dealed"));
            messages.add(sipMessage);
        }
        cursor.close();
        return messages;
    }

    public void saveEvent(SipSystemMessage message){
        ContentValues contentValues = new ContentValues();
        contentValues.put("associated",message.assoicatedUser.id);
        contentValues.put("name",message.assoicatedUser.name);
        contentValues.put("type",message.type);
        contentValues.put("dealed",message.state);
        db.insert("event",null,contentValues);
    }

    public void updateState(int id,int state){
        ContentValues contentValues = new ContentValues();
        contentValues.put("dealed",state);
        db.update("event",contentValues,"_id=?",new String[]{String.valueOf(id)});
    }

    public List<SipMessage> loadAllMessages() {
        Cursor cursor = db.rawQuery("SELECT * FROM message", null);
        ArrayList<SipMessage> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            SipMessage sipMessage = new SipMessage();
            sipMessage.type = cursor.getInt(cursor.getColumnIndex("type"));
            sipMessage.createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
            sipMessage.comeTime = cursor.getLong(cursor.getColumnIndex("comeTime"));
            sipMessage.belong = cursor.getInt(cursor.getColumnIndex("belong"));
            sipMessage.content = cursor.getString(cursor.getColumnIndex("content"));
            sipMessage.from = cursor.getInt(cursor.getColumnIndex("fromid"));
            List<Integer> toids = new ArrayList<>();
            int to = cursor.getInt(cursor.getColumnIndex("toid"));
            toids.add(to);
            sipMessage.to = toids;
            messages.add(sipMessage);
        }
        cursor.close();
        return messages;
    }

    public void save(List<SipMessage> messages) {
        for (SipMessage sipMessage : messages) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("type", 0);
            contentValues.put("createTime", System.currentTimeMillis());
            contentValues.put("comeTime", System.currentTimeMillis());
            contentValues.put("belong", -1);
            contentValues.put("content", "hello,world");
            contentValues.put("fromid", 3);
            contentValues.put("toid", 5);
            db.insert("message", null, contentValues);
        }

    }

    public void save(SipMessage message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", 0);
        contentValues.put("createTime", message.createTime);
        contentValues.put("comeTime", -1);
        contentValues.put("belong", -1);
        contentValues.put("content", message.content);
        contentValues.put("fromid", message.from);
        contentValues.put("toid",message.to.get(0));
        db.insert("message", null, contentValues);
    }

    public void delete(int with) {
        String sql = "delete from message where fromid = "+with+" or toid = "+with;
        db.execSQL(sql);
    }

    public void clearMsg() {
        String sql = "delete from message";  //清空数据
        String sql2 = "update sqlite_sequence SET seq = 0 where name ='message'";
        db.execSQL(sql);
        db.execSQL(sql2);
    }

    public void clearSysMsg(){
        String sql = "delete from event";//清除系统消息
        String sql2 = "update sqlite_sequence SET seq = 0 where name ='event'";
        db.execSQL(sql);
        db.execSQL(sql2);
    }


}
