package cn.twesix.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity
{
    ListView list_view_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        setTitle("Add Friend");

        list_view_friends =(ListView) findViewById(R.id.friends_list_view);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        get_user_list();
    }

    void get_user_list()
    {
        HTTP.get("get_user_list", new Callback()
        {
            @Override
            public void onResponse(String response)
            {
                show_users(response);
                JSONObject res = JSON.parseObject(response);
                show_users(res.get("message").toString());
            }
        });
    }

    void show_users(String users)
    {
        System.out.println(users);
        final ArrayList<Friend> list = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String my_account = sp.getString("account", null);
        for(String account : users.split("___"))
        {
            System.out.println(account);
            if(! already_a_friend(account) && ! account.equals(my_account))
            {
                Friend friend = new Friend(account);
                list.add(friend);
            }
        }

        FriendAdapter adapter = new FriendAdapter(AddFriendActivity.this, R.layout.list_view, list);
        list_view_friends.setAdapter(adapter);
        list_view_friends.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Friend friend = list.get(position);
                confirm("Are you sure to add this person as your friend ?", friend.account);
            }
        });
    }

    void confirm(String message, final String account)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Notice");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                add_friend(account);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    void add_friend(String account)
    {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String friends = sp.getString("friends", null);
        if(friends == null || friends.equals(""))
        {
            friends = account;
        }
        else
        {
            friends += "___" + account;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("friends", friends);
        editor.apply();
        editor.commit();
        get_user_list();
        Toast.makeText(this, "Done add friend : " + account, Toast.LENGTH_LONG).show();
    }

    boolean already_a_friend(String account) {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String friends = sp.getString("friends", null);
        return friends != null && friends.contains(account);
    }
}