package cn.twesix.chat;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RemoveFriendActivity extends AppCompatActivity
{
    ListView list_view_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_friend);
        setTitle("Remove Friend");

        show_friends();
    }

    void show_friends()
    {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);

        String accounts = sp.getString("friends", null);

        if(accounts != null)
        {
            final ArrayList<Friend> list = new ArrayList<>();
            for(String account : accounts.split("___"))
            {
                System.out.println(account);
                if(account.equals(""))
                {
                    continue;
                }
                Friend friend = new Friend(account);
                list.add(friend);
            }

            FriendAdapter adapter = new FriendAdapter(this, R.layout.list_view, list);

            list_view_friends = (ListView) findViewById(R.id.my_friends) ;
            list_view_friends.setAdapter(adapter);
            list_view_friends.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Friend friend = list.get(position);
                    confirm("Are you sure to remove friend ?", friend.account);
                }
            });
        }
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
                remove_friend(account);
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

    void remove_friend(String account)
    {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String friends = sp.getString("friends", null);
        if(friends == null)
        {
            return;
        }
        else
        {
            friends = friends.replaceAll("___"+account, "");
            friends = friends.replaceAll(account+"___", "");
            friends = friends.replaceAll(account, "");
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("friends", friends);
        editor.apply();
        editor.commit();
        show_friends();
        Toast.makeText(this, "Done remove friend : " + account, Toast.LENGTH_LONG).show();
    }
}


