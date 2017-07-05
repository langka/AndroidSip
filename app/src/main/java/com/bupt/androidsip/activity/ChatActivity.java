package com.bupt.androidsip.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.Message;
import com.bupt.androidsip.view.DropdownListView;
import com.bupt.androidsip.view.MyEditText;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vita-nove on 04/07/2017.
 */

public class ChatActivity extends BaseActivity implements DropdownListView.OnRefreshListenerHeader {

    @BindView(R.id.send_to_btn)
    Button sendToBtn;

    @BindView(R.id.send_to_msg)
    MyEditText sendToMsg;

    @BindView(R.id.face_btn)
    Button faceBtn;

    @BindView(R.id.header)
    RelativeLayout headerContainer;

    @BindView(R.id.root)
    LinearLayout root;

    @BindView(R.id.input_linear)
    LinearLayout inputContainer;

    @BindView(R.id.msg_listview)
    DropdownListView msgListView;

    @BindView(R.id.chat_face_container)
    LinearLayout chatFaceContainer;

    @BindView(R.id.face_viewpager)
    ViewPager faceViewPager;

    @BindView(R.id.face_dots_container)
    LinearLayout dotsContainer;

    private Context context;
    private List<String> staticFacesList;
    private List<View> views = new ArrayList<View>();
    private int columns = 6;
    private int rows = 4;
    private MessageAdapter msgAdapter;
    private LinkedList<Message> messages = new LinkedList<Message>();
    private SimpleDateFormat simpleDateFormat;
    private String reply;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        context = ChatActivity.this;
        msgAdapter = new MessageAdapter(this, messages);
        msgListView.setAdapter(msgAdapter);
        faceViewPager.setOnPageChangeListener(new PageChange());

        getHeaderDivider().setVisibility(View.GONE);
        enableLeftImage(R.drawable.ic_arrow_back_24px, e -> finish());

        msgListView.setOnRefreshListenerHead(this);
        msgListView.setOnTouchListener(getOnTouchListener());
        sendToBtn.setOnClickListener(sendBtnListener);
        faceBtn.setOnClickListener(faceBtnListener);
        sendToMsg.setOnClickListener(sendToMsgListener);
        initStaticFaces();
        initViewPager();

