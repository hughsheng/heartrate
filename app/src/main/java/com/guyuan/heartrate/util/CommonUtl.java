package com.guyuan.heartrate.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.guyuan.heartrate.base.app.AppApplication;
import com.inuker.bluetooth.library.search.SearchRequest;

import java.util.List;

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
}