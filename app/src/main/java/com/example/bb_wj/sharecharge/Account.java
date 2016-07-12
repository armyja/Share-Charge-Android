package com.example.bb_wj.sharecharge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import static com.example.bb_wj.sharecharge.HttpUtils.getJsonContent;

/**
 * Created by bb_wj on 16-7-9.
 */
public class Account extends Activity {
    private TextView tvInfo;
    private static MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        tvInfo = (TextView) findViewById(R.id.tvInformation);
        handler = new MyHandler();
        new GetAccountThread().start();
    }

    private class GetAccountThread extends Thread {
        GetAccountThread() {
        }

        @Override
        public void run() {
            super.run();
            try {
                String url_path = getString(R.string.host) + "/user";
                JSONObject result = new JSONObject(getJsonContent(url_path));
                // 创建 message
                if (result.getString("status").equals("success")) {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = result.toString();
                    // 向工作线程中发送消息
                    handler.sendMessage(msg);
//                    dialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 定义个Handler类
    public class MyHandler extends Handler {

        public MyHandler() {
        }

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 控制UI控件输出消息内容
            tvInfo.setText(msg.obj.toString());
        }
    }
}
