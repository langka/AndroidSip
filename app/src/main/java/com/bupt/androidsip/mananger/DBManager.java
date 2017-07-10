package com.bupt.androidsip.mananger;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bupt.androidsip.entity.sip.SipChat;
import com.bupt.androidsip.entity.sip.SipMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xusong on 2017/7/10.
 * About:
 */

public class DBManager {
    public static class SipDBHelper extends SQLiteOpenHelper {

        private static final String TAG = "SipDBHelper";
        private static final String INIT_SQL = "create table if not exists messages(_id integer primary key autoincrement,type integer," +
                "createTime long,comeTime long,content text,belong integer,from integer,to text) ";
        private static final String DBNAME = "androidsip.db";
        private static final int VERSION = 1;

        public SipDBHelper(Context context) {
            super(context, DBNAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INIT_SQL);
            Log.d(TAG, "CREATE SUCCESS");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static DBManager instance;
    public static DBManager getInstance(Context context){
        if(instance==null){
            instance = new DBManager(context);
        }
        return instance;
    }
    private DBManager(Context context){
        helper = new SipDBHelper(context);
        db = helper.getWritableDatabase();
    }


    private SipDBHelper helper;
    private SQLiteDatabase db;

    public List<SipMessage> loadAllMessages(){
        Cursor cursor  = db.rawQuery("SELECT * FROM messages", null);
        ArrayList<SipMessage> messages = new ArrayList<>();
        while (cursor.moveToNext()){
            SipMessage sipMessage = new SipMessage();
            sipMessage.type = cursor.getInt(cursor.getColumnIndex("type"));
            sipMessage.createTime = cursor.getLong(cursor.getColumnIndex("createTime"));
            sipMessage.comeTime = cursor.getLong(cursor.getColumnIndex("comeTime"));
            sipMessage.belong = cursor.getInt(cursor.getColumnIndex("belong"));
            sipMessage.content = cursor.getString(cursor.getColumnIndex("content"));
            sipMessage.from = cursor.getInt(cursor.getColumnIndex("from"));
            List<Integer> toids = new ArrayList<>();
            String[] to = cursor.getString(cursor.getColumnIndex("to")).split("_");
            for(String a:to)
                toids.add(Integer.valueOf(a));
            sipMessage.to = toids;
            messages.add(sipMessage);
        }
        cursor.close();
        return messages;
    }
    public  void save(List<SipMessage> messages){

    }

    public void save(SipMessage message){

    }

    public void delete(int with){
        
    }
}
