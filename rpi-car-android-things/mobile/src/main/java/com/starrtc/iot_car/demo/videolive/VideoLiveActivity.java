package com.starrtc.iot_car.demo.videolive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.starrtc.iot_car.R;
import com.starrtc.iot_car.demo.MLOC;
import com.starrtc.iot_car.demo.SplashActivity;
import com.starrtc.iot_car.demo.listener.XHLiveManagerListener;
import com.starrtc.iot_car.demo.ui.HandleView;
import com.starrtc.iot_car.utils.AEvent;
import com.starrtc.iot_car.utils.IEventListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHLiveManager;
import com.starrtc.starrtcsdk.apiInterface.IXHCallback;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.core.im.callback.IStarVideoCallback;
import com.starrtc.starrtcsdk.core.player.StarPlayer;

import org.json.JSONException;
import org.json.JSONObject;


import static com.starrtc.iot_car.demo.MLOC.*;

public class VideoLiveActivity extends Activity implements IEventListener {

    public static String CREATER_ID         = "CREATER_ID";          //创建者ID
    public static String LIVE_ID            = "LIVE_ID";            //直播ID
    public static String LIVE_NAME          = "LIVE_NAME";          //直播名称

    private XHLiveManager liveManager;
    private String createrId;
    private String liveId;
    private StarPlayer player1;
    private HandleView cameraOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_live);

        createrId = getIntent().getStringExtra(CREATER_ID);
        liveId = getIntent().getStringExtra(LIVE_ID);

        player1 = findViewById(R.id.player1);

        if(TextUtils.isEmpty(liveId)){
            showMsg(this,"信息不全");
            startActivity(new Intent(this, SplashActivity.class));
            finish();
            return;
        }

        liveManager = XHClient.getInstance().getLiveManager(this);
        liveManager.addListener(new XHLiveManagerListener());

        addListener();
        initContrlPanel();

        player1.addCallback(new IStarVideoCallback() {
            @Override
            public void onCreated() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinLive();
                    }
                });
            }
            @Override
            public void onError(int i) {

            }
        });
    }

    private void initContrlPanel(){
        cameraOp = findViewById(R.id.cameraOp);
        cameraOp.setHandleReaction(new HandleView.HandleReaction() {
            String lastData = "camera==";
            @Override
            public void report(float h, float v) {
                MLOC.d("IOTCAR","h:"+h+" v:"+v);

                String data;
                if(h<0.33){
                    if(v<0.33){
                        data  = "camera+-";
                    }else if(v>0.66){
                        data  = "camera++";
                    }else{
                        data  = "camera+=";
                    }
                }else if(h>0.66){
                    if(v<0.33){
                        data  = "camera--";
                    }else if(v>0.66){
                        data  = "camera-+";
                    }else{
                        data  = "camera-=";
                    }
                }else{
                    if(v<0.33){
                        data  = "camera=-";
                    }else if(v>0.66){
                        data  = "camera=+";
                    }else{
                        data  = "camera==";
                    }
                }
                if(!data.equals(lastData)){
                    lastData = data;
                    byte[] dataBytes = data.getBytes();
                    StarRtcCore.getInstance().sendRealtimeData(dataBytes,dataBytes.length);
                }
            }
        });

        findViewById(R.id.ctrl_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        byte[] data = "start".getBytes();
                        StarRtcCore.getInstance().sendRealtimeData(data,data.length);
                        switch (v.getId()){
                            case R.id.ctrl_up:
                                byte[] up = "up".getBytes();
                                StarRtcCore.getInstance().sendRealtimeData(up,up.length);
                                break;
                            case R.id.ctrl_down:
                                byte[] down = "down".getBytes();
                                StarRtcCore.getInstance().sendRealtimeData(down,down.length);
                                break;
                            case R.id.ctrl_left:
                                byte[] left = "left".getBytes();
                                StarRtcCore.getInstance().sendRealtimeData(left,left.length);
                                break;
                            case R.id.ctrl_right:
                                byte[] right = "right".getBytes();
                                StarRtcCore.getInstance().sendRealtimeData(right,right.length);
                                break;
                            case R.id.ctrl_reset:
                                byte[] reset = "cameraReset".getBytes();
                                StarRtcCore.getInstance().sendRealtimeData(reset,reset.length);
                                break;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        byte[] stop = "stop".getBytes();
                        StarRtcCore.getInstance().sendRealtimeData(stop,stop.length);
                        break;
                }
                return true;
            }
        };

        findViewById(R.id.ctrl_up).setOnTouchListener(touchListener);
        findViewById(R.id.ctrl_down).setOnTouchListener(touchListener);
        findViewById(R.id.ctrl_left).setOnTouchListener(touchListener);
        findViewById(R.id.ctrl_right).setOnTouchListener(touchListener);
        findViewById(R.id.ctrl_reset).setOnTouchListener(touchListener);

    }




    private void joinLive(){
        //观众加入直播
        liveManager.watchLive(liveId, new IXHCallback() {
            @Override
            public void success(Object data) {
                liveManager.applyToBroadcaster(createrId);
            }
            @Override
            public void failed(final String errMsg) {
                d("XHLiveManager","watchLive failed "+errMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMsg(VideoLiveActivity.this,errMsg);
                        removeListener();
                        finish();
                    }
                });

            }
        });
    }



    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT,this);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onRestart(){
        super.onRestart();
        addListener();
    }

    @Override
    public void onStop(){
        removeListener();
        super.onStop();
    }

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK_RESULT,this);

    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(VideoLiveActivity.this).setCancelable(true)
                .setTitle("是否停止遥控?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        stop();
                    }
                }
        ).show();
    }

    private void stop(){
        liveManager.leaveLive(liveId, new IXHCallback() {
            @Override
            public void success(Object data) {
                removeListener();
                startActivity(new Intent(VideoLiveActivity.this, SplashActivity.class));
                finish();
            }

            @Override
            public void failed(final String errMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showMsg(VideoLiveActivity.this,errMsg);
                    }
                });
                removeListener();
                startActivity(new Intent(VideoLiveActivity.this, SplashActivity.class));
                finish();
            }
        });
    }


    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        d("XHLiveManager","dispatchEvent  "+aEventID + eventObj);
        switch (aEventID){
            case AEvent.AEVENT_LIVE_ADD_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    final String addId = data.getString("actorID");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           if(addId.equals(createrId)){
                               liveManager.attachPlayerView(addId,player1,true);
                           }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_LIVE_REMOVE_UPLOADER:
               stop();
                break;

            case AEvent.AEVENT_LIVE_APPLY_LINK_RESULT:
                if((XHConstants.XHLiveJoinResult)eventObj== XHConstants.XHLiveJoinResult.XHLiveJoinResult_accept){
                    liveManager.changeToBroadcaster();
                }
                break;

            case AEvent.AEVENT_LIVE_ERROR:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String errStr = (String) eventObj;
                        showMsg(getApplicationContext(),errStr);
                        removeListener();
                        startActivity(new Intent(VideoLiveActivity.this, SplashActivity.class));
                        finish();
                    }
                });
                break;
        }
    }

}
