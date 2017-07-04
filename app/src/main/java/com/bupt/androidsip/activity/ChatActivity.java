package com.bupt.androidsip.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.name;

/**
 * Created by vita-nove on 04/07/2017.
 */

public class ChatActivity extends BaseActivity {
    @BindView(R.id.send_to_btn)
    Button sendToBtn;

    @BindView(R.id.send_to_msg)
    EditText sendToMsg;

    @BindView(R.id.face_btn)
    Button faceBtn;

    @BindView(R.id.header)
    RelativeLayout headerContainer;

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.input_linear)
    LinearLayout inputContainer;

    @BindView(R.id.msg_listview)
    ListView msgListview;

    private MessageAdapter msgAdapter = null;
    private List<Message> messages = null;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        // TODO: 04/07/2017 徐日天要修正header被顶上去的问题 
        // controlKeyboardLayout(root, headerContainer);
        context = ChatActivity.this;
        messages = new LinkedList<Message>();
        msgAdapter = new MessageAdapter((LinkedList<Message>) messages, context);
        msgListview.setAdapter(msgAdapter);
        getHeaderDivider().setVisibility(View.GONE);
        enableLeftImage(R.drawable.ic_arrow_back_24px, e -> finish());

        msgAdapter.add(new Message("徐松", R.drawable.xusong, "我爱吃西瓜", true));

    }


    private void controlKeyboardLayout(final View root, final View targetView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        root.getWindowVisibleDisplayFrame(rect);
                        int rootInvisibleHeight = root.getRootView()
                                .getHeight() - rect.bottom;
                        Log.i("tag", "最外层的高度" + root.getRootView().getHeight());
                        Log.i("tag", "rect.boom" + rect.bottom);
                        // 若rootInvisibleHeight高度大于200，则说明当前视图上移了，说明软键盘弹出了
                        //给它上移90
                        if (rootInvisibleHeight > 200) {
                            //         targetView.scrollBy(0, rootInvisibleHeight);
                            targetView.offsetTopAndBottom(rootInvisibleHeight);
                        } else {
                            // 软键盘没有弹出来的时候
                            targetView.scrollBy(500, 0);
                        }
                    }
                });
    }


    static class MessageAdapter extends BaseAdapter {
        private LinkedList<Message> messages;
        private Context context;

        public void add(Message message) {

            messages.add(message);
            notifyDataSetChanged();
        }

        public MessageAdapter() {
        }

        public MessageAdapter(LinkedList<Message> messages, Context context) {
            this.messages = messages;
            this.context = context;
        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Message getItem(int position) {
            return messages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_msg_left,
                        parent, false);
                holder = new ViewHolder();
                holder.avatar = (CircleImageView) convertView.findViewById(R.id.item_avatar);
                holder.name = (TextView) convertView.findViewById(R.id.item_name);
                holder.msg = (TextView) convertView.findViewById(R.id.item_msg);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.avatar.setImageResource(R.drawable.xusong);
            holder.msg.setText("西瓜爱吃我");
            holder.name.setText("徐日天");
            return convertView;
        }

        private class ViewHolder {
            CircleImageView avatar;
            TextView name;
            TextView msg;
        }

    }
}
