package com.starrtc.iot_car.gpio;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;
import com.starrtc.iot_car.demo.MLOC;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PwmManager {
    private static PwmManager instance;
    private PwmManager(){

    }
    public static PwmManager getInstance(){
        if(instance==null)
            instance = new PwmManager();
        return instance;
    }

    //GPIO13,GPIO18;
    private Pwm mPwmCameraV;
    private final String GpioNameVRotate = "PWM0";
    private Pwm mPwmCameraH;
    private final String GpioNameHRotate = "PWM1";
    private double stepLenght = 0.0463;
    private double beginValue = 3.27;

    private String lastCommand = "";

    public AtomicBoolean running = new AtomicBoolean(true);
    public AtomicInteger camearV = new AtomicInteger(30);
    public AtomicInteger camearH = new AtomicInteger(90);
    private int lastCameraV = 30;
    private int lastCameraH = 90;

    private PeripheralManager manager;

    public void startPwm(){
        MLOC.d("IOTCAR_PWM","startPwm");
        new PwmThread().start();
    }

    public void initCarPwm(){
        MLOC.d("IOTCAR_PWM","initCarPwm");
        manager = PeripheralManager.getInstance();
        try {
            if(mPwmCameraH!=null) {
                mPwmCameraH.setEnabled(false);
                mPwmCameraH.close();
                mPwmCameraH = null;
            }
            running.set(true);
            mPwmCameraH = manager.openPwm(GpioNameHRotate);
            mPwmCameraH.setPwmDutyCycle(beginValue+90*stepLenght);
            mPwmCameraH.setPwmFrequencyHz(50);
            mPwmCameraH.setEnabled(true);

            if(mPwmCameraV!=null) {
                mPwmCameraV.setEnabled(false);
                mPwmCameraV.close();
                mPwmCameraV = null;
            }
            mPwmCameraV = manager.openPwm(GpioNameVRotate);
            mPwmCameraV.setPwmDutyCycle(beginValue+40*stepLenght);
            mPwmCameraV.setPwmFrequencyHz(50);
            mPwmCameraV.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gotCommand(String data){
        lastCommand = data;
    }

    public void changePwm(){
        try {
            if(mPwmCameraV!=null&&lastCameraV!=camearV.get()){
                mPwmCameraV.setEnabled(false);
                mPwmCameraV.setPwmDutyCycle(beginValue+camearV.get()*stepLenght);
                mPwmCameraV.setEnabled(true);
                lastCameraV = camearV.get();
            }
            if(mPwmCameraH!=null&&lastCameraH!=camearH.get()){
                mPwmCameraH.setEnabled(false);
                mPwmCameraH.setPwmDutyCycle(beginValue+camearH.get()*stepLenght);
                mPwmCameraH.setEnabled(true);
                lastCameraH = camearH.get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPwm(){
        running.set(false);
        if(mPwmCameraH!=null){
            try {
                mPwmCameraH.setEnabled(false);
                mPwmCameraH.close();
                mPwmCameraH = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(mPwmCameraV!=null){
            try {
                mPwmCameraV.setEnabled(false);
                mPwmCameraV.close();
                mPwmCameraV = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MLOC.d("IOTCAR_PWM","stopPwm");
    }

    class PwmThread extends Thread{
        @Override
        public void run() {
            super.run();
            MLOC.d("IOTCAR_PWM","PwmThread run");
            initCarPwm();

            while (running.get()){
                MLOC.d("IOTCAR_PWM","changePwm");
                switch (lastCommand){
                    case "camera++":
                        camearH.set(camearH.get()+2);
                        if(camearH.get()>180){
                            camearH.set(180);
                        }
                        camearV.set(camearV.get()+2);
                        if(camearV.get()>120){
                            camearV.set(120);
                        }
                        break;
                    case "camera+-":
                        camearH.set(camearH.get()+2);
                        if(camearH.get()>180){
                            camearH.set(180);
                        }
                        camearV.set(camearV.get()-2);
                        if(camearV.get()<0){
                            camearV.set(0);
                        }
                        break;
                    case "camera+=":
                        camearH.set(camearH.get()+2);
                        if(camearH.get()>180){
                            camearH.set(180);
                        }
                        break;
                    case "camera-+":
                        camearH.set(camearH.get()-2);
                        if(camearH.get()<0){
                            camearH.set(0);
                        }
                        camearV.set(camearV.get()+2);
                        if(camearV.get()>90){
                            camearV.set(90);
                        }
                        break;
                    case "camera--":
                        camearH.set(camearH.get()-2);
                        if(camearH.get()<0){
                            camearH.set(0);
                        }
                        camearV.set(camearV.get()-2);
                        if(camearV.get()<0){
                            camearV.set(0);
                        }
                        break;
                    case "camera-=":
                        camearH.set(camearH.get()-2);
                        if(camearH.get()<0){
                            camearH.set(0);
                        }
                        break;
                    case "camera=+":
                        camearV.set(camearV.get()+2);
                        if(camearV.get()>120){
                            camearV.set(120);
                        }
                        break;
                    case "camera=-":
                        camearV.set(camearV.get()-2);
                        if(camearV.get()<0){
                            camearV.set(0);
                        }
                        break;
                    case "camera==":
                        break;
                    case "cameraReset":
                        camearH.set(90);
                        camearV.set(30);
                        break;
                }
                changePwm();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            MLOC.d("IOTCAR_PWM","PwmThread stoped");
        }
    }
}
