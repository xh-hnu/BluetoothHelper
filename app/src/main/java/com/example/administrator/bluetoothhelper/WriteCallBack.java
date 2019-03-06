package com.example.administrator.bluetoothhelper;

interface WriteCallBack {
    void onWriteStarted();
    void onWriteFinished(boolean b, String s);
}
