package com.starrtc.iot_car.demo.serverAPI;

import android.os.AsyncTask;
import android.os.Bundle;

import com.starrtc.iot_car.demo.MLOC;
import com.starrtc.iot_car.utils.AEvent;
import com.starrtc.iot_car.utils.ICallback;
import com.starrtc.iot_car.utils.StarHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by zhangjt on 2017/8/17.
 */

public class InterfaceUrls {
    public static final String BASE_URL = "https://api.starrtc.com";
    //获取authKey
    public static final String LOGIN_URL = BASE_URL+"/demo/authKey";

    public static void demoLogin(String userId){
        String url = LOGIN_URL+"?userid="+userId;
        String params = "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("starUid",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params = jsonObject.toString();

        StarHttpUtil httpPost = new StarHttpUtil(StarHttpUtil.REQUEST_METHOD_GET);
        httpPost.addListener(new ICallback() {
            @Override
            public void callback(boolean reqSuccess, String statusCode, String data) {
                if(reqSuccess){
                    if(statusCode.equals("1")){
                        MLOC.authKey = data;
                        AEvent.notifyListener(AEvent.AEVENT_LOGIN,true,"登录成功");
                        return;
                    }
                }
                AEvent.notifyListener(AEvent.AEVENT_LOGIN,false,"登录失败！");
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(StarHttpUtil.URL,url);
        bundle.putString(StarHttpUtil.DATA,params);
        httpPost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,bundle);
    }


}
