package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothDevice;

interface PinBlueCallBack {
    void onBondRequest();

    void onBondFail(BluetoothDevice device);

    void onBonding(BluetoothDevice device);

    void onBondSuccess(BluetoothDevice device);
}
