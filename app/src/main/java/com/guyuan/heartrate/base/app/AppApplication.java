package com.guyuan.heartrate.base.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.guyuan.heartrate.busbean.BlueToothConnectBusBean;
import com.guyuan.heartrate.util.CommonUtl;
import com.guyuan.heartrate.util.ConstanceValue;
import com.guyuan.heartrate.util.SharedPreferencesUtils;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

/**
 * created by tl on 2019/7/28
 */
public class AppApplication extends Application {

    private static AppApplication application;
    private int width = 0, height = 0;
    private SharedPreferencesUtils mSharedPreferencesUtils;
    private BluetoothClient bleClient;
    protected BleConnectStatusListener connectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (status == STATUS_CONNECTED) {
                sendStatus(mac, true);
            } else if (status == STATUS_DISCONNECTED) {
                sendStatus("", false);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mSharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
        bleClient = new BluetoothClient(this);
        String connectAddress = CommonUtl.getConnectedAddress();
        if (!TextUtils.isEmpty(connectAddress)) {//打开app时如果已经连上蓝牙设备则设置蓝牙连接状态监听
            bleClient.registerConnectStatusListener(connectAddress, connectStatusListener);
        }

    }

    public void sendStatus(String address, boolean isConnect) {
        ConstanceValue.macAddress = address;
        BlueToothConnectBusBean busBean = new BlueToothConnectBusBean();
        busBean.setConnect(isConnect);
        EventBus.getDefault().post(busBean);
    }


    private void initProperty() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // 屏幕宽度（像素）
        height = metric.heightPixels; // 屏幕高度（像素
    }

    public BleConnectStatusListener getConnectStatusListener() {
        return connectStatusListener;
    }

    public static AppApplication getInstance() {
        return application;
    }

    public int getWindowWidth() {
        if (width == 0) {
            initProperty();
        }
        return this.width;
    }

    public int getWindowHeight() {
        if (height == 0) {
            initProperty();
        }
        return this.height;
    }

    public BluetoothClient getBleClient() {
        return bleClient;
    }

    /**
     * 采用andriod本身数据格式缓存数据
     * 后期可以更改成其他缓存
     */
    public void saveCacheData(String key, Object data) {
        saveCacheData(SharedPreferencesUtils.SP_NAME, key, data);
    }

    private void saveCacheData(final String fileName, final String key, final Object defaultObject) {
        mSharedPreferencesUtils.saveData(fileName, key, defaultObject);
    }

    public Object getCacheData(String key, Object defaultObject) {
        return getCacheData(SharedPreferencesUtils.SP_NAME, key, defaultObject);
    }

    private Object getCacheData(final String fileName, final String key, final Object defaultObject) {
        return mSharedPreferencesUtils.getData(fileName, key, defaultObject);
    }

    public void saveCacheListData(String key, final List<Map<String, String>> dataList) {
        saveCacheListData(SharedPreferencesUtils.SP_NAME, key, dataList);
    }

    private void saveCacheListData(final String fileName, final String key, final List<Map<String, String>> dataList) {
        mSharedPreferencesUtils.saveListData(fileName, key, dataList);
    }

    public List<Map<String, String>> getCacheListData(final String key) {
        return getCacheListData(SharedPreferencesUtils.SP_NAME, key);
    }

    private List<Map<String, String>> getCacheListData(final String fileName, final String key) {
        return mSharedPreferencesUtils.getListData(fileName, key);
    }

    public void removeListData(final String key) {
        mSharedPreferencesUtils.removeListData(SharedPreferencesUtils.SP_NAME, key);
    }

    public void saveCacheStringListData(String key, final List<String> dataList) {
        mSharedPreferencesUtils.saveStringListData(SharedPreferencesUtils.SP_NAME, key, dataList);
    }

    public List<String> getCacheStringListData(final String key) {
        return mSharedPreferencesUtils.getStringListData(SharedPreferencesUtils.SP_NAME, key);
    }

    public void saveMapData(String key, Map<String, String> mapData) {
        mSharedPreferencesUtils.saveMapData(SharedPreferencesUtils.SP_NAME, key, mapData);
    }

    public Map<String, String> getMapData(String key) {
        return mSharedPreferencesUtils.getMapData(SharedPreferencesUtils.SP_NAME, key);
    }

    public void saveTreeMapData(String key, Map<String, String> mapData) {
        mSharedPreferencesUtils.saveTreeMapData(SharedPreferencesUtils.SP_NAME, key, mapData);
    }

    public Map<String, String> getTreeMapData(String key) {
        return mSharedPreferencesUtils.getTreeMapData(SharedPreferencesUtils.SP_NAME, key);
    }

}
