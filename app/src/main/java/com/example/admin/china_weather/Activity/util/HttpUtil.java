package com.example.admin.china_weather.Activity.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 2016/8/21.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
         new Thread(new Runnable() {
             @Override
             public void run() {
                 HttpURLConnection connection = null;
                 try {
                     URL url = new URL(address);
                     connection = (HttpURLConnection) url.openConnection();
                     connection.setRequestMethod("GET");
                     connection.setConnectTimeout(8000);
                     connection.setReadTimeout(8000);
                     InputStream inputStream = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                     StringBuilder response = new StringBuilder();
                     String line;
                     while ((line = reader.readLine()) != null){
                         response.append(line);
                     }
                             if (listener != null){
                                 //回调onFinsh()方法
                                 listener.onFinish(response.toString());
                             }

                 } catch (MalformedURLException e) {
                     if (listener != null){
                         //回调onError()方法
                         listener.onError(e);
                     }
                     e.printStackTrace();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }finally {
                     if (connection != null){
                         connection.disconnect();
                     }
                 }
             }
         }).start();
    }
}
