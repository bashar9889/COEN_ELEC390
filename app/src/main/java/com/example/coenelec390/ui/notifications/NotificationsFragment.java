package com.example.coenelec390.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coenelec390.R;
import com.example.coenelec390.bluetooth.BLE_MANAGER;
import com.example.coenelec390.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private BLE_MANAGER bleManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        bleManager = new BLE_MANAGER(getActivity());

        Button btnOn = root.findViewById(R.id.btnOn);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bleManager.hasBluetooth())  bleManager.enableBluetooth();
            }
        });

        Button btnOff = root.findViewById(R.id.btnOff);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bleManager.hasBluetooth())  bleManager.disableBluetooth();
            }
        });
        Button btnScan = root.findViewById(R.id.scan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bleManager.hasBluetooth())  bleManager.enableBluetooth();
//                startScanning();
            }
        });


        Button stopScan = root.findViewById(R.id.stopScan);
        stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}