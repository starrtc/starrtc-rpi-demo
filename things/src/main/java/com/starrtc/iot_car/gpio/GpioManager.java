package com.starrtc.iot_car.gpio;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;
import com.starrtc.iot_car.demo.MLOC;

import java.io.IOException;

public class GpioManager {
    private static GpioManager instance;
    private GpioManager(){

    }
    public static GpioManager getInstance(){
        if(instance==null)
            instance = new GpioManager();
        return instance;
    }

    private PeripheralManager manager;
    private Gpio mGpioLeftRun;
    private Gpio mGpioLeftDirection;
    private Gpio mGpioRightRun;
    private Gpio mGpioRightDirection;
    private final String GpioNameLeftRun = "BCM10";
    private final String GpioNameLeftDirection = "BCM25";
    private final String GpioNameRightRun = "BCM17";
    private final String GpioNameRightDirection = "BCM4";




    //初始化小车需要的GPIO口
    public void initCarGpio(){
        manager = PeripheralManager.getInstance();
        try {
            mGpioLeftRun = manager.openGpio(GpioNameLeftRun);
            resetGpio(mGpioLeftRun);
            mGpioLeftDirection = manager.openGpio(GpioNameLeftDirection);
            resetGpio(mGpioLeftDirection);
            mGpioRightRun = manager.openGpio(GpioNameRightRun);
            resetGpio(mGpioRightRun);
            mGpioRightDirection = manager.openGpio(GpioNameRightDirection);
            resetGpio(mGpioRightDirection);
        } catch (IOException e) {
            MLOC.d("IOTCAR","initCarGpio IOException"+e.getMessage());
            e.printStackTrace();
        }
        MLOC.d("IOTCAR","initCarGpio");
    }

    //关闭车
    public void stopCarGpio(){
        destoryGpio(mGpioLeftRun);
        destoryGpio(mGpioLeftDirection);
        destoryGpio(mGpioRightRun);
        destoryGpio(mGpioRightDirection);

        MLOC.d("IOTCAR","stopCarGpio");
    }



    public void controlCar(byte[] data){
        try {
            String command = new String(data);
            MLOC.d("iot_car","revCtrlCommand "+ command);
            MLOC.d("IOTCAR_PWM","controlCar");
            switch (command){
                case "up":
                    mGpioLeftDirection.setValue(false);
                    mGpioRightDirection.setValue(false);
                    return;
                case "down":
                    mGpioLeftDirection.setValue(true);
                    mGpioRightDirection.setValue(true);
                    return;
                case "left":
                    mGpioLeftDirection.setValue(true);
                    mGpioRightDirection.setValue(false);
                    return;
                case "right":
                    mGpioLeftDirection.setValue(false);
                    mGpioRightDirection.setValue(true);
                    return;
                case "start":
                    mGpioLeftDirection.setValue(false);
                    mGpioRightDirection.setValue(true);
                    mGpioLeftRun.setValue(true);
                    mGpioRightRun.setValue(true);
                    return;
                case "stop":
                    mGpioLeftRun.setValue(false);
                    mGpioRightRun.setValue(false);
                    return;
                case "camera++":
                case "camera+-":
                case "camera+=":
                case "camera-+":
                case "camera--":
                case "camera-=":
                case "camera=+":
                case "camera=-":
                case "camera==":
                case "cameraReset":
                    PwmManager.getInstance().gotCommand(command);
                    return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //重置GPIO
    private void resetGpio(Gpio gpio){
        try {
            if(gpio!=null) {
                gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);//设置为输出，默认低电平
                gpio.setActiveType(Gpio.ACTIVE_HIGH);//设置高电平为活跃的
                gpio.setValue(false);//设置成低电平
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //销毁GPIO
    private void destoryGpio(Gpio gpio){
        try {
            if(gpio!=null){
                gpio.close();
                gpio = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
