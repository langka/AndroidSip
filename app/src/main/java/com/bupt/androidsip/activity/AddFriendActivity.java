package com.bupt.androidsip.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Friend;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.entity.response.SipSearchResponse;
import com.bupt.androidsip.entity.sip.SipFailure;
import com.bupt.androidsip.mananger.UserManager;
import com.bupt.androidsip.sip.SipNetListener;
import com.bupt.androidsip.sip.impl.SipManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by WHY on 2017/7/10.
 */

public class AddFriendActivity extends BaseActivity {
    @BindView(R.id.add_search_friend_input)
    EditText searchtext;
    @BindView(R.id.add_search_friend_btn)
    TextView searchCancle;
    @BindView(R.id.add_search_friend_list)
    ListView searchResult;

    AddFriendListAdapter friendListAdapter = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        List<User> friendlist = new ArrayList<>();
        friendListAdapter = new AddFriendListAdapter(this, R.layout.item_friend_add, friendlist);
        searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    ((InputMethodManager) searchtext.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            AddFriendActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    search();
                    return true;
                }
                return false;
            }
        });

        searchCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        searchResult.setAdapter(friendListAdapter);
        searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = friendListAdapter.getItem(i);
                // TODO: 2017/7/13 发送添加好友请求
               // UserManager.getInstance().addfriend(??);

                showText("好友请求已发送");
            }
        });

    }

//    private User initJiashuju(){
//        User jiashuju = new User();
//        jiashuju.id=1;
//        jiashuju.name="123";
//        return jiashuju;
//    }

    //搜索用户信息
    private void search() {
        String searchString = searchtext.getText().toString();
        SipManager.getSipManager().searchUsers(searchString, new SipNetListener<SipSearchResponse>() {
            @Override
            public void onSuccess(SipSearchResponse response) {
                friendListAdapter.clear();
                friendListAdapter.addAll(response.users);
            }

            @Override
            public void onFailure(SipFailure failure) {
                showText(failure.reason);
            }
        });
    }


    private static class AddFriendListAdapter extends ArrayAdapter<User> {
        private List<User> list;
        private int resourceId;
        private Context context;

        public AddFriendListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
            this.context = context;
            this.list = objects;
            this.resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final User user = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
                holder = new ViewHolder();
                holder.headImg = (ImageView) convertView.findViewById(R.id.item_add_friend_list_head);
                holder.maskView = convertView.findViewById(R.id.item_add_friend_list_mask);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.item_add_friend_list_name);
                holder.stateTextView = (TextView) convertView.findViewById(R.id.item_add_friend_list_state);
                holder.desc = (TextView) convertView.findViewById(R.id.item_add_friend_list_desc);
                holder.addButton = (ImageView) convertView.findViewById(R.id.item_add_friend_list_btn);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.headImg.setImageDrawable((user.head == 1) ? convertView.getResources().getDrawable(R.drawable.xusong) :
                    convertView.getResources().getDrawable(R.drawable.ic_perm_data_setting_30px));
            holder.maskView.setVisibility(user.state == 0 ? View.VISIBLE : View.INVISIBLE);
            holder.stateTextView.setText(user.state == 0 ? "[离线]" : "[在线]");
            holder.nameTextView.setText(user.name);
            holder.desc.setText(user.description);
            if (isFriend(user)) {
                holder.addButton.setVisibility(View.INVISIBLE);
            } else
                holder.addButton.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_friend_add_blue));
            return convertView;

        }

        // 2017/7/11 检测是否为好友
        boolean isFriend(User user) {
            int myID = UserManager.getInstance().getUser().id;
            for(int i = 0; i<user.friends.size();i++){
                if(user.friends.get(i).id == myID){
                    return true;
                }
            }
            return false;
        }

        static class ViewHolder {
            ImageView headImg;
            View maskView;
            TextView nameTextView;
            TextView stateTextView;
            TextView desc;
            ImageView addButton;
        }
    }

}
