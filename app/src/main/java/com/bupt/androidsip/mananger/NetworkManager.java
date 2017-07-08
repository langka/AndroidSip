package com.bupt.androidsip.mananger;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bupt.androidsip.SipAplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xusong on 2017/5/17.
 * About:网络请求管理
 */

public class NetworkManager {
    final static String IP = "http://10.209.8.209:8080/";
    final static String URL_LOGIN = IP + "login";
    final static String URL_REGISTER = IP + "register";

    public static void login(String name, String password, BaseListener listener) {
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL_LOGIN, null, null) {
            // 需要重写获取参数的函数,可以向服务器提交参数
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", name);
                map.put("password", password);
                return map;
            }

            ;
        };
        SipAplication.getInstance().addToRequestQueue(stringRequest1);
    }
}
