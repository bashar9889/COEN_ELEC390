package com.example.coenelec390;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import java.util.HashMap;
import java.util.Map;
import com.example.coenelec390.databinding.ActivityMainBinding;
import com.example.coenelec390.db_manager.Component;
import com.example.coenelec390.db_manager.DatabaseManager;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard, R.id.navigation_items, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        DatabaseManager dbManager = new DatabaseManager();
        Map<String, String> characteristics5 = new HashMap<>();
        characteristics5.put("capacitance", "100uF");
        Component capacitor1 = new Component(characteristics5, "Shelf E", 100);
        dbManager.addComponent("pull_req", "demo", "BASHAR", capacitor1);
        Toast.makeText(MainActivity.this, "demo", Toast.LENGTH_SHORT).show();





    }

}