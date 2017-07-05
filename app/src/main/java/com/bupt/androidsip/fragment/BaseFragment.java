package com.bupt.androidsip.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.bupt.androidsip.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by xusong on 2017/4/19.
 */

public class BaseFragment extends Fragment {

    public void showFragLoadingView(View root) {
        View v = root.findViewById(R.id.loadingview);
        v.setVisibility(View.VISIBLE);
        AVLoadingIndicatorView loadingIndicatorView = (AVLoadingIndicatorView) v.findViewById(R.id.loading_loader);
        loadingIndicatorView.show();
    }

    public void hideFragLoadingView(View root) {
        View v = root.findViewById(R.id.loadingview);
        v.setVisibility(View.INVISIBLE);
        AVLoadingIndicatorView loadingIndicatorView = (AVLoadingIndicatorView) v.findViewById(R.id.loading_loader);
        loadingIndicatorView.hide();
    }

}
