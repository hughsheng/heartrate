package com.guyuan.heartrate.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * created by tl on 2019/8/2
 */
public class ConstanceValue {

    //上次连接的蓝牙mac地址
    public static final String LAST_CONNECTED_BLE = "last_connected_ble";

    //连接的蓝牙设备Mac地址
    public static String macAddress = "";

    //心率服务uuid
    public static String HEART_RATE_SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    //心率特征值uuid
    public static String HEART_RATE_CHARACTERISTIC_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
}
