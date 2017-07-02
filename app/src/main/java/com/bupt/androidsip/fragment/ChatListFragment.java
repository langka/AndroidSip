package com.bupt.androidsip.fragment;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Friend;
import com.bupt.androidsip.mananger.UserManager;

import org.w3c.dom.Text;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by WHY on 2017/7/1.
 */

public class FriendFragment extends BaseFragment {
    @BindView(R.id.frag_friend_head)
    ImageView headImage;
    @BindView(R.id.frag_friend_append)
    ImageView append;
    @BindView(R.id.frag_friend_list)
    ListView listView;

    private List<Friend> friendList = new ArrayList<>();




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_friend, null);
        ButterKnife.bind(this, v);

        initdata();

        // TODO: 2017/7/1 动态展示用户头像，获取用户URL
        headImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_box_18px));

        // TODO: 2017/7/1 list 动态展示
        FriendAdapter friendAdapter = new FriendAdapter(getActivity(),R.layout.item_frag_friend,friendList);

        listView.setAdapter(friendAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            // TODO: 2017/7/1 触发点击

        });
        return v;

    }

    void initdata(){
        friendList = UserManager.getInstance().getUser().friendList;
    }

    static class FriendAdapter extends ArrayAdapter<Friend> {

        private int resourceId;

        public FriendAdapter(Context context, int textViewResourceId, List<Friend> objects){
            super(context, textViewResourceId, objects);
            this.resourceId = textViewResourceId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Friend friend = getItem(position);
            ViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(resourceId,null);
                holder = new ViewHolder();
                holder.friendHead = (ImageView) convertView.findViewById(R.id.item_friend_head_img);
                holder.friendName = (TextView) convertView.findViewById(R.id.item_friend_name);
                holder.friendSignature = (TextView) convertView.findViewById(R.id.item_friend_signature);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.friendHead.setImageURI(null);
            holder.friendName.setText(friend.getName());
            holder.friendSignature.setText(friend.getSignature());

            return convertView;

        }

        static class ViewHolder {
            ImageView friendHead;
            TextView friendName;
            TextView friendSignature;
        }
    }

}
