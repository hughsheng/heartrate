package com.guyuan.heartrate.ui.device;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.guyuan.heartrate.R;
import com.guyuan.heartrate.adapter.BlueToothAdapter;
import com.guyuan.heartrate.base.BaseActivity;
import com.guyuan.heartrate.util.CommonUtl;
import com.guyuan.heartrate.util.ConstanceValue;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import java.util.List;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * @author : tl
 * @description :
 * @since: 2020/11/9 17:48
 * @company : 固远（深圳）信息技术有限公司
 **/

public class DeviceActivity extends BaseActivity {

    private TextView back_tv;
    private RecyclerView device_rv;
    private BlueToothAdapter adapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, DeviceActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_device;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        back_tv = findViewById(R.id.back_tv);
        back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        device_rv = findViewById(R.id.device_rv);

        adapter = new BlueToothAdapter(this, R.layout.item_device);
        adapter.setListener(new BlueToothAdapter.BlueToothAdapterListener() {
            @Override
            public void connect(BluetoothDevice bleDev) {
                showLoadingWithStatus(fragmentManager, "连接中...");
                String address = bleDev.getAddress();
                bluetoothClient.registerConnectStatusListener(address, application.getConnectStatusListener());
                bluetoothClient.connect(bleDev.getAddress(), application.getOptions(), new BleConnectResponse() {
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
        });
        device_rv.setLayoutManager(new LinearLayoutManager(this));
        device_rv.setAdapter(adapter);

        //蓝牙搜索回调
        bluetoothClient.search(CommonUtl.getSearchRequest(), new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                BluetoothDevice bleDevice = device.device;
                List<BluetoothDevice> deviceList = adapter.getBleDevList();
                if (bleDevice != null && !deviceList.contains(bleDevice) && !TextUtils.isEmpty(bleDevice.getAddress())) {
                    adapter.addData(bleDevice);
                }
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });
    }


}