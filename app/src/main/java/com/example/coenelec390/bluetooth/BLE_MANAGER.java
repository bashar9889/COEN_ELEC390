package com.example.coenelec390.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;


import androidx.core.app.ActivityCompat;

import com.example.coenelec390.Utils;
import java.util.ArrayList;

public class BLE_MANAGER {
    Context context;
    Activity activity;
    private final BluetoothAdapter btAdapter;
    private final BluetoothLeScanner btScanner;
    private final BLE_STATE btState;
    private static final long SCAN_PERIOD = 10000;
    ArrayList<BLE_DEVICE> devices;


    public BLE_MANAGER(Activity _activity){
        context = _activity.getApplicationContext();
        activity  = _activity;
        BluetoothManager bluetoothManager = (BluetoothManager) _activity.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();


        btState = new BLE_STATE(context);
        devices = new ArrayList<>();
    }
    public void startScan() {
        Utils.print("Scanning started");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                    return;
                }
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScan() {
        Utils.print("Scan Stopping");
        AsyncTask.execute(() -> {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }
            btScanner.stopScan(leScanCallback);
        });
    }

    public void showDevices(){
        if (devices.size() != 0) {
            for (BLE_DEVICE device : devices) {
                String deviceInfo = "Device Name:  " + device.getName() + "  Address : " + device.getAddress() + " rssi : " + device.getRSSI() + "\n";
                String espName = "ESP32";
                Utils.print(deviceInfo);
            }
        } else Utils.print("No devices found");
    }
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }
            BLE_DEVICE device = new BLE_DEVICE(result.getDevice(), result.getDevice().getName(), result.getRssi());
            devices.add(device);
        }
    };

    public void enableBluetooth() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Utils.print("Permission not given");
            requestPermissions();
            return;
        }
        activity.startActivity(enableIntent);

        // broadcast the fact that bluetooth changed
        IntentFilter newIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        activity.registerReceiver(btState, newIntent);
    };

    public void disableBluetooth() {
        // guide user to disable bluetooth
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        activity.startActivity(intent);

        // broadcast the fact that bluetooth changed
        IntentFilter newIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        activity.registerReceiver(btState, newIntent);
    }

    public  boolean hasBluetooth(){
        if(btAdapter == null || !(btAdapter.isEnabled())) return false;
        return true;
    }

    public void requestPermissions() {
        int BLUETOOTH_PERMISSION_CODE = 1;
//        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, BLUETOOTH_PERMISSION_CODE);
    }
}
