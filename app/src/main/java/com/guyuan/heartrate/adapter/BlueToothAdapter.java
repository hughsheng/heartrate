package com.guyuan.heartrate.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guyuan.heartrate.R;
import com.guyuan.heartrate.service.CenterService;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * created by tl on 2019/8/3
 */
public class BlueToothAdapter extends RecyclerView.Adapter<BlueToothAdapter.Holder> {
    private Context context;
    private List<BluetoothDevice> bleDevList = new ArrayList<>();
    private int layoutId;
    private BlueToothAdapterListener listener;


    public BlueToothAdapter(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final BluetoothDevice bleDev = bleDevList.get(position);
        if (!TextUtils.isEmpty(bleDev.getName())) {
            holder.title.setText(bleDev.getName());
        } else {
            holder.title.setText(bleDev.getAddress());
        }
        holder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.connect(bleDev);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bleDevList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView connect;


        private Holder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.device_name_tv);
            connect = itemView.findViewById(R.id.connect_tv);
        }
    }


    public void setListener(BlueToothAdapterListener listener) {
        this.listener = listener;
    }


    public void setData(List<BluetoothDevice> list) {
        bleDevList.clear();
        bleDevList.addAll(list);
        notifyDataSetChanged();
    }

    public void addData(BluetoothDevice device) {
        bleDevList.add(device);
        notifyDataSetChanged();
    }

    public void clearData() {
        bleDevList.clear();
        notifyDataSetChanged();
    }

    public List<BluetoothDevice> getBleDevList() {
        return bleDevList;
    }

    public interface BlueToothAdapterListener {
        void connect(BluetoothDevice bleDev);
    }

}
