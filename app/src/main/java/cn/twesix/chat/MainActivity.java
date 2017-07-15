package cn.twesix.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    ListView list_view_friends;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Offline");
        list_view_friends = (ListView) findViewById(R.id.friends) ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(Menu.NONE, 0, 0, "Logout");
        menu.add(Menu.NONE, 1, 0, "Add Friend");
        menu.add(Menu.NONE, 2, 0, "Remove Friend");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == 0)
        {
            logout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == 1)
        {
            Intent intent = new Intent(this, AddFriendActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == 2)
        {
            Intent intent = new Intent(this, RemoveFriendActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume()
    {
        super.onResume();
        // 检查登录状态
        online();
    }

    void online()
    {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);

        String account = sp.getString("account", null);

        // 没有登录， 跳转到登录页面
        if(account == null)
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            // 已经登录
            setTitle(account + " is online ~~~");

            // 显示好友列表
            String friends_list = sp.getString("friends", "");
            if(friends_list.equals(""))
            {
                return;
            }
            show_friends(friends_list);
        }
    }

    void logout()
    {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("account", null);
        editor.apply();
        editor.commit();
        setTitle("Offline");
    }

    void show_friends(String accounts)
    {
        System.out.println(accounts);

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

            list_view_friends.setAdapter(adapter);
            list_view_friends.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Friend friend = list.get(position);
                    talk(friend.account);
                }
            });
        }
    }

    void talk(String account)
    {
        Intent intent = new Intent(this, TalkActivity.class);
        intent.putExtra("talking_to", account);
        startActivity(intent);
    }
}
