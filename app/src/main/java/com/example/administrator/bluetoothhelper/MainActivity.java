package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

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
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;
    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String for MAC address
    private static String address = null;

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
                    }
                }else {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                }

                break;
            case R.id.stop:
                break;
            case R.id.get:
                break;
        }
    }

    private class ConnectedThread {
    }
}
