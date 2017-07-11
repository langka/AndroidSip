package com.bupt.androidsip.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bupt.androidsip.R;
import com.bupt.androidsip.entity.response.SipLoginResponse;
import com.bupt.androidsip.entity.sip.SipFailure;
import com.bupt.androidsip.mananger.FriendManager;
import com.bupt.androidsip.mananger.SipChatManager;
import com.bupt.androidsip.mananger.UserManager;
import com.bupt.androidsip.sip.SipNetListener;
import com.bupt.androidsip.sip.impl.SipManager;
import com.bupt.androidsip.util.BitmapUtils;
import com.dd.CircularProgressButton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xusong on 2017/5/3.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.bgImgview)
    ImageView imageView;
    @BindView(R.id.btnConfirm)
    CircularProgressButton confirm;
    @BindView(R.id.login_account_edit)
    EditText accountEdit;
    @BindView(R.id.login_pwd_edit)
    EditText pwdEdit;

    SipManager sipManager = SipManager.getSipManager();
    UserManager userManager = UserManager.getInstance();
    SipChatManager sipChatManager = SipChatManager.getInstance();
    FriendManager friendManager = FriendManager.getInstance();

    boolean isShock = true;
    boolean pushEnterToSend = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MySettings", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isShock", isShock);
        editor.putBoolean("pushEnterToSend", pushEnterToSend);
        editor.apply();
        initView();

    }

    boolean x = false;

    private void initView() {

        imageView.post(() -> imageView.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(getResources(),
                R.drawable.batman1, imageView.getWidth(), imageView.getHeight())));
        confirm.setOnClickListener(v -> {


            if ((!TextUtils.isEmpty(accountEdit.getText())) &&
                    (!TextUtils.isEmpty(pwdEdit.getText()))) {
                showLoadingView();

                //调试用
                hideLoadingView();
                TabActivity.Start(LoginActivity.this);
                finish();


                sipManager.login(accountEdit.getText().toString(), pwdEdit.getText().toString(),
                        new SipNetListener<SipLoginResponse>() {
                            @Override
                            public void onSuccess(SipLoginResponse response) {
                                userManager.initUser(response);
                                sipChatManager.setSipChat(response.groups);
                                friendManager.setFriends(response.friends);
                                hideLoadingView();
                                TabActivity.Start(LoginActivity.this);
                                finish();
                            }

                            @Override
                            public void onFailure(SipFailure failure) {
                                Toast.makeText(getApplicationContext(),
                                        failure.reason, Toast.LENGTH_SHORT).show();
                                hideLoadingView();
                                pwdEdit.setText("");
                            }
                        });


            } else
                Toast.makeText(getApplicationContext(),
                        "请输入账号和密码", Toast.LENGTH_SHORT).show();
        });
    }

    // TODO: 2017/5/3
    private boolean checkPwdAndAccount() {
        return true;
    }

    private void simulateSuccessProgress(final CircularProgressButton button, boolean success) {
        // 这里巧妙运用了valueAnimator这个类来计算动画的值，这个类本身就起计算作用，不处理任何动画，这里在计算好后自行进行了进度的设定
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, 100); // 设定范围为1到100
        widthAnimation.setDuration(1500); // 设定动画的持续时间
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator()); // 设定动画的插值器
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // 在动画进行时进行处理
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                if (value != 100)
                    button.setProgress(value);
                else if (success)
                    button.setProgress(100);
                else button.setProgress(-1);
            }
        });
        widthAnimation.start(); // 开始动画的计算工作
    }

}
