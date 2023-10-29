package com.example.coenelec390.bluetooth;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.coenelec390.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class BLE_MANAGER {
    Context context;
    Activity activity;
    private final BluetoothAdapter btAdapter;
    private final BluetoothLeScanner btScanner;
    private final BLE_STATE btState;
    BluetoothGatt gatt;
    private static final long SCAN_PERIOD = 10000;
    ArrayList<BLE_DEVICE> devices;
    BluetoothDevice peripheral;
    Handler bleHandler;
    private boolean peripheralAvailable = false;

    private Queue<Runnable> commandQueue = new LinkedList<>();
    private boolean commandQueueBusy = false;


    public BLE_MANAGER(Activity _activity) {
        context = _activity.getApplicationContext();
        activity = _activity;
        BluetoothManager bluetoothManager = (BluetoothManager) _activity.getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();


        btState = new BLE_STATE(context);
        bleHandler = new Handler(Looper.getMainLooper());

    }


    public void connectPeripheral() {
        if (!peripheralAvailable) {
            Utils.print("Peripheral not available");
            return;
        }
        // start the gatt connection
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }

        Utils.print("Peripheral available, attempting to connect devices");
        gatt = peripheral.connectGatt(context, false, gattCallback, TRANSPORT_LE);

        //TODO : do something some freakin chaching
    }

    ;
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            // TODO : implement error handling when devices are not connecting
//            if(status !==BluetoothGatt.GATT_SUCCESS)

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Utils.print("Connected to GATT server.");
                // Attempts to discover services after successful connection.
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                    return;
                }
                int bondState = peripheral.getBondState();
                //
                // If BOND_BONDING: bonding is in progress, don’t call discoverServices()
                if (bondState == BluetoothDevice.BOND_NONE || bondState == BOND_BONDED) {
                    int delayWhenBonded = 0;
                    //for some version need to
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                        delayWhenBonded = 1000;
                    }
                    final int delay = bondState == BOND_BONDED ? delayWhenBonded : 0;

                    Runnable discoveryServicesRunnable = new Runnable() {

                        @Override
                        public void run() {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions();
                                return;
                            }
                            boolean success = gatt.discoverServices();
                            if (!success) {
                                Utils.print("DiscoveryServiceRunnable :  discoverServices failed to start ");
                            }
                        }
                    };
                    bleHandler.postDelayed(discoveryServicesRunnable, delay);
                } else if (bondState == BOND_BONDING) {
                    Utils.print("Waiting for bonding to complete");
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Utils.print("Disconnected from GATT server.");
                gatt.close();
            }
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //TODO : clean up this mess
                UUID serviceUUID = UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
                UUID characteristicUUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");
                // get the ble gatt service
                BluetoothGattService service = gatt.getService(serviceUUID);
                if (service != null) {
                    //get the characteristic
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);

                    if (characteristic != null) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions();
                            return;
                        }
                        gatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);


                    } else {Utils.print("Characteristic not found");}
                } else {Utils.print("Service not found");}
//            List<BluetoothGattService> services = gatt.getServices();
//            for (BluetoothGattService service : services) {
//                Utils.print("Service found: " + service.getUuid().toString());
//                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
//                    Utils.print("-- Characteristic found: " + characteristic.getUuid().toString());
//                }
//            }
            } else {
                Utils.print("Service discovery failed with status: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // The data is contained in the characteristic's value
                byte[] data = characteristic.getValue();
                int value = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
                Utils.print("Value from esp32 : " + String.valueOf(value));
            } else {
                Utils.print("Failed to read characteristic");
            }
            completedCommand();
        }


        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
//            // Convert the byte array to a string
//            String stringValue = new String(value, StandardCharsets.UTF_8);
//            Utils.print("Value from esp32 : " + stringValue);
            boolean success = readCharacteristic(characteristic);
            if(success){
                nextCommand();
            }else {Utils.print("Failed to receive data");}

        }

        @Override
        public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status, @NonNull byte[] value) {
            super.onDescriptorRead(gatt, descriptor, status, value);
        }
    };

    public void startScan() {
        Utils.print("Scanning started");
        devices = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                    return;
                }

                //TODO : Implement scan with filters
//                String[] names = new String[]{"ESP32"};
//                List<ScanFilter> filters = null;
//                if(names != null) {
//                    filters = new ArrayList<>();
//                    for (String name : names) {
//                        ScanFilter filter = new ScanFilter.Builder()
//                                .setDeviceName(name)
//                                .build();
//                        filters.add(filter);
//                    }
//                }

                //TODO : Implement scan with specific settings
//                ScanSettings scanSettings = new ScanSettings.Builder()
//                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
//                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
//                        .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
//                        .setReportDelay(0L)
//                        .build();
//                scanner.startScan(filters, scanSettings, leScanCallback);
                btScanner.startScan(leScanCallback);

                // TODO : Add Match Mode
                //TODO : Add Caching
            }
        });
    }

    public boolean readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if (gatt == null) {
            Utils.print("ERROR: Gatt is 'null', ignoring read request");
            return false;
        }
        if (characteristic == null) {
            Utils.print("ERROR: Characteristic is 'null', ignoring read request");
            return false;
        }
        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) == 0) {
            Utils.print("ERROR: Characteristic cannot be read");
            return false;
        }
        return commandQueue.add(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                    return;
                }
                if (!gatt.readCharacteristic(characteristic)) {
//                    Utils.print(String.format("ERROR: readCharacteristic failed for characteristic: %s", characteristic.getUuid()));
                    completedCommand();
                } else {
//                    Utils.print(String.format("Reading characteristic <%s>", characteristic.getUuid()));
                    nextCommand();
                }
            }
        });
    }

    private void nextCommand() {
        if(commandQueueBusy) {
            return;
        }
        if (gatt == null) {
            Utils.print(String.format("ERROR: GATT is 'null' for peripheral '%s', clearing command queue", peripheral.getAddress()));
            commandQueue.clear();
            commandQueueBusy = false;
            return;
        }
        if (commandQueue.size() > 0) {
            final Runnable bluetoothCommand = commandQueue.peek();
            commandQueueBusy = true;
            bleHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bluetoothCommand.run();
                    } catch (Exception ex) {
                        Utils.print(String.format("ERROR: Command exception for device '%s'", peripheral.getAddress()));
                    }
                }
            });
        }
    }

    private void completedCommand() {
        commandQueueBusy = false;
        commandQueue.poll();
        nextCommand();
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
                String name = device.getName();
                String deviceInfo = "Device Name:  " + name + "  Address : " + device.getAddress() + " rssi : " + device.getRSSI() + "\n";
                Utils.print(deviceInfo);
                //make sure null string pass because it will have a null pointer exption
                if(name == null) continue;
                if(name.equals("ESP32")) {
                    peripheralAvailable = true;
                    peripheral = device.getDevice();
//                    break;
                }
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

        public void onScanFailed(int errorCode){
            //TODO : implement error handling when scan fails
            Utils.print("onScanFailed : scan failed");
            stopScan();
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
        return btAdapter != null && btAdapter.isEnabled();
    }

    public void requestPermissions() {
        int BLUETOOTH_PERMISSION_CODE = 1;
//        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permissions = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(activity, permissions, BLUETOOTH_PERMISSION_CODE);
    }
}
