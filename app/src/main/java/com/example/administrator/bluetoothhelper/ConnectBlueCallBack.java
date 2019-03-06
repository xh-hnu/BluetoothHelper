package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

interface ConnectBlueCallBack {
    void onStartConnect();

    void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket);

    void onConnectFail(BluetoothDevice bluetoothDevice, String 连接失败);
}
