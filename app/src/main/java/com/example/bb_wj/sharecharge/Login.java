package com.example.bb_wj.sharecharge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by bb_wj on 16-7-9.
 */
public class Login extends Activity {
    private static final String TAG = "main";
    private static URL url;
    private static String PATH = "http://192.168.1.108:5000/login/";
    private EditText user_name;
    private EditText password;
    private Button btn_login;
    private Button btn_register;

    static {
        try {
            url = new URL(PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        btn_login = (Button) findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_name = (EditText) findViewById(R.id.editText_user_name);
                password = (EditText) findViewById(R.id.editText_password);
                String str1 = user_name.getText().toString().trim();
                String str2 = Md5(password.getText().toString().trim());
                new LoginThread(str1, str2).start();
            }
        });

        btn_register = (Button) findViewById(R.id.register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * new thread for login
     */
    public class LoginThread extends Thread {
        private Map params = new HashMap();

        LoginThread(String username, String password) {
            params.put("username", username);
            params.put("password", password);
        }

        @Override
        public void run() {
            super.run();
            String result = sendPostMessage(params, "utf-8");
            Looper.prepare();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Looper.loop();
            if (result == "Success") {
                Intent intent = new Intent(Login.this, Account.class);
                startActivity(intent);
                finish();
            }
            // Log.i(TAG, "登录状态" + result);
        }
    }

    /**
     * 通过给定的请求参数和编码格式，获取服务器返回的数据
     *
     * @param params 请求参数
     * @param encode 编码格式
     * @return 获得的字符串
     */

    public static String sendPostMessage(Map<String, String> params, String encode) {
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
     * MD5 encrypt
     */
    public static String Md5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte aB : b) {
                i = aB;
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            System.out.println("result: " + result);//32位的加密
//   System.out.println("result: " + buf.toString().substring(8,24));//16位的加密
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block e.printStackTrace();
        }
        return result;
    }
}
