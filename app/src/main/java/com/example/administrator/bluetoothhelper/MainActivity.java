package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PinBlueCallBack,
                                                               ConnectBlueCallBack,
                                                               WriteCallBack,
                                                               ReadCallBack{

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;
    TextView deviceName;
    TextView lux_value;
    TextView temp_value;
    TextView hum_value;
    TextView stat;
    Button connectBtn;
    private BluetoothAdapter btAdapter = null;
    private BluetoothDevice btDevice = null;
    // SPP UUID service - this should work for most devices
    public static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final PinBTReceiver pinBTReceiver = new PinBTReceiver(this);
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothSocket mBluetoothSocket = null;
    private WriteTask writeTask = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pinBTReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceName = findViewById(R.id.device_name);
        lux_value = findViewById(R.id.lux_value);
        temp_value = findViewById(R.id.temp_value);
        hum_value = findViewById(R.id.hum_value);
        stat = findViewById(R.id.stat);
        connectBtn = findViewById(R.id.connect_btn);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "Device does not support Bluetooth",Toast.LENGTH_SHORT).show();
        }else {
            if (!btAdapter.isEnabled()){
                connectBtn.setText(getResources().getString(R.string.to_open));
            }else {
                //to connect BT
                connectBtn.setText(getResources().getString(R.string.to_connect));
            }
        }
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(pinBTReceiver,filter4);
        registerReceiver(pinBTReceiver,filter5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    connectBtn.setText(getResources().getString(R.string.to_connect));
                }
                else {
                    connectBtn.setText(getResources().getString(R.string.to_open));
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == RESULT_OK){
                    String name ,address;
                    if(data != null){
                        String name_and_address = data.getStringExtra("name_and_address");
                        name = name_and_address.substring(0,name_and_address.indexOf("\n"));
                        address = name_and_address.substring(name_and_address.indexOf("\n") + 1).trim();
                        btDevice = btAdapter.getRemoteDevice(address);
                        if (btDevice != null){
                            boolean result = pin(btDevice);
                            if (result){
                                connect(btDevice);
                            }
                        }
                        Log.d("name ---> ", name);
                        Log.d("address ---> ", address);
                    }

                }
                break;
        }
    }

    //点击事件
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.connect_btn:
                //避免空指针
                if (btAdapter != null){
                    if (!btAdapter.isEnabled()){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }else if (btDevice == null){
                        Intent intent = new Intent(this, DeviceListActivity.class);
                        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                    }else {
                        btAdapter.disable();
                        btDevice = null;
                        connectBtn.setText(getString(R.string.to_open));
                    }
                }else {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                }
                break;
            case R.id.stop:
                if (mBluetoothSocket != null){
                    if (writeTask == null){
                        writeTask = new WriteTask(this, mBluetoothSocket);
                        writeTask.execute("S");
                        stat.setText(getString(R.string.stop_stat));
                        stat.setTextColor(getResources().getColor(R.color.colorLightRed));
                        writeTask = null;
                    }
                }
                break;
            case R.id.get:
                if (mBluetoothSocket != null){
                    if (writeTask == null){
                        writeTask = new WriteTask(this, mBluetoothSocket);
                        writeTask.execute("G");
                        stat.setText(getString(R.string.getting));
                        stat.setTextColor(getResources().getColor(R.color.colorLightGreen));
                        writeTask = null;
                    }
                }
                break;
        }
    }

    @Override
    public void onBondRequest() {

    }

    @Override
    public void onBondFail(BluetoothDevice device) {

    }

    @Override
    public void onBonding(BluetoothDevice device) {

    }

    @Override
    public void onBondSuccess(BluetoothDevice device) {
        Toast.makeText(this, "配对成功", Toast.LENGTH_SHORT).show();
    }

    public boolean pin(BluetoothDevice device){
        if (device != null){
            if (btAdapter != null){
                //未配对则配对
                if (device.getBondState() == BluetoothDevice.BOND_NONE){
                    try {
                        Method createBondMethod = device.getClass().getMethod("createBond");
                        return (boolean) createBondMethod.invoke(device);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }else {
                    return true;
                }
            }
        }
        return false;
    }

    /** * 连接 （在配对之后调用） * @param device */
    public void connect(BluetoothDevice device){
        if (device == null){
            Log.d("Main", "bond device null");
            return;
        }
    new ConnectBTTask(this).execute(device);
}

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnectSuccess(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket) {
        connectBtn.setText(getString(R.string.disconnect));
        deviceName.setText(bluetoothDevice.getName());
        this.mBluetoothDevice = bluetoothDevice;
        this.mBluetoothSocket = bluetoothSocket;
        new ReadThread(bluetoothSocket, this).start();
        stat.setText(getString(R.string.getting));
        stat.setTextColor(getResources().getColor(R.color.colorLightGreen));
    }

    @Override
    public void onConnectFail(BluetoothDevice bluetoothDevice, String 连接失败) {

    }

    @Override
    public void onWriteStarted() {
    }

    @Override
    public void onWriteFinished(boolean b, String s) {
        Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReadFinished(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (s.substring(0, 3)){
                    case "lux":
                        lux_value.setText(String.format("%slux", s.substring(3)));
                        break;
                    case "tem":
                        temp_value.setText(String.format("%s°C", s.substring(3)));
                        break;
                    case "hum":
                        hum_value.setText(String.format("%s%%", s.substring(3)));
                        break;
                        default:
                            break;
                }
            }
        });
    }

    public class ReadThread extends Thread{

        private BluetoothSocket mSocket;
        private ReadCallBack callBack;

        ReadThread(BluetoothSocket socket, ReadCallBack callBack){
            this.mSocket = socket;
            this.callBack = callBack;
        }

        @Override
        public void run() {
            super.run();
            BufferedInputStream in;
            String data;
            int ch;
            try {
                in = new BufferedInputStream(mSocket.getInputStream());
                int length = 0;
                byte[] buf = new byte[1024];
                while (true){
                    while ((ch = in.read()) != '\n'){
                        if (ch != -1){
                            buf[length] = (byte) ch;
                            length++;
                        }
                    }
                    if (length > 0)
                    {
                        data = new String(buf,0, length);
                        Log.d("data ---> ",data);
                        length = 0;
                        callBack.onReadFinished(data);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
