package com.example.coenelec390;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class Utils {
    private static final String TAG = "MAIN_ACTIVITY";
    public static boolean hasBluetooth(BluetoothAdapter adapter){
        if(adapter == null || !(adapter.isEnabled())) return false;
        return true;
    }

    public static void print(String msg) {
        Log.d(TAG, msg);
    }
    public static void display(Context context,  String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

};


