package com.guyuan.heartrate.ui.sport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.guyuan.heartrate.R;
import com.guyuan.heartrate.base.BaseFragment;

public class SportFragment extends BaseFragment {

    public static final String TAG = "SportFragment";
    private TextView status_tv;
    private SwitchCompat tip_switch;
    private ImageView tip_iv;

    public static SportFragment newInstance() {

        Bundle args = new Bundle();

        SportFragment fragment = new SportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_sport;
    }

    @Override
    protected void initialization() {
        initView();
    }

    private void initView() {
        status_tv = getView().findViewById(R.id.status_tv);
        tip_switch = getView().findViewById(R.id.tip_switch);
        tip_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tip_iv.setImageResource(R.mipmap.notice_on);
                } else {
                    tip_iv.setImageResource(R.mipmap.notice_off);
                }
            }
        });
        tip_iv = getView().findViewById(R.id.tip_iv);
    }
}