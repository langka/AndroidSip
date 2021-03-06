package com.bupt.androidsip.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.util.BitmapUtils;
import com.dd.CircularProgressButton;

import java.util.Random;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    boolean x = false;

    private void initView() {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.batman1, imageView.getWidth(), imageView.getHeight()));
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmCancelDialog("王昊阳","lyk","xs",e->{

                },e->{

                });

                if (checkPwdAndAccount()) {
                    if (confirm.getProgress() == 0) {
                        simulateSuccessProgress(confirm, x); // 如果是初始状态就开始进入进度条动画
                        x = !x;
                    } else {
                        confirm.setProgress(0); // 如果不是初始状态，那么就回到初始状态
                    }
                } else showText("账户或密码有格式错误，请您检查!");
            }
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
