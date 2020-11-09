package com.guyuan.heartrate.ui.earphone;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.guyuan.heartrate.R;
import com.guyuan.heartrate.base.BaseFragment;
import com.guyuan.heartrate.ui.device.DeviceActivity;

public class EarphoneFragment extends BaseFragment {

    public static final String TAG = "EarphoneFragment";
    private TextView bindTv;

    public static EarphoneFragment newInstance() {

        Bundle args = new Bundle();

        EarphoneFragment fragment = new EarphoneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_earphone;
    }

    @Override
    protected void initialization() {
        initView();
    }

    private void initView() {
        bindTv = getView().findViewById(R.id.bind_tv);
        bindTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                String connect = (String) tv.getText();
                if (connect.equals(getString(R.string.bind))) {
                    DeviceActivity.start(getContext());
                } else if (connect.equals(getString(R.string.unbind))) {
                    // todo 解绑
                }
            }
        });
    }
}