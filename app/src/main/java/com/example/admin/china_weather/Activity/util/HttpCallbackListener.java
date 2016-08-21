package com.example.admin.china_weather.Activity.util;

/**
 * Created by admin on 2016/8/21.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
