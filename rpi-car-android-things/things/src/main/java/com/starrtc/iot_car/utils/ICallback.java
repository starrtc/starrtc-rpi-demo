package com.starrtc.iot_car.utils;

public interface ICallback {
    abstract  void callback(boolean reqSuccess, String statusCode, String data);
}
