package com.guyuan.heartrate.ui.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guyuan.heartrate.R;
import com.guyuan.heartrate.adapter.BlueToothAdapter;
import com.guyuan.heartrate.base.BaseActivity;
import com.guyuan.heartrate.service.CenterService;

/**
 * @author : tl
 * @description :
 * @since: 2020/11/9 17:48
 * @company : 固远（深圳）信息技术有限公司
 **/

public class DeviceActivity extends BaseActivity {

    private TextView back_tv;
    private RecyclerView device_rv;

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

        BlueToothAdapter adapter = new BlueToothAdapter(this, R.layout.item_device);
        adapter.setListener(new BlueToothAdapter.BlueToothAdapterListener() {
            @Override
            public void connect(CenterService.BleDev bleDev) {

            }
        });
        device_rv.setLayoutManager(new LinearLayoutManager(this));
        device_rv.setAdapter(adapter);
    }


}