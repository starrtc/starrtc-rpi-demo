package com.starrtc.iot_car.demo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.starrtc.iot_car.R;
import com.starrtc.iot_car.demo.listener.XHChatManagerListener;
import com.starrtc.iot_car.demo.listener.XHLoginManagerListener;
import com.starrtc.iot_car.demo.serverAPI.InterfaceUrls;
import com.starrtc.iot_car.demo.videolive.SampleLiveActivity;
import com.starrtc.iot_car.gpio.GpioManager;
import com.starrtc.iot_car.utils.AEvent;
import com.starrtc.iot_car.utils.IEventListener;
import com.starrtc.iot_car.utils.StarNetUtil;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHSDKConfig;
import com.starrtc.starrtcsdk.apiInterface.IXHCallback;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements IEventListener {
    private boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_splash);

        addListener();
        GpioManager.getInstance().initCarGpio();
        GpioManager.getInstance().stopCarGpio();
        MLOC.userId = "car0001";
        MLOC.saveSharedData(getApplicationContext(),"userId",MLOC.userId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String IP = StarNetUtil.getHostIP()+"/"+StarNetUtil.getNetIp();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView)findViewById(R.id.ip_text)).setText(IP);
                    }
                });
            }
        }).start();


    }


    private Timer checkNetTimer = new Timer();
    private void checkNetAvailable(){
        checkNetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                Process pingProcess = null;
                try {
                    String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
                    //时间是否已经同步
                    if(nowDate.contains("201")){
                        checkNetTimer.cancel();
                        InterfaceUrls.demoLogin(MLOC.userId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },3000,3000);
    }


    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onResume(){
        super.onResume();
        isLogin = XHClient.getInstance().getIsOnline();
        if(isLogin){
            startAnimation();
        }else{
            MLOC.init(getApplicationContext());
            XHClient.getInstance().setDefaultConfig(true,true,
                    0,0,
                    1,false,false,
                    false,false,
                    XHConstants.XHCropTypeEnum.STAR_VIDEO_CROP_CONFIG_BIG_NOCROP_SMALL_NONE);
            XHClient.getInstance().setCustomEncoderConfig(640,480,
                    640,480,15,500,45);
            XHClient.getInstance().initSDK(this, new XHSDKConfig(MLOC.agentId),MLOC.userId);
            XHClient.getInstance().getChatManager().addListener(new XHChatManagerListener());
            XHClient.getInstance().getLoginManager().addListener(new XHLoginManagerListener());

            startAnimation();
            checkNetAvailable();
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    private void addListener(){
        AEvent.addListener(AEvent.AEVENT_LOGIN,this);
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG,this);
    }

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LOGIN,this);
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG,this);
    }

    private void startCar(String fromId){
        removeListener();
        Intent intent = new Intent(SplashActivity.this,SampleLiveActivity.class);
        intent.putExtra("driverId",fromId);
        startActivity(intent);
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        switch (aEventID){
            case AEvent.AEVENT_LOGIN:
                if(success){
                    MLOC.d("", (String) eventObj);
                    XHClient.getInstance().getLoginManager().login(MLOC.authKey, new IXHCallback() {
                        @Override
                        public void success(Object data) {
                            isLogin = true;
                        }

                        @Override
                        public void failed(final String errMsg) {
                            MLOC.d("",errMsg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MLOC.showMsg(SplashActivity.this,errMsg);
                                }
                            });
                        }
                    });
                }else{
                    MLOC.d("", (String) eventObj);
                }
                break;
            case AEvent.AEVENT_C2C_REV_MSG:
                XHIMMessage message = (XHIMMessage) eventObj;
                String command = message.contentData;
                switch (command){
                    case "IotCarStart":
                        startCar(message.fromId);
                        break;
                }
                break;
        }
    }

    @SuppressLint("WrongConstant")
    private void startAnimation(){
        final View eye = findViewById(R.id.eye);
        eye.setAlpha(0.2f);
        final View black = findViewById(R.id.black_view);
        final View white = findViewById(R.id.white_view);

        final ObjectAnimator va = ObjectAnimator.ofFloat(eye,"alpha",0.2f,1f);
        va.setDuration(1000);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(Animation.REVERSE);
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                 if(isLogin){
                     va.cancel();
                    ObjectAnimator va1 = ObjectAnimator.ofFloat(white,"alpha",0f,1f);
                    ObjectAnimator va2 = ObjectAnimator.ofFloat(black,"alpha",1f,0f);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(1500);
                    animatorSet.playTogether(va1,va2);
                    animatorSet.start();
                }
            }
        });
        va.start();
    }



}
