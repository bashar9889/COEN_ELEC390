package com.example.coenelec390.bluetooth;

import com.example.coenelec390.Utils;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//Broadcast Receiver
public class BLE_STATE extends BroadcastReceiver {
    Context activityContext;
    public BLE_STATE(Context _activityContext){
        this.activityContext = _activityContext;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state){
                case BluetoothAdapter.STATE_OFF:
                    Utils.print("onReceive: State OFF");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Utils.print("on Receive : State Turning OFF");
                    break;

                case BluetoothAdapter.STATE_ON:
                    Utils.print("onReceive : State ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Utils.print("onReceive : State Turning on");
                    break;
            }
        }
    }
}
