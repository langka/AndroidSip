package com.bupt.androidsip;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.bupt.androidsip.sip.impl.SipManager;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by xusong on 2017/5/17.
 * About:
 */

public class SipAplication extends Application {
    private RequestQueue requestQueue;
    private static SipAplication sipAplication;
    Handler handler;
    public static synchronized SipAplication getInstance() {
        return sipAplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sipAplication = this;
        handler = new Handler();
        SipManager.prepareManager(handler,this);
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;

    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
