package com.example.bb_wj.sharecharge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.example.bb_wj.sharecharge.HttpUtils.sendPostMessage;
import static com.example.bb_wj.sharecharge.Md5Utils.Md5;


/**
 * Created by bb_wj on 16-7-9.
 */
public class Login extends Activity {
    private static URL url;
    private EditText user_name;
    private EditText password;
    private Button btn_login;
    private Button btn_register;
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
        private Map<String, String> params = new HashMap<>();

        LoginThread(String username, String password) {
            params.put("username", username);
            params.put("password", password);
        }

        @Override
        public void run() {
            super.run();
            try {
                // get response
                String PATH = getString(R.string.host) + "/login";
                url = new URL(PATH);
                JSONObject result = new JSONObject(sendPostMessage(url, params, "utf-8"));
                Looper.prepare();
                // login successfully
                if (result.getString("status").equals("success")) {
                    Intent intent = new Intent(Login.this, Account.class);
                    startActivity(intent);
                    finish();
                } else {
                    // show error
                    Toast.makeText(getApplicationContext(), result.getString("status"), Toast.LENGTH_SHORT).show();
                }
                Looper.loop();
            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }


}
