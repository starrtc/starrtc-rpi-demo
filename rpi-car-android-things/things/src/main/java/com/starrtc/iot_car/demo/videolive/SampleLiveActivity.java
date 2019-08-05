package com.starrtc.iot_car.demo.videolive;

import android.app.Activity;
import android.os.Bundle;

import com.starrtc.iot_car.demo.MLOC;
import com.starrtc.iot_car.demo.listener.XHLiveManagerListener;
import com.starrtc.iot_car.demo.serverAPI.InterfaceUrls;
import com.starrtc.iot_car.gpio.GpioManager;
import com.starrtc.iot_car.gpio.PwmManager;
import com.starrtc.iot_car.utils.AEvent;
import com.starrtc.iot_car.utils.IEventListener;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHLiveItem;
import com.starrtc.starrtcsdk.api.XHLiveManager;
import com.starrtc.starrtcsdk.apiInterface.IXHCallback;
import com.starrtc.starrtcsdk.core.StarRtcCore;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SampleLiveActivity extends Activity implements IEventListener {
    private static final String TAG = SampleLiveActivity.class.getSimpleName();
    private XHLiveManager liveManager;
    private String liveId;
    private String driverId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        liveId = MLOC.loadSharedData(this,MLOC.userId+"_iotCarId");
        liveManager = XHClient.getInstance().getLiveManager(this);
        liveManager.addListener(new XHLiveManagerListener());

        driverId = getIntent().getStringExtra("driverId");
        if(driverId.isEmpty()){
            finish();
            return;
        }
        GpioManager.getInstance().initCarGpio();
        PwmManager.getInstance().startPwm();
        addListener();
        if(liveId.equals("")){
            createLive();
        }else{
            starLive();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void createLive(){
        //创建直播
        final XHLiveItem liveItem = new XHLiveItem();
        liveItem.setLiveType(XHConstants.XHLiveType.XHLiveTypeGlobalPublic);
        liveItem.setLiveName(MLOC.userId);
        //创建直播间
        liveManager.createLive(liveItem, new IXHCallback() {
            @Override
            public void success(Object data) {
                //创建成功
                MLOC.d("XHLiveManager","createLive success "+data);
                liveId = (String) data;
                MLOC.saveSharedData(SampleLiveActivity.this,MLOC.userId+"_iotCarId", liveId);
                InterfaceUrls.demoReportLive(liveId,liveItem.getLiveName(),MLOC.userId);
                starLive();
            }
            @Override
            public void failed(final String errMsg) {
                //创建失败
                MLOC.d("XHLiveManager","createLive failed "+errMsg);
                removeListener();
                finish();
            }
        });
    }

    private void starLive(){
        //开始直播
        liveManager.startLive(liveId, new IXHCallback() {
            @Override
            public void success(Object data) {
                //成功
                MLOC.d("XHLiveManager","startLive success "+data);
                //给操控者发送直播间ID
                XHClient.getInstance().getChatManager().sendOnlineMessage(liveId, driverId, null);
            }
            @Override
            public void failed(final String errMsg) {
                //失败
                MLOC.d("XHLiveManager","startLive failed "+errMsg);
                MLOC.saveSharedData(SampleLiveActivity.this,MLOC.userId+"_iotCarId", "");
                stop();
            }
        });
    }

    public void addListener(){
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_APPLY_LINK,this);
        AEvent.addListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,this);
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

    private void removeListener(){
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_ERROR,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REMOVE_UPLOADER,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_APPLY_LINK,this);
        AEvent.removeListener(AEvent.AEVENT_LIVE_REV_REALTIME_DATA,this);
    }

    private void stop(){
        liveManager.leaveLive(liveId, new IXHCallback() {
            @Override
            public void success(Object data) {
                removeListener();
                GpioManager.getInstance().stopCarGpio();
                PwmManager.getInstance().stopPwm();
                finish();
            }

            @Override
            public void failed(final String errMsg) {
                removeListener();
                GpioManager.getInstance().stopCarGpio();
                PwmManager.getInstance().stopPwm();
                finish();
            }
        });
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        MLOC.d("XHLiveManager","dispatchEvent  "+aEventID + eventObj);
        switch (aEventID){
            case AEvent.AEVENT_C2C_REV_MSG:
                XHIMMessage message = (XHIMMessage) eventObj;
                if(message.fromId.equals(driverId)){
                    String command = message.contentData;
                    if(command.equals("IotCarStart")){
                        XHClient.getInstance().getChatManager().sendOnlineMessage(liveId, driverId, null);
                    }
                }
                break;
            case AEvent.AEVENT_LIVE_ADD_UPLOADER:
                //连麦者加入，因为小车不需要播放，所以设置为不接收视频
                StarRtcCore.getInstance().setNullVideo();
                break;
            case AEvent.AEVENT_LIVE_REMOVE_UPLOADER:
                //操控者退出，本次操控结束
                driverId = "";
                stop();
                break;
            case AEvent.AEVENT_LIVE_APPLY_LINK:
                //收到连麦申请
                if(driverId.equals((String) eventObj)){
                    // 操控者申请，自动同意上麦
                    liveManager.agreeApplyToBroadcaster(driverId);
                }else{
                    // 拒绝其他人上麦
                    liveManager.refuseApplyToBroadcaster((String) eventObj);
                }
                break;
            case AEvent.AEVENT_LIVE_ERROR:
                removeListener();
                finish();
                MLOC.d("VideoLiveActivity","AEVENT_LIVE_ERROR  "+eventObj);
                break;
            case AEvent.AEVENT_LIVE_REV_REALTIME_DATA:
                // 收到实时流数据，操控车的指令
                if(success){
                    try {
                        JSONObject jsonObject = (JSONObject) eventObj;
                        final byte[] tData = (byte[]) jsonObject.get("data");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //给小车下达指令
                                GpioManager.getInstance().controlCar(tData);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

}
