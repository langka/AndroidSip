package com.bupt.androidsip.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bupt.androidsip.R;

/**
 * Created by xusong on 2017/7/2.
 * About:
 */

public class MessageFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_message, null);
        return v;
    }
}
