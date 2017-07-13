package com.bupt.androidsip.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Chat;
import com.bupt.androidsip.entity.User;
import com.bupt.androidsip.mananger.ChatManager;
import com.bupt.androidsip.mananger.UserManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bupt.androidsip.R.id.item_profile_righttext;
import static com.bupt.androidsip.R.id.item_profile_text;

/**
 * Created by acer on 2017/7/5.
 */

public class FriendDetailInfoActivity extends BaseActivity {
    @BindView(R.id.frag_head_image)
    ImageView headimageContainer;

    @BindView(R.id.frag_friend_nickname)
    TextView nicknameContainer;

    @BindView(R.id.frag_friend_personalize)
    TextView personalizeContainer;

    @BindView(R.id.frag_sex)
    RelativeLayout sexContainer;

    @BindView(R.id.frag_birthday)
    RelativeLayout birthdayContainer;

    @BindView(R.id.frag_constellation)
    RelativeLayout constellationContainer;

    @BindView(R.id.activity_friend_delete)
    TextView deleteFriend;

    @BindView(R.id.activity_friend_chat)
    TextView chatFriend;

    @BindView(R.id.frag_friend_detail_info_goback)
    ImageView gobackContainer;

    User me = UserManager.getInstance().getUser();
    User friend;

    Intent intent;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail_info);
        ButterKnife.bind(this);
        intent = getIntent();
        int friendID = intent.getExtras().getInt("FriendId");
        friend = UserManager.getInstance().searchUser(friendID);
        if (friendID == UserManager.getInstance().getUser().id) {
            deleteFriend.setVisibility(View.GONE);
            chatFriend.setVisibility(View.GONE);
        }
        initView();

    }

    private View.OnClickListener accountOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.frag_sex:
                    showText("您无法更改好友的个人资料！");
                    break;
                case R.id.frag_birthday:
                    showText("您无法更改好友的个人资料！");
                    break;
                case R.id.frag_constellation:
                    showText("您无法更改好友的个人资料！");
                    break;
                case R.id.frag_friend_detail_info_goback:
                    //返回上一页
                    break;
            }
        }
    };

    private void initRowView(View v, int imgId, String text, String detail) {
        ImageView icon = (ImageView) v.findViewById(R.id.item_profile_icon);
        TextView textView = (TextView) v.findViewById(item_profile_text);
        TextView detailView = (TextView) v.findViewById(item_profile_righttext);
        icon.setImageDrawable(getResources().getDrawable(imgId));
        textView.setText(text);
        detailView.setText(detail);
    }

    private void initView() {
        sexContainer.setOnClickListener(accountOnClick);
        birthdayContainer.setOnClickListener(accountOnClick);
        constellationContainer.setOnClickListener(accountOnClick);
        deleteFriend.setOnClickListener((v) -> {
            // TODO: 2017/7/12  删除好友业务逻辑
            showTextOnDialog("确认删除？", view -> {
                UserManager.getInstance().deleteFriend(friend.id);
                finish();
            });

        });
        chatFriend.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChatActivity.class);
            Bundle bundle = new Bundle();
//            bundle.putParcelable("chat", );
            User user = friend;

            if (ChatManager.getChatManager().isInList(user.id))
                bundle.putParcelable("chat", ChatManager.getChatManager().getChatFromID(user.id));
            else {
                Chat chat = new Chat(user.name, user.head, 1, "", user.id);
                ChatManager.getChatManager().addChat(chat);
                bundle.putParcelable("chat", chat);
            }
            intent.putExtras(bundle);
            startActivity(intent);
        });
        gobackContainer.setOnClickListener(accountOnClick);

        TextView nameView = (TextView) nicknameContainer.findViewById(R.id.frag_friend_nickname);
        nameView.setText(friend.name);

        TextView personalizeView = (TextView) personalizeContainer.findViewById(R.id.frag_friend_personalize);
        personalizeView.setText(friend.description);

        //ImageView sexView = (ImageView) sexContainer.findViewById(R.id.frag_friend_sex);
        //sexView.setImageDrawable("drawable/profile_icon_male.png");设置性别的图片

        ImageView headimageView = (ImageView) headimageContainer.findViewById(R.id.frag_head_image);
        //sexView.setImageDrawable("drawable/profile_icon_male.png");设置头像的图片

        if (friend.sex.equals("U"))
            initRowView(sexContainer, R.drawable.ic_face_black_30dp, "性别", "未知");
        else if (friend.sex.equals("F"))
            initRowView(sexContainer, R.drawable.ic_face_black_30dp, "性别", "女性");
        else if (friend.sex.equals("M"))
            initRowView(sexContainer, R.drawable.ic_face_black_30dp, "性别", "男性");
        initRowView(birthdayContainer, R.drawable.ic_email_black_30dp, "邮箱", friend.email);
        initRowView(constellationContainer, R.drawable.ic_star_border_black_30dp, "星座", "狮子座");
        //initRowView(deleteFriend, R.drawable.ic_delete_forever_red_30dp, "删除", "");


    }

}
