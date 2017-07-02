package com.bupt.androidsip.activity;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.androidsip.R;

import java.util.List;

import static com.bupt.androidsip.R.id.header_righttext;

/**
 * Created by lenovo on 2017/4/19.
 */

public class BaseActivity extends FragmentActivity {
    public void showText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void setTitle(String text) {
        TextView tv = (TextView) findViewById(R.id.header_title);
        tv.setVisibility(View.VISIBLE);
        tv.setText(text);
    }

    public ImageView enableLeftImage(int resId, View.OnClickListener listener) {
        ImageView iv = (ImageView) findViewById(R.id.header_leftimage);
        iv.setVisibility(View.VISIBLE);
        iv.setImageDrawable(getResources().getDrawable(resId));
        iv.setOnClickListener(listener);
        return iv;
    }

    public TextView enableRightText(String text, View.OnClickListener listener) {
        TextView textView = (TextView) findViewById(header_righttext);
        textView.setVisibility(View.VISIBLE);
        textView.setText(text);
        textView.setOnClickListener(listener);
        return textView;
    }

    //展示确认取消对话框
    public void showConfirmCancelDialog(String detail, String confirmTitle, String cancel, final View.OnClickListener confirmListener, final View.OnClickListener cancelListener) {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        View layout = getLayoutInflater().inflate(R.layout.dialog_confirmcancel, null);
        // set the dialog title
        TextView cancelTextView = (TextView) layout.findViewById(R.id.dialog_input_cancel);
        TextView detailT = (TextView) layout.findViewById(R.id.dialog_confirmcancel_title);
        detailT.setText(detail);
        TextView confirmView = (TextView) layout.findViewById(R.id.dialog_input_confirm);
        if (confirmTitle != null) {
            confirmView.setText(confirmTitle);
        }
        if (cancel != null)
            cancelTextView.setText(cancel);
        layout.findViewById(R.id.dialog_input_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmListener != null) {
                    confirmListener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        layout.findViewById(R.id.dialog_input_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
                dialog.cancel();
            }
        });

        dialog.setCancelable(false);
        dialog.setContentView(layout);
        dialog.show();
    }


    public void showBottomDialog(List<String> tips, final List<View.OnClickListener> listeners) {
        if (tips == null || tips.size() == 0 || listeners == null || listeners.size() == 0 || listeners.size() != tips.size()) {
            return;
        }
        final Dialog dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setBackgroundColor(getResources().getColor(R.color.W1));
        for (int i = 0; i < tips.size(); i++) {
            View item = getLayoutInflater().inflate(R.layout.dialog_bottom_item1, null);
            View divide = null;
            if (i != 0) {
                divide = new View(this);
                divide.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                divide.setBackgroundColor(getResources().getColor(R.color.G5));
            }
            TextView textView = (TextView) item.findViewById(R.id.dialog_bottom_text);
            textView.setText(tips.get(i));
            final View.OnClickListener listener = listeners.get(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v);
                    dialog.dismiss();
                }
            });
            if (divide != null) {
                linearLayout.addView(divide);
            }
            linearLayout.addView(item);
        }
        View cancelContainer = getLayoutInflater().inflate(R.layout.dialog_bottom_item2, null);
        cancelContainer.findViewById(R.id.dialog_bottom_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        linearLayout.addView(cancelContainer);
        dialog.setCancelable(true);
        dialog.setContentView(linearLayout);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


}
