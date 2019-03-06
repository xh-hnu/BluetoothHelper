package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothDevice;

public interface ScanBTCallBack {

    void onScanStarted();
    void onScanFinished();
    void onScanning(BluetoothDevice device);
}
