package com.bupt.androidsip.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
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
        context = ChatActivity.this;
        messages = new LinkedList<Message>();
        CustomAdapter customAdapter = new CustomAdapter((LinkedList<Message>) messages, context);
        msgAdapter = new MessageAdapter((LinkedList<Message>) messages, context);
        msgListview.setAdapter(customAdapter);
        getHeaderDivider().setVisibility(View.GONE);
        enableLeftImage(R.drawable.ic_arrow_back_24px, e -> finish());
        msgListview.setOnTouchListener(getOnTouchListener());
        //   msgListview.setLayoutAnimation(getListAnim());
        sendToBtn.setOnClickListener(sendBtnListener);
    }


    Button.OnClickListener sendBtnListener = new Button.OnClickListener() {
        public void onClick(View v) {
            String msg = sendToMsg.getText().toString();
            sendToMsg.setText("");
            msgAdapter.add(new Message("我", R.drawable.xusong, msg, true));
            // TODO: 04/07/2017 完成发送消息的操作
        }
    };


    //触摸ListView之后隐藏键盘
    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Activity activity = (Activity) context;
                if (activity != null) {
                    InputMethodManager imm = (InputMethodManager) activity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive() && activity.getCurrentFocus() != null) {
                        imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                                .getWindowToken(), 0);
                    }
                }
                return false;
            }
        };
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_msg,
                        parent, false);
                holder = new ViewHolder();

                holder.avatar_right = (CircleImageView) convertView.findViewById(R.id.item_avatar_right);
                holder.avatar_left = (CircleImageView) convertView.findViewById(R.id.item_avatar_left);
                holder.name_left = (TextView) convertView.findViewById(R.id.item_name_left);
                holder.name_right = (TextView) convertView.findViewById(R.id.item_name_right);
                holder.msg_left = (TextView) convertView.findViewById(R.id.item_msg_left);
                holder.msg_right = (TextView) convertView.findViewById(R.id.item_msg_right);
                holder.leftContainer = convertView.findViewById(R.id.msg_left);
                holder.rightContainer = convertView.findViewById(R.id.msg_right);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            Message msg = messages.get(position);
            if (msg.isFromMe()) {
                holder.leftContainer.setVisibility(View.INVISIBLE);
                holder.rightContainer.setVisibility(View.VISIBLE);
                holder.msg_right.setText(msg.getMessage());
                holder.name_right.setText(msg.getName());
                holder.avatar_right.setImageResource(msg.getHeadImageURL());
                //设置tag防止图片闪烁
                holder.avatar_right.setTag("imgurl");
                // TODO: 04/07/2017 加载数据
            } else {
                holder.leftContainer.setVisibility(View.VISIBLE);
                holder.rightContainer.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        private class ViewHolder {
            CircleImageView avatar_left;
            CircleImageView avatar_right;
            TextView name_left;
            TextView name_right;
            TextView msg_left;
            TextView msg_right;
            View leftContainer;
            View rightContainer;

        }

        private AnimationSet getAnim() {
            AnimationSet set = new AnimationSet(true);
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(300);
            set.addAnimation(animation);
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            animation.setDuration(500);
            set.addAnimation(animation);
            return set;
        }

    }

    private LayoutAnimationController getListAnim() {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.5f);
        return controller;
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
        private Context context;

        // The items to display in your RecyclerView
        //     private ArrayList<String> items;
        private LinkedList<Message> messages;

        // Allows to remember the last item shown on screen
        private int lastPosition = -1;

        public class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView avatar_left;
            CircleImageView avatar_right;
            TextView name_left;
            TextView name_right;
            TextView msg_left;
            TextView msg_right;
            View leftContainer;
            View rightContainer;

            public ViewHolder(View itemView) {
                super(itemView);
                avatar_right = (CircleImageView) itemView.findViewById(R.id.item_avatar_right);
                avatar_left = (CircleImageView) itemView.findViewById(R.id.item_avatar_left);
                name_left = (TextView) itemView.findViewById(R.id.item_name_left);
                name_right = (TextView) itemView.findViewById(R.id.item_name_right);
                msg_left = (TextView) itemView.findViewById(R.id.item_msg_left);
                msg_right = (TextView) itemView.findViewById(R.id.item_msg_right);
                leftContainer = itemView.findViewById(R.id.msg_left);
                rightContainer = itemView.findViewById(R.id.msg_right);
            }
        }

        public CustomAdapter(LinkedList<Message> messages, Context context) {
            this.messages = messages;
            this.context = context;
        }

        @Override
        public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

//        public Message getItem(int position) {
//            return messages.get(position);
//        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message msg = messages.get(position);
            if (msg.isFromMe()) {
                holder.leftContainer.setVisibility(View.INVISIBLE);
                holder.rightContainer.setVisibility(View.VISIBLE);
                holder.msg_right.setText(msg.getMessage());
                holder.name_right.setText(msg.getName());
                holder.avatar_right.setImageResource(msg.getHeadImageURL());
                //设置tag防止图片闪烁
                holder.avatar_right.setTag("imgurl");
                // TODO: 04/07/2017 加载数据
            } else {
                holder.leftContainer.setVisibility(View.VISIBLE);
                holder.rightContainer.setVisibility(View.INVISIBLE);
            }

            // Here you apply the animation when the view is bound
            setAnimation(holder.itemView, position);
        }

        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }

}
