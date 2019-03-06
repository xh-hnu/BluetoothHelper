package com.example.administrator.bluetoothhelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity implements ScanBTCallBack, OnItemClickListener{

    private BluetoothAdapter mBTAdapter;
    private RecyclerView.Adapter mDiscoverAdapter;
    private List<String> mPairedDevices = new ArrayList<>();
    private List<String> mDiscoverDevices = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView searchTextView;
    private Button scanBtn;
    boolean isScanning = false;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BTBroadcastReceiver(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        RecyclerView pairedRecyclerView = findViewById(R.id.paired_recycle_view);
        RecyclerView discoverRecycleView = findViewById(R.id.discover_recycle_view);
        progressBar = findViewById(R.id.progress);
        searchTextView = findViewById(R.id.search_textView);
        scanBtn = findViewById(R.id.scan_btn);
        progressBar.setVisibility(View.GONE);
        searchTextView.setVisibility(View.GONE);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter != null){
            Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
        // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    mPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
            }
        }
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        pairedRecyclerView.setLayoutManager(layoutManager);
        discoverRecycleView.setLayoutManager(layoutManager2);
        // specify an adapter (see also next example)
        RecyclerView.Adapter mPairedAdapter = new MyAdapter(mPairedDevices,this);
        mDiscoverAdapter = new MyAdapter(mDiscoverDevices,this);
        //set Adapter
        pairedRecyclerView.setAdapter(mPairedAdapter);
        discoverRecycleView.setAdapter(mDiscoverAdapter);
        // Create a BroadcastReceiver for ACTION_FOUND
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filter1 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filter2 = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        registerReceiver(mReceiver, filter1);
        registerReceiver(mReceiver, filter2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void onClick(View view) {

        switch (view.getId()){
            case R.id.scan_btn:
                if (mBTAdapter != null){
                    if (!isScanning){
                        //discover bluetooth
                        progressBar.setVisibility(View.VISIBLE);
                        searchTextView.setVisibility(View.VISIBLE);
                        mDiscoverDevices.clear();
                        boolean result = mBTAdapter.startDiscovery();
                        Log.d("Device discover", "" + result);
                        scanBtn.setText(getString(R.string.cannel));
                        isScanning = true;
                    }else {
                        //cancel discover BT
                        progressBar.setVisibility(View.INVISIBLE);
                        searchTextView.setVisibility(View.VISIBLE);
                        searchTextView.setText("发现" + mDiscoverDevices.size() + "个设备");
                        scanBtn.setText(getString(R.string.scan));
                        mBTAdapter.cancelDiscovery();
                        isScanning = false;
                    }
                }
                break;
        }
    }

    @Override
    public void onScanStarted() {
        progressBar.setVisibility(View.VISIBLE);
        searchTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onScanFinished() {
        progressBar.setVisibility(View.INVISIBLE);
        searchTextView.setText("发现" + mDiscoverDevices.size() + "个设备");
    }

    @Override
    public void onScanning(BluetoothDevice device) {
        if(device.getName() != null){
            mDiscoverDevices.add(device.getName() + "\n" + device.getAddress());
           mDiscoverAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view) {
        TextView textView = (TextView) view;
        Intent intent = new Intent();
        intent.putExtra("name_and_address", (String) textView.getText());
        setResult(RESULT_OK, intent);
        mBTAdapter.cancelDiscovery();
        finish();
    }
}
