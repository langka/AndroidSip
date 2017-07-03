package com.bupt.androidsip.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.bupt.androidsip.R;
import com.bupt.androidsip.util.ViewUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xusong on 2017/7/3.
 * About:
 */
public class SlideBar extends View {

    public static String[] INDEX_STRING = {"热", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private List<String> letterList;

    private int choose = -1;
    private Paint paint = new Paint();
    private TextView mTextDialog;

    private Context context;
    Rect bounds;


    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();

    }

    public void setLetterList(List<String> letterList) {
        this.letterList = letterList;
    }

    private void init() {
        letterList = Arrays.asList(INDEX_STRING);
    }

    public void setChoose(char letter){
        letter = Character.toUpperCase(letter);
        for(int i=0;i<letterList.size();i++){
            if(letter==letterList.get(i).charAt(0)){
                choose = i;
                invalidate();
                return;
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();// 获取对应高度
        int width = getWidth();// 获取对应宽度
        float singleHeight = (float) height / letterList.size();// 获取每一个字母的高度
        for (int i = 0; i < letterList.size(); i++) {
            paint.setColor(getResources().getColor(R.color.G2));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(ViewUtils.dp2px(context, 11));
            // 选中的状态
            if (i == choose) {
                paint.setColor(getResources().getColor(R.color.R1));
                paint.setFakeBoldText(true);
            }
            // x坐标等于中间-字符串宽度的一半.
            float xPos = width / 2 - paint.measureText(letterList.get(i)) / 2;
            float yPos = singleHeight * i + singleHeight / 2;
            if (bounds == null) {
                bounds = new Rect();
                paint.getTextBounds(letterList.get(i), 0, 1, bounds);
            }
            canvas.drawText(letterList.get(i), xPos, yPos + bounds.height() / (float) 2, paint);
            paint.reset();// 重置画笔
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();// 点击y坐标
        final int oldChoose = choose;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * letterList.size());// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

        switch (action) {
            case MotionEvent.ACTION_UP:
                //setBackgroundColor(Color.parseColor("#F0F0F0"));
                //choose = -1;
                invalidate();
                if (mTextDialog != null) {
                    mTextDialog.setVisibility(View.GONE);
                }
                break;
            default:
                //setBackgroundResource(R.drawable.sidebar_background);
                if (oldChoose != c) {
                    if (c >= 0 && c < letterList.size()) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(letterList.get(c));
                        }
                        if (mTextDialog != null) {
                            mTextDialog.setText(letterList.get(c));
                            mTextDialog.setVisibility(View.VISIBLE);
                        }
                        choose = c;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    public void setIndexText(ArrayList<String> indexStrings) {
        this.letterList = indexStrings;
        invalidate();
    }

    /**
     * 为SideBar设置显示当前按下的字母的TextView
     *
     * @param mTextDialog
     */
    public void setTextView(TextView mTextDialog) {
        this.mTextDialog = mTextDialog;
    }

    /**
     * 向外公开的方法
     *
     * @param onTouchingLetterChangedListener
     */
    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    /**
     * 接口
     */
    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }

}
