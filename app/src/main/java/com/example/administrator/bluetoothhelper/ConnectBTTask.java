package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;


/**连接线程

 * Created by zqf on 2018/7/7.

 */



public class ConnectBTTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {

    private static final String TAG = ConnectBTTask.class.getName();

    private BluetoothDevice bluetoothDevice;

    private ConnectBlueCallBack callBack;



    public ConnectBTTask(ConnectBlueCallBack callBack){

        this.callBack = callBack;

    }



    @Override

    protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {

        bluetoothDevice = bluetoothDevices[0];

        BluetoothSocket socket = null;

        try{

            socket = bluetoothDevice.createRfcommSocketToServiceRecord(MainActivity.BTMODULEUUID);

            if (socket != null && !socket.isConnected()){

                socket.connect();

            }

        }catch (IOException e){

            Log.e(TAG,"socket连接失败");

            try {

                socket.close();

            } catch (IOException e1) {

                e1.printStackTrace();

                Log.e(TAG,"socket关闭失败");

            }

        }

        return socket;

    }



    @Override

    protected void onPreExecute() {

        Log.d(TAG,"开始连接");

        if (callBack != null) callBack.onStartConnect();

    }



    @Override

    protected void onPostExecute(BluetoothSocket bluetoothSocket) {

        if (bluetoothSocket != null && bluetoothSocket.isConnected()){

            Log.d(TAG,"连接成功");

            if (callBack != null) callBack.onConnectSuccess(bluetoothDevice, bluetoothSocket);

        }else {

            Log.d(TAG,"连接失败");

            if (callBack != null) callBack.onConnectFail(bluetoothDevice, "连接失败");

        }

    }

}

