package com.rtbasia.rtbsdk.data.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.rtbasia.rtbsdk.RTBEngine;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class ConnectUtils {

    //private static final String CHARSET = "UTF-8";
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private static final int CONNECT_TIMEOUT = 30 * 1000;
    private static final int READ_TIMEOUT = 30 * 1000;

    private static ConnectUtils instance;
    private static TrustManager[] trustAllManager = {new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    private ConnectUtils() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllManager, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static ConnectUtils getInstance() {
        if (instance == null) {
            synchronized (ConnectUtils.class) {
                if (instance == null) {
                    instance = new ConnectUtils();
                }
            }
        }
        return instance;
    }

    public String get(String destURL) {
        byte[] ipBytes = performGet(destURL);

        return ipBytes == null ? "" : new String(ipBytes, Charset.forName("UTF-8"));
    }

    public byte[] performGet(String destURL) {
        //Logger.d("Attempting Get to " + destURL + "\n");
        byte[] response = null;
        HttpURLConnection httpConnection = null;
        InputStream is = null;

        try {
            String encodedUrl = Uri.encode(destURL, ALLOWED_URI_CHARS);

            URL url = new URL(encodedUrl);

            httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setConnectTimeout(CONNECT_TIMEOUT);
            httpConnection.setReadTimeout(READ_TIMEOUT);
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            int statusCode = httpConnection.getResponseCode();


            if (statusCode == HttpURLConnection.HTTP_OK || statusCode == HttpURLConnection.HTTP_MOVED_PERM || statusCode == HttpURLConnection.HTTP_MOVED_TEMP) {

                try {
                    is = httpConnection.getInputStream();
                    response = writeToArr(is);
                } catch (Exception e) {
                    response = new byte[]{};
                }

                //redirect
                String redirectURL = httpConnection.getHeaderField("Location");
                if (!TextUtils.isEmpty(redirectURL)) {
                    httpConnection = (HttpURLConnection) new URL(redirectURL).openConnection();
                    statusCode = httpConnection.getResponseCode();
                    //Logger.d("redirect statusCode::" + statusCode);
                }
            }
        } catch (Exception e) {
        } finally {
            IOUtil.closeGracefully(is);

            if (null != httpConnection)
                httpConnection.disconnect();
        }

        return response;
    }

    /**
     * 读取到Buffer内，转换成byte[]数组
     */
    private static byte[] writeToArr(final InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[8192];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        // buffer.close();
        return buffer.toByteArray();
    }

    public String post(String url, byte[] data) {
        byte[] respBytes = performPost(url, data, false);
        return respBytes == null ? "" : new String(respBytes, Charset.forName("UTF-8"));
    }

    public byte[] performPost(String destURL, byte[] data, boolean useGzip) {
        //Logger.d("Attempting Post to " + destURL + "\n");

        byte[] response = null;

        OutputStream os = null;
        BufferedOutputStream bos = null;
        HttpURLConnection httpConnection = null;
        InputStream is = null;

        try {
            URL url = new URL(destURL);
            httpConnection = (HttpURLConnection) url.openConnection();

            httpConnection.setConnectTimeout(CONNECT_TIMEOUT);
            httpConnection.setReadTimeout(READ_TIMEOUT);
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/octet-stream");
            //httpConnection.setRequestProperty("Accept-Encoding", "gzip,deflate");

            if (useGzip) {
                httpConnection.setRequestProperty("Content-Encoding", "gzip");
            }
            os = httpConnection.getOutputStream();//upload

            if (useGzip) {
//                byte[] buffer = eGzip(data.getBytes("UTF-8"));
                byte[] buffer = eGzip(data);
                os.write(buffer);
                os.flush();
            } else {
                bos = new BufferedOutputStream(os);
//                bos.write(data.getBytes("UTF-8"));
                bos.write(data);
                bos.flush();
            }

            int statusCode = httpConnection.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK || statusCode== HttpURLConnection.HTTP_NO_CONTENT) {
                // 使用普通流读取
                is = httpConnection.getInputStream();
                response = writeToArr(is);
            } else {
                Log.w(RTBEngine.TAG,"post data fail status:" + statusCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeGracefully(bos);
            IOUtil.closeGracefully(os);
            IOUtil.closeGracefully(is);

            if (null != httpConnection)
                httpConnection.disconnect();
        }

        return response;
    }

    /**
     * 把byte 通过GZipStream封装
     *
     * @param content
     * @return
     */
    private static byte[] eGzip(byte[] content) {
        GZIPOutputStream gos = null;
        try {
            // 通过一个缓冲的byte[] 对标准输出流进行封装,不需要主动close
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(baos);
            gos.write(content);
            gos.finish();
            gos.close();
            gos = null;
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeGracefully(gos);
        }

        return null;
    }

    private static class NullHostNameVerifier implements HostnameVerifier {
        public NullHostNameVerifier() {
        }

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
