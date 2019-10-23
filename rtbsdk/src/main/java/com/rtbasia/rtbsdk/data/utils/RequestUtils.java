package com.rtbasia.rtbsdk.data.utils;

import android.Manifest;
import android.util.Log;

import com.rtbasia.rtbsdk.RTBEngine;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Create by Juan.gong 2019-10-22
 */
public class RequestUtils {


    public static void getDataFromNetGet(final String url, final RequestCallback oncallback) {
        if (PermissionUtils.checkPermission(Manifest.permission.INTERNET)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    DataInputStream dis = null;
                    try {
                        URL temUrl = new URL(url);
                        connection = (HttpURLConnection) temUrl.openConnection();
                        connection.setConnectTimeout(5000);//设置请求超时时间
                        connection.setRequestMethod("GET");//设置请求方式
                        connection.connect();//建立Http连接
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            //获取响应流
                            dis = new DataInputStream(connection.getInputStream());
                            StringBuffer inputLine = new StringBuffer();
                            String tmp = "";
                            while ((tmp = dis.readLine()) != null) {
                                inputLine.append(tmp);
                            }
                            Log.i(RTBEngine.TAG, "网络成功：" + inputLine.toString());
                            oncallback.onRequestCallback(inputLine.toString());
                            dis.close();
                        } else {
                            Log.i(RTBEngine.TAG, "网络请求失败：" + connection.getResponseCode());
                            oncallback.onRequestCallback("");
                        }
                    } catch (Exception e) {
                        oncallback.onRequestCallback("");
                        if (dis != null) {
                            try {
                                dis.close();
                            } catch (IOException ex) {
                                e.printStackTrace();
                            }
                        }
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }).start();
        }
    }


    public static void getDataFromNetPost(final String url, byte[] body) {

        if (PermissionUtils.checkPermission(Manifest.permission.INTERNET)) {
            final byte[] data = body;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(RTBEngine.TAG, url);

                    DataInputStream dis = null;
                    try {
                        URL temUrl = new URL(url);
                        HttpURLConnection connection = (HttpURLConnection) temUrl.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(5000);
                        connection.setDoOutput(true);//设置输出流，允许提交数据
                        connection.setDoInput(true);//设置输入流，允许读取响应
                        connection.setUseCaches(false);//post方式请求不允许缓存
                        //设置请求体的类型是文本类型
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        //向服务器提交请求体，此处实际打开连接了
                        OutputStream outputStream = connection.getOutputStream();
                        outputStream.write(data);
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK
                                || connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                            dis = new DataInputStream(connection.getInputStream());
                            String tmp = "";
                            StringBuffer inputLine = new StringBuffer();
                            while ((tmp = dis.readLine()) != null) {
                                inputLine.append(tmp);
                            }
                            Log.i(RTBEngine.TAG, "网络成功：" + inputLine.toString() + tmp);
                        } else {
                            Log.i(RTBEngine.TAG, "网络请求失败：" + connection.getResponseCode());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * 将参数封装为键值对
     *
     * @param params
     * @param encode
     * @return
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
}
