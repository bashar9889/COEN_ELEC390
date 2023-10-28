package com.example.coenelec390.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.ActivityCompat;

import com.example.coenelec390.Utils;
import java.util.ArrayList;

public class BLE_MANAGER {
    Context context;
    Activity activity;
    private BluetoothAdapter btAdapter;
    private BluetoothLeScanner btScanner;
    private static final int REQUEST_ENABLE_BT = 1;
    private int BLUETOOTH_PERMISSION_CODE = 1;
    private BLE_STATE btState;
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
    public  boolean hasBluetooth(){
        if(btAdapter == null || !(btAdapter.isEnabled())) return false;
        return true;
    }


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

    public void requestPermissions() {
        int BLUETOOTH_PERMISSION_CODE = 1;
//        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, BLUETOOTH_PERMISSION_CODE);
    }
}
