package com.bupt.androidsip.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bupt.androidsip.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        // TODO: 04/07/2017 徐日天要修正header被顶上去的问题 
        // controlKeyboardLayout(root, headerContainer);
        headerContainer.scrollBy(100, 200);
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
}
