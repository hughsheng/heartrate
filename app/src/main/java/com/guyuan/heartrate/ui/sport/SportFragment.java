package com.guyuan.heartrate.ui.sport;

import android.media.MediaPlayer;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.guyuan.heartrate.R;
import com.guyuan.heartrate.base.BaseFragment;
import com.guyuan.heartrate.util.CommonUtl;
import com.guyuan.heartrate.util.ConstanceValue;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private boolean isCollecting = false;  //是否正在采集心率数据
    private List<Integer> dataList = new ArrayList<>();//采集数据集合
    private int averageHeartRate;//心率平均值
    private MediaPlayer mediaPlayer;

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
        tip_iv = getView().findViewById(R.id.tip_iv);
        connect_time_tv = getView().findViewById(R.id.connect_time_tv);

        tip_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ConstanceValue.macAddress)) {
                    showToastTip(R.string.not_connect_tip);
                    tip_switch.setChecked(false);
                }
            }
        });
        tip_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//打开心率提醒
                    openCollecting();
                    tip_iv.setImageResource(R.mipmap.notice_on);
                } else {//关闭心率提醒
                    closeCollecting();
                    tip_iv.setImageResource(R.mipmap.notice_off);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
            }
        });

        cm.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                // 从开始计时到现在满了5分钟
                if (SystemClock.elapsedRealtime() - chronometer.getBase() == 5 * 1000) {
                    closeCollecting();
                }
            }
        });

        setUI(!TextUtils.isEmpty(ConstanceValue.macAddress));
        getHeartRate();
        setMediaPlayer();
    }

    public void setUI(boolean connect) {
        if (connect) {
            status_tv.setText(getString(R.string.reading));

        } else {
            status_tv.setText(getString(R.string.disconnect));
        }
    }

    //打开心率采集
    private void openCollecting() {
        dataList.clear();
        connect_time_tv.setVisibility(View.VISIBLE);
        cm.setVisibility(View.VISIBLE);
        cm.setBase(SystemClock.elapsedRealtime());
        cm.start();
        isCollecting = true;
    }


    //关闭心率采集
    private void closeCollecting() {
        connect_time_tv.setVisibility(View.GONE);
        cm.stop();
        cm.setVisibility(View.GONE);
        isCollecting = false;
    }

    //心率对比
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void compareHeartRate(int heartRate) {
        if (averageHeartRate == 0) {
            averageHeartRate = (int) dataList.stream().mapToInt(i -> i).average().orElse(0);
        }

        if (Math.abs(averageHeartRate - heartRate) > 5) {//报警
            tip_iv.setImageResource(R.mipmap.ic_launcher);
            mediaPlayer.start();
        }
    }


    private void setMediaPlayer() {
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.notice);
        mediaPlayer.setLooping(true);
    }


    //读取心率
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
                        if (!TextUtils.isEmpty(ConstanceValue.macAddress)) {
//                            bluetoothClient.read(ConstanceValue.macAddress, heartServiceUUID, heartCharacteristicUUID,
//                                    new BleReadResponse() {
//                                        @Override
//                                        public void onResponse(int code, byte[] data) {
//                                            if (code == REQUEST_SUCCESS) {
//                                                status_tv.setText(String.valueOf(CommonUtl.bytesToInt(data, 0)));
//                                            } else {
//                                                showToastTip("读取心率失败");
//                                            }
//                                        }
//                                    });

                            bluetoothClient.notify(ConstanceValue.macAddress, heartServiceUUID, heartCharacteristicUUID, new BleNotifyResponse() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onNotify(UUID service, UUID character, byte[] value) {
                                    int heartRate = value[1];
                                    status_tv.setText(String.valueOf(heartRate));
                                    if (heartRate > 0 && tip_switch.isChecked()) {//去掉无效数据,并且处于提醒状态
                                        if (isCollecting) {//采集状态：采集心率数据
                                            dataList.add(heartRate);
                                        } else {//对比状态：对比心率数据
                                            compareHeartRate(heartRate);
                                        }
                                    }
                                }

                                @Override
                                public void onResponse(int code) {
                                    if (code != REQUEST_SUCCESS) {
                                        showToastTip("读取心率失败");
                                    }
                                }
                            });
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}