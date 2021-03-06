package com.guyuan.heartrate.ui;

import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.guyuan.heartrate.R;
import com.guyuan.heartrate.base.BaseActivity;
import com.guyuan.heartrate.busbean.BlueToothConnectBusBean;
import com.guyuan.heartrate.ui.earphone.EarphoneFragment;
import com.guyuan.heartrate.ui.sport.SportFragment;
import com.guyuan.heartrate.util.ActivityUtils;
import com.guyuan.heartrate.util.CommonUtl;
import com.guyuan.heartrate.util.ConstanceValue;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static java.security.AccessController.getContext;

public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private Fragment currentFragment;
    private SportFragment sportFragment;
    private EarphoneFragment earphoneFragment;


    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        requestBle();
        setNav();
        initFragment();
    }

    private void initFragment() {
        sportFragment = SportFragment.newInstance();
        earphoneFragment = EarphoneFragment.newInstance();
        currentFragment = sportFragment;//设置运动为初始界面
        ActivityUtils.addFragmentToActivity(fragmentManager, currentFragment,
                R.id.fragment_container, SportFragment.TAG);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BlueToothConnectBusBean bean) {
        if (bean.isConnect()) {
            showToastTip("连接成功");
            connectState();
        } else {
            showToastTip("断开连接");
            disConnectedState();
        }
    }

    //连接状态
    private void connectState() {
        sportFragment.setUI(true);
        earphoneFragment.setUI(true);
    }

    //未连接状态
    private void disConnectedState() {
        sportFragment.setUI(false);
        earphoneFragment.setUI(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //    CommonUtl.disConnectAndRelease();
    }


    private void setNav() {
        RadioGroup rp = findViewById(R.id.rd_group);
        rp.setOnCheckedChangeListener(this);
    }


    @Override
    protected void onBlueToothOpened() {
        super.onBlueToothOpened();
        connectBle();
    }

    //连接上次连接的蓝牙设备
    private void connectBle() {
        String address = (String) application.getCacheData(ConstanceValue.LAST_CONNECTED_BLE, "");
        if (!TextUtils.isEmpty(address)) {
            bluetoothClient.registerConnectStatusListener(address, application.getConnectStatusListener());
            bluetoothClient.connect(address, application.getOptions(), new BleConnectResponse() {
                @Override
                public void onResponse(int code, BleGattProfile data) {
                    hideLoading();
                    if (code == REQUEST_SUCCESS) {//连接成功
                        application.saveCacheData(ConstanceValue.LAST_CONNECTED_BLE, address);
                        finish();
                    }
                }
            });
        }
    }

    //切换fragment
    private void switchContent(Fragment from, Fragment to, String tag) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_container, to, tag).commitAllowingStateLoss(); //
                // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.home_sport:
                switchContent(currentFragment, sportFragment, SportFragment.TAG);
                break;

            case R.id.home_earphone:
                switchContent(currentFragment, earphoneFragment, EarphoneFragment.TAG);
                break;

            default:

                break;
        }
    }


}