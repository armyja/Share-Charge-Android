package com.example.bb_wj.sharecharge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
public class Register extends Activity {
    private Button btn;
    private EditText et_reg_name;
    private EditText et_reg_email;
    private EditText et_reg_pwd;
    private EditText et_reg_pwd2;
    private EditText et_reg_tel;
    private EditText et_reg_stu_id;
    private RadioGroup radioGroup_reg_sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn = (Button) findViewById(R.id.btn_reg_register);
        et_reg_name = (EditText) findViewById(R.id.et_reg_name);
        et_reg_email = (EditText) findViewById(R.id.et_reg_email);
        et_reg_pwd = (EditText) findViewById(R.id.et_reg_pwd);
        et_reg_pwd2 = (EditText) findViewById(R.id.et_reg_pwd2);
        et_reg_tel = (EditText) findViewById(R.id.et_reg_tel);
        et_reg_stu_id = (EditText) findViewById(R.id.et_reg_stu_id);
        radioGroup_reg_sex = (RadioGroup) findViewById(R.id.radioGroup_reg_sex);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 16-7-12 表单验证
                if (!et_reg_pwd.getText().toString().equals(et_reg_pwd2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "passwords are different", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("user_name", et_reg_name.getText().toString());
                params.put("email", et_reg_email.getText().toString());
                params.put("password", Md5(et_reg_pwd.getText().toString()));
                params.put("phone", et_reg_tel.getText().toString());
                params.put("school_card_id", et_reg_stu_id.getText().toString());

                if (radioGroup_reg_sex.getCheckedRadioButtonId() == R.id.radioMale) {
                    params.put("sex", "MALE");
                } else {
                    params.put("sex", "FEMALE");
                }
                new RegisterThread(params).start();
            }
        });
    }

    /**
     * new thread for login
     */
    public class RegisterThread extends Thread {
        private Map<String, String> params = new HashMap<>();

        RegisterThread(Map<String, String> _params) {
            params = _params;
        }

        @Override
        public void run() {
            super.run();
            try {
                // get response
                String PATH = getString(R.string.host) + "/register";
                URL url = new URL(PATH);
                JSONObject result = new JSONObject(sendPostMessage(url, params, "utf-8"));
                Looper.prepare();
                // login successfully
                if (result.getString("status").equals("success")) {
                    Intent intent = new Intent(Register.this, MainActivity.class);
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
