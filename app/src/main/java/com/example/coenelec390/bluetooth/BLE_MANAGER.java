package com.example.coenelec390.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.os.Handler;
import android.widget.Button;
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
}
