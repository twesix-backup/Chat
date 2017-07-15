package cn.twesix.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static android.view.View.TEXT_ALIGNMENT_VIEW_END;

public class TalkActivity extends AppCompatActivity
{
    String to;
    String me;
    LinearLayout messages_list;
    private final Timer timer = new Timer();
    private TimerTask task = new TimerTask()
    {
        @Override
        public void run()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    fresh_message();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        Intent intent = getIntent();
        to = intent.getStringExtra("talking_to");
        setTitle("Talking to : " + to);
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        me = sp.getString("account", null);
        if(me != null)
        {
            start_talk();
        }
        messages_list =(LinearLayout) findViewById(R.id.messages);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        timer.cancel();
    }

    void start_talk()
    {
        timer.schedule(task, 1000, 1000);
    }

    void fresh_message()
    {
        HTTP.get("fetch?me=" + me + "&from=" + to, new Callback() {
            @Override
            public void onResponse(String response)
            {
                if(!(response == null) && !response.equals(""))
                {
                    for(String message : response.split("___"))
                    {
                        incoming_message(message);
                    }
                }
            }
        });
    }

    void my_message(String message)
    {
        TextView text_view = new TextView(this);
        text_view.setText(message);
        text_view.setPadding(10, 10, 100, 10);
        text_view.setTextSize(14);
        messages_list.addView(text_view);
    }

    void incoming_message(String message)
    {
        TextView text_view = new TextView(this);
        text_view.setText(message);
        text_view.setPadding(100, 10,10,10);
        text_view.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
        text_view.setTextSize(20);
        messages_list.addView(text_view);
    }

    public void sendMessage(View view)
    {
        EditText edit_text = (EditText) findViewById(R.id.my_message);
        my_message(edit_text.getText().toString());
        HTTP.get("send?from=" + me + "&to=" + to + "&message=" + edit_text.getText().toString(), new Callback()
        {
            @Override
            public void onResponse(String response)
            {
                System.out.println(response);
            }
        });
    }
}

