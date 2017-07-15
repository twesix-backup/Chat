package cn.twesix.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class FriendAdapter extends ArrayAdapter<Friend>
{
    private int resource_id;
    FriendAdapter(Context context, int textViewResourceId, List<Friend> list)
    {
        super(context, textViewResourceId, list);
        resource_id = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Friend friend = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resource_id, null);
        TextView account = (TextView) view.findViewById(R.id.friend_account);
        account.setText(friend.account);
        return view;
    }
}

class Friend
{
    String account;

    Friend(String account)
    {
        this.account = account;
    }
}