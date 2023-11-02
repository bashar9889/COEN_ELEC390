package com.example.coenelec390.ui.item;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.coenelec390.databinding.ActivityItemsBinding;

public class ItemFragment extends Fragment {//TODO: To remove

    private ActivityItemsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ItemViewModel itemViewModel =
                new ViewModelProvider(this).get(ItemViewModel.class);

        binding = ActivityItemsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textItem;
        itemViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}