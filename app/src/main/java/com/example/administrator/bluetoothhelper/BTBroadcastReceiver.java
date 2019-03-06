package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BTBroadcastReceiver extends BroadcastReceiver {

    private ScanBTCallBack callBack;

    public BTBroadcastReceiver(ScanBTCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d("device search", action);
        switch (action){
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                Log.d("receiver", "开始扫描...");
                callBack.onScanStarted();
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Log.d("receiver", "结束扫描...");
                callBack.onScanFinished();
                break;
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("receiver", "发现设备...");
                callBack.onScanning(device);
                break;
        }
    }
}
