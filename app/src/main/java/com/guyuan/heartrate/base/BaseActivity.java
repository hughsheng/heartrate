package com.guyuan.heartrate.base;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.android.material.snackbar.Snackbar;
import com.guyuan.heartrate.R;
import com.guyuan.heartrate.base.app.AppApplication;
import com.guyuan.heartrate.util.ConstanceValue;
import com.guyuan.heartrate.util.LoadingDialogFragment;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * created by tl
 * created at 2020/8/18
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected FragmentManager fragmentManager;
    protected AppApplication application = AppApplication.getInstance();
    protected BluetoothClient bluetoothClient = application.getBleClient();
    protected LoadingDialogFragment loadingDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        fragmentManager = getSupportFragmentManager();
        loadingDialogFragment = LoadingDialogFragment.newInstance();
        loadingDialogFragment.setColor(R.color.white);
        init(savedInstanceState);
    }


    protected View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    protected abstract int getLayoutID();

    protected abstract void init(Bundle savedInstanceState);


    /**
     * app字体不跟随系统设置改变而改变
     *
     * @param newConfig config
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
        {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 去掉手机状态栏
     */
    protected void setFullScreen() {
        getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    /**
     * 检查该设备是否打开蓝牙
     */
    private void checkBleSwitch() {
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            BluetoothAdapter blueToothAdapter = bluetoothManager.getAdapter();
            if (blueToothAdapter == null || !blueToothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
                Toast.makeText(this, "请允许蓝牙权限，否则无法正常使用app！", Toast.LENGTH_SHORT).show();
            } else {
                onBlueToothOpened();
            }
        } else {
            Toast.makeText(this, "请打开蓝牙，否则无法正常使用app！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 蓝牙和位置权限
     */
    public void requestBle() {

        PermissionsUtil.requestPermission(this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                // 检测蓝牙开关
                checkBleSwitch();
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Log.e("Fuck", "用户拒绝了-蓝牙权限");
            }
        }, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
        }, false, null);
    }


    protected void onBlueToothOpened() {
    }

    ;


    public void showToastTip(int resId) {
        String txt = getString(resId);
        showToastTip(txt);
    }

    public void showToastTip(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(message);
        toast.show();
    }

    public void showSnackBarTip(String message) {
        Snackbar.make(getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBarTip(int resId) {
        Snackbar.make(getRootView(), resId, Snackbar.LENGTH_SHORT).show();
    }

    public void showLoading(FragmentManager manager) {
        loadingDialogFragment.show(manager, LoadingDialogFragment.TAG);
    }

    public void showLoadingWithStatus(FragmentManager manager, String status) {
        loadingDialogFragment.showWithStatus(manager, status);
    }

    public void hideLoading() {
        loadingDialogFragment.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothClient != null && !TextUtils.isEmpty(ConstanceValue.macAddress)) {
            bluetoothClient.disconnect(ConstanceValue.macAddress);
        }
    }
}
