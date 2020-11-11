package com.guyuan.heartrate.ui.sport;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
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
import com.guyuan.heartrate.util.CommonUtl;
import com.guyuan.heartrate.util.ConstanceValue;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTED;

public class SportFragment extends BaseFragment {

    public static final String TAG = "SportFragment";
    private TextView status_tv;
    private SwitchCompat tip_switch;
    private ImageView tip_iv;
    private Chronometer cm;
    private TextView connect_time_tv;

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
        cm = getView().findViewById(R.id.cm);
        connect_time_tv = getView().findViewById(R.id.connect_time_tv);
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
        boolean isConnect = bluetoothClient.getConnectStatus(ConstanceValue.macAddress) == STATUS_DEVICE_CONNECTED;
        setUI(isConnect);
        getHeartRate();
    }

    public void setUI(boolean connect) {
        if (connect) {
            status_tv.setText(getString(R.string.reading));
            connect_time_tv.setVisibility(View.VISIBLE);
            cm.setVisibility(View.VISIBLE);
            cm.setBase(SystemClock.elapsedRealtime());
            cm.start();

        } else {
            status_tv.setText(getString(R.string.disconnect));
            connect_time_tv.setVisibility(View.GONE);
            cm.stop();
            cm.setVisibility(View.GONE);
        }
    }


    private void getHeartRate() {
        UUID heartServiceUUID = UUID.fromString(ConstanceValue.HEART_RATE_SERVICE_UUID);
        UUID heartCharacteristicUUID = UUID.fromString(ConstanceValue.HEART_RATE_CHARACTERISTIC_UUID);
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(2000);
                    //    if (!TextUtils.isEmpty(ConstanceValue.macAddress)) {
                            bluetoothClient.read(ConstanceValue.macAddress, heartServiceUUID, heartCharacteristicUUID,
                                    new BleReadResponse() {
                                        @Override
                                        public void onResponse(int code, byte[] data) {
                                            if (code == REQUEST_SUCCESS) {
                                                status_tv.setText(Math.random() + "");
                                                //status_tv.setText(String.valueOf(CommonUtl.bytesToInt(data, 0)));
                                            }
                                        }
                                    });
                    //    }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}