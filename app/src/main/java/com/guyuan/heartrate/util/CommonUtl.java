package com.guyuan.heartrate.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.text.TextUtils;

import com.guyuan.heartrate.base.app.AppApplication;
import com.guyuan.heartrate.busbean.BlueToothConnectBusBean;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.search.SearchRequest;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

/**
 * @author : tl
 * @description :
 * @since: 2020/11/11 14:18
 * @company : 固远（深圳）信息技术有限公司
 **/

public class CommonUtl {
    //获取已连上verge设备
    public static String getConnectedAddress() {
        String address = "";
        BluetoothManager bluetoothManager = (BluetoothManager) AppApplication.getInstance()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            if (devices != null && devices.size() > 0) {
                address = devices.get(0).getAddress();
            }
        }
        return address;
    }

    //获取蓝牙搜索配置
    public static SearchRequest getSearchRequest() {
        return new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();
    }


    //字节数组转int
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF) |
                ((src[offset + 1] & 0xFF) << 8) |
                ((src[offset + 2] & 0xFF) << 16) |
                ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    //断开连接，释放资源
    public static void disConnectAndRelease() {
        if (!TextUtils.isEmpty(ConstanceValue.macAddress)) {
            AppApplication application = AppApplication.getInstance();
            application.getBleClient().disconnect(ConstanceValue.macAddress);
        }
    }
}