package cn.twesix.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import cn.twesix.chat.HTTP;
import cn.twesix.chat.Callback;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
    }

    public void login(View view)
    {
        EditText view_login_account = (EditText) findViewById(R.id.login_account);
        EditText view_login_password = (EditText) findViewById(R.id.login_password);

        String account = view_login_account.getText().toString();
        String password = view_login_password.getText().toString();

        send_login_request(account, password);
    }

    void send_login_request(final String account,String password)
    {
        final String request_url = "login?account=" + account + "&password=" + password;

        System.out.println("在子线程执行网络请求");
        HTTP.get(request_url, new Callback()
        {
            @Override
            public void onResponse(String response)
            {
                set_user_info(response, account);
            }
        });
    }

    void set_user_info(String response, String account)
    {
        JSONObject res = JSON.parseObject(response);
        System.out.println(res);
        if(res.get("status").equals("ok"))
        {
            SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("account", account);
            editor.apply();
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            System.out.println("done login !");
        }
        else
        {
            login_failed(res.get("status").toString());
        }

    }

    void login_failed(String message)
    {
        new AlertDialog.Builder(this)
                .setTitle("Login failed !")
                .setMessage(message)
                .setPositiveButton("Get it !", null)
                .show();
    }
}
