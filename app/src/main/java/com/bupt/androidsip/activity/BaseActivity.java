package com.bupt.androidsip.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.androidsip.R;

import static com.bupt.androidsip.R.id.header_righttext;

/**
 * Created by lenovo on 2017/4/19.
 */

public class BaseActivity extends FragmentActivity {
    public void showText(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    public void setTitle(String text){
        TextView tv= (TextView) findViewById(R.id.header_title);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);
    }
    public void enableLeftImage(int resId, View.OnClickListener listener){
        ImageView iv = (ImageView) findViewById(R.id.header_leftimage);
        iv.setVisibility(View.VISIBLE);
        iv.setImageDrawable(getResources().getDrawable(resId));
        iv.setOnClickListener(listener);
    }
    public void enableRightText(String text, View.OnClickListener listener){
        TextView textView = (TextView) findViewById(header_righttext);
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
        textView.setOnClickListener(listener);
    }
}
