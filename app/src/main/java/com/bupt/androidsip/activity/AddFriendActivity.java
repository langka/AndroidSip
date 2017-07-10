package com.bupt.androidsip.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bupt.androidsip.R;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 当按了搜索之后关闭软键盘
                    ((InputMethodManager) searchtext.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            AddFriendActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    return true;
                }
                return false;
            }
        });
    }
}