        messages.add(getChatMsgFrom("我爱吃西瓜吃西瓜吃西瓜吃西瓜吃西瓜吃西瓜吃西瓜吃西瓜吃西瓜"));
        messages.add(getChatMsgTo("测试测试#[face/png/f_static_018.png]#"));

    }

    private Message getChatMsgFrom(String message) {
        Message msg = new Message();
        msg.content = message;
        msg.fromOrTo = 0;
        msg.time = simpleDateFormat.format(new Date());
        return msg;
    }

    private Message getChatMsgTo(String message) {
        Message msg = new Message();
        msg.content = message;
        msg.fromOrTo = 1;
        msg.time = simpleDateFormat.format(new Date());
        return msg;
    }

    EditText.OnClickListener sendToMsgListener = new EditText.OnClickListener() {
        public void onClick(View v) {
            if (chatFaceContainer.getVisibility() == View.VISIBLE)
                chatFaceContainer.setVisibility(View.GONE);
        }
    };


    Button.OnClickListener sendBtnListener = new Button.OnClickListener() {
        public void onClick(View v) {
            reply = sendToMsg.getText().toString();
            if (!TextUtils.isEmpty(reply)) {
                messages.add(getChatMsgTo(reply));
                msgAdapter.setList(messages);
                msgAdapter.notifyDataSetChanged();
                msgListView.setSelection(messages.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messages.add(getChatMsgFrom(reply));
                        msgAdapter.setList(messages);
                        msgAdapter.notifyDataSetChanged();
                        msgListView.setSelection(messages.size() - 1);
                    }
                }, 1000);
                sendToMsg.setText("");
            }
            // TODO: 04/07/2017 完成发送消息的操作
        }
    };

    Button.OnClickListener faceBtnListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //隐藏软键盘先
            hideSoftInputView();
            if (chatFaceContainer.getVisibility() == View.GONE) {
                chatFaceContainer.setVisibility(View.VISIBLE);
            } else {
                chatFaceContainer.setVisibility(View.GONE);
            }
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
                    if (chatFaceContainer.getVisibility() == View.VISIBLE) {
                        chatFaceContainer.setVisibility(View.GONE);
                    }
                }
                return false;
            }
        };
    }

    //表情翻页时改变dot的颜色
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < dotsContainer.getChildCount(); i++)
                dotsContainer.getChildAt(i).setSelected(false);
            dotsContainer.getChildAt(arg0).setSelected(true);
        }

    }

    //隐藏软键盘
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //初始化静态表情
    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            dotsContainer.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        faceViewPager.setAdapter(mVpAdapter);
        dotsContainer.getChildAt(0).setSelected(true);
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView viewById = (ImageView) layout.findViewById(R.id.face_dot);
        viewById.setId(position);
        return viewById;
    }

    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.face_gridview, null);//表情布局
        GridView gridview = (GridView) layout.findViewById(R.id.chart_face_gv);

        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        //删除的图标
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, this);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return gridview;
    }

    //删除图标事件
    private void delete() {
        if (sendToMsg.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(sendToMsg.getText());
            int iCursorStart = Selection.getSelectionStart(sendToMsg.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/png/f_static_000.png]#";
                        ((Editable) sendToMsg.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) sendToMsg.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    ((Editable) sendToMsg.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((sendToMsg.getText()));
        int iCursorEnd = Selection.getSelectionEnd((sendToMsg.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) sendToMsg.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((sendToMsg.getText()));
        ((Editable) sendToMsg.getText()).insert(iCursor, text);
    }

    //判断删除的是否是一个png图
    private boolean isDeletePng(int cursor) {
        String st = "#[face/png/f_static_000.png]#";
        String content = sendToMsg.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            // TODO: 05/07/2017 修正在edittext中png不能正常显示的问题
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(ChatActivity.this, BitmapFactory
                            .decodeStream(getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }


    public class FaceGVAdapter extends BaseAdapter {
        private static final String TAG = "FaceGVAdapter";
        private List<String> list;
        private Context context;

        public FaceGVAdapter(List<String> list, Context context) {
            super();
            this.list = list;
            this.context = context;
        }

        public void clear() {
            this.context = null;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.face_image, null);
                holder.iv = (ImageView) convertView.findViewById(R.id.face_img);
                holder.tv = (TextView) convertView.findViewById(R.id.face_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                Bitmap mBitmap = BitmapFactory.decodeStream(context.getAssets().open("face/png/" + list.get(position)));
                holder.iv.setImageBitmap(mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            holder.tv.setText("face/png/" + list.get(position));

            return convertView;
        }

        class ViewHolder {
            ImageView iv;
            TextView tv;
        }
    }

    public class FaceVPAdapter extends PagerAdapter {

        // 界面列表
        private List<View> views;

        public FaceVPAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }

            return 0;
        }

        // 初始化arg1位置的界面
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(views.get(arg1), 0);

            return views.get(arg1);
        }

        // 判断是否由对象生成界
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return (arg0 == arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
        }
    }


    @SuppressLint("NewApi")
    public class MessageAdapter extends BaseAdapter {
        private Context context;
        private List<Message> list;
        /**
         * 弹出的更多选择框
         */
        private PopupWindow popupWindow;

        /**
         * 复制，删除
         */
        private TextView copy, delete;

        private LayoutInflater inflater;
        /**
         * 执行动画的时间
         */
        protected long animationTime = 150;

        public MessageAdapter(Context context, List<Message> list) {
            super();
            this.context = context;
            this.list = list;
            inflater = LayoutInflater.from(context);
            initPopWindow();
        }

        public void setList(List<Message> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_message, null);
                holder.fromContainer = (ViewGroup) convertView.findViewById(R.id.chat_left);
                holder.toContainer = (ViewGroup) convertView.findViewById(R.id.chat_right);
                holder.fromContent = (TextView) convertView.findViewById(R.id.msg_left);
                holder.toContent = (TextView) convertView.findViewById(R.id.msg_right);
                holder.time = (TextView) convertView.findViewById(R.id.msg_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (list.get(position).fromOrTo == 0) {
                // 收到消息 from显示
                holder.toContainer.setVisibility(View.GONE);
                holder.fromContainer.setVisibility(View.VISIBLE);

                // 对内容做处理
                SpannableStringBuilder sb = handler(holder.fromContent,
                        list.get(position).content);
                holder.fromContent.setText(sb);
                holder.time.setText(list.get(position).time);
            } else {
                // 发送消息 to显示
                holder.toContainer.setVisibility(View.VISIBLE);
                holder.fromContainer.setVisibility(View.GONE);

                // 对内容做处理
                SpannableStringBuilder sb = handler(holder.toContent,
                        list.get(position).content);
                holder.toContent.setText(sb);
                holder.time.setText(list.get(position).time);
            }
            holder.fromContent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });
            holder.toContent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });

            // 设置+按钮点击效果
            holder.fromContent.setOnLongClickListener(new popAction(convertView,
                    position, list.get(position).fromOrTo));
            holder.toContent.setOnLongClickListener(new popAction(convertView,
                    position, list.get(position).fromOrTo));
            return convertView;
        }

        private SpannableStringBuilder handler(final TextView gifTextView, String content) {
            SpannableStringBuilder sb = new SpannableStringBuilder(content);
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(content);
            while (m.find()) {
                String tempText = m.group();
                try {
                    String num = tempText.substring("#[face/png/f_static_".length(), tempText.length() - ".png]#".length());
                    String gif = "face/gif/f" + num + ".gif";
                    /**
                     * 如果open这里不抛异常说明存在gif，则显示对应的gif
                     * 否则说明gif找不到，则显示png
                     * */
                    InputStream is = context.getAssets().open(gif);
                    sb.setSpan(new com.bupt.androidsip.util.gif.AnimatedImageSpan(new com.bupt.androidsip.util.gif.AnimatedGifDrawable(is, new com.bupt.androidsip.util.gif.AnimatedGifDrawable.UpdateListener() {
                                @Override
                                public void update() {
                                    gifTextView.postInvalidate();
                                }
                            })), m.start(), m.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    is.close();
                } catch (Exception e) {
                    String png = tempText.substring("#[".length(), tempText.length() - "]#".length());
                    try {
                        sb.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getAssets().open(png))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
            return sb;
        }

        class ViewHolder {
            ImageView fromIcon, toIcon;
            TextView fromContent, toContent, time;
            ViewGroup fromContainer, toContainer;
        }

        /**
         * 屏蔽listitem的所有事件
         */
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        /**
         * 初始化弹出的pop
         */
        private void initPopWindow() {
            View popView = inflater.inflate(R.layout.item_copy_delete_menu,
                    null);
            copy = (TextView) popView.findViewById(R.id.chat_copy_menu);
            delete = (TextView) popView.findViewById(R.id.chat_delete_menu);
            popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0));
            // 设置popwindow出现和消失动画
            // popupWindow.setAnimationStyle(R.style.PopMenuAnimation);
        }

        /**
         * 显示popWindow
         */
        public void showPop(View parent, int x, int y, final View view,
                            final int position, final int fromOrTo) {
            // 设置popwindow显示位置
            popupWindow.showAtLocation(parent, 0, x, y);
            // 获取popwindow焦点
            popupWindow.setFocusable(true);
            // 设置popwindow如果点击外面区域，便关闭。
            popupWindow.setOutsideTouchable(true);
            // 为按钮绑定事件
            // 复制
            copy.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    // 获取剪贴板管理服务
                    ClipboardManager cm = (ClipboardManager) context
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本数据复制到剪贴板
                    cm.setText(list.get(position).content);
                }
            });
            // 删除
            delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    if (fromOrTo == 0) {
                        // from
                        leftRemoveAnimation(view, position);
                    } else if (fromOrTo == 1) {
                        // to
                        rightRemoveAnimation(view, position);
                    }

                    // list.remove(position);
                    // notifyDataSetChanged();
                }
            });
            popupWindow.update();
            if (popupWindow.isShowing()) {

            }
        }

        /**
         * 每个ITEM中more按钮对应的点击动作
         */
        public class popAction implements View.OnLongClickListener {
            int position;
            View view;
            int fromOrTo;

            public popAction(View view, int position, int fromOrTo) {
                this.position = position;
                this.view = view;
                this.fromOrTo = fromOrTo;
            }

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                int[] arrayOfInt = new int[2];
                // 获取点击按钮的坐标
                v.getLocationOnScreen(arrayOfInt);
                int x = arrayOfInt[0];
                int y = arrayOfInt[1];
                // System.out.println("x: " + x + " y:" + y + " w: " +
                // v.getMeasuredWidth() + " h: " + v.getMeasuredHeight() );
                showPop(v, x, y, view, position, fromOrTo);
                return true;
            }
        }

        /**
         * item删除动画
         */
        private void rightRemoveAnimation(final View view, final int position) {
            final Animation animation = (Animation) AnimationUtils.loadAnimation(
                    context, R.anim.msg_right_remove_anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    view.setAlpha(0);
                    performDismiss(view, position);
                    animation.cancel();
                }
            });

            view.startAnimation(animation);
        }

        /**
         * item删除动画
         */
        private void leftRemoveAnimation(final View view, final int position) {
            final Animation animation = (Animation) AnimationUtils.loadAnimation(context, R.anim.msg_left_remove_anim);
            animation.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    view.setAlpha(0);
                    performDismiss(view, position);
                    animation.cancel();
                }
            });

            view.startAnimation(animation);
        }

        /**
         * 在此方法中执行item删除之后，其他的item向上或者向下滚动的动画，并且将position回调到方法onDismiss()中
         *
         * @param dismissView
         * @param dismissPosition
         */
        private void performDismiss(final View dismissView,
                                    final int dismissPosition) {
            final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();// 获取item的布局参数
            final int originalHeight = dismissView.getHeight();// item的高度

            ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0)
                    .setDuration(animationTime);
            animator.start();

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    list.remove(dismissPosition);
                    notifyDataSetChanged();
                    // 这段代码很重要，因为我们并没有将item从ListView中移除，而是将item的高度设置为0
                    // 所以我们在动画执行完毕之后将item设置回来
//				ViewHelper.setAlpha(dismissView, 1f);
//				ViewHelper.setTranslationX(dismissView, 0);
                    ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
                    lp.height = originalHeight;
                    dismissView.setLayoutParams(lp);
                }
            });

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    // 这段代码的效果是ListView删除某item之后，其他的item向上滑动的效果
                    lp.height = (Integer) valueAnimator.getAnimatedValue();
                    dismissView.setLayoutParams(lp);
                }
            });

        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    msgAdapter.setList(messages);
                    msgAdapter.notifyDataSetChanged();
                    msgListView.onRefreshCompleteHeader();
                    break;
            }
        }
    };

    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    android.os.Message msg = handler.obtainMessage(0);
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


}