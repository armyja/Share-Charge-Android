package com.example.bb_wj.sharecharge;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by bb_wj on 16-7-10.
 */
public class HttpUtils {

    public HttpUtils() {
    }

    private static final String TAG = "main";
    private static String sessionId = "";

    /**
     * GET方法，通过 Uri 地址，获取服务器的 JSON 数据。
     *
     * @param url_path JSON 数据请求的 Uri 地址
     * @return JSON 数据
     */
    public static String getJsonContent(String url_path) {
        try {
            URL url = new URL(url_path);
            // 使用 HttpUrlConnection 获取服务器数据
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty("Cookie", sessionId);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            //Get Session ID
            String key;

            for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    sessionId = connection.getHeaderField(key);
                    sessionId = sessionId.substring(0, sessionId.indexOf(";"));
                }
            }

            int code = connection.getResponseCode();
            if (code == 200) {
                Log.i(TAG, "获取服务器 JSON 数据流成功");
                return changeJsonString(connection.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }


    /**
     * POST 方法，通过给定的请求参数和编码格式，获取服务器返回的数据
     *
     * @param params 请求参数
     * @param encode 编码格式
     * @return 获得的字符串
     */

    public static String sendPostMessage(URL url, Map<String, String> params, String encode) {
        StringBuilder buffer = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    // 请求的参数之间用 & 分割
                    buffer.append(entry.getKey())
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            buffer.deleteCharAt(buffer.length() - 1);
            // Log.i(TAG, "POST请求参数" + buffer.toString());
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Cookie", sessionId);

                // 设置连接超时时长
                urlConnection.setConnectTimeout(3000);

                // 设置允许输入输出
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                byte[] mydata = buffer.toString().getBytes();

                // 设置请求报文头，设定请求数据类型
                urlConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                // 设置请求数据长度
                urlConnection.setRequestProperty("Content-Length",
                        String.valueOf(mydata.length));

                // 设置POST方式请求数据
                urlConnection.setRequestMethod("POST");
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(mydata);

                //Get Session ID
                String key;
                for (int i = 1; (key = urlConnection.getHeaderFieldKey(i)) != null; i++) {
                    if (key.equalsIgnoreCase("set-cookie")) {
                        sessionId = urlConnection.getHeaderField(key);
                        sessionId = sessionId.substring(0, sessionId.indexOf(";"));
                    }
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    return changeInputstream(urlConnection.getInputStream(), encode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 把服务端返回的输入流转换为字符串格式
     *
     * @param inputStream 服务器返回的输入流
     * @param encode      编码格式
     * @return 解析后的字符串
     */

    private static String changeInputstream(InputStream inputStream, String encode) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
                result = new String(outputStream.toByteArray(), encode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 把服务器获取的 JSON 数据流转换成 JSON 字符串数据
     *
     * @param inputStream JSON 数据流
     * @return JSON 字符串数据
     */

    private static String changeJsonString(InputStream inputStream) {
        String jsonString = "";
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len;
            byte[] data = new byte[1024];
            while ((len = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
            jsonString = new String(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

}
