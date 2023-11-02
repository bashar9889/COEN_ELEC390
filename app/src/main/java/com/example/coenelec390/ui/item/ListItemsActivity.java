package com.example.coenelec390.ui.item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.coenelec390.API;
import com.example.coenelec390.R;
import com.example.coenelec390.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListItemsActivity extends AppCompatActivity{
    private RecyclerView revProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        API api = RetrofitClient.getInstance().getAPI();
        Call<List<Item>> call = api.getProducts();

        call.enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ListItemsActivity.this, response.code() + "", Toast.LENGTH_LONG).show();
                    return;
                }

                List<Item> products = response.body();
                revProducts = findViewById(R.id.revProducts);
                revProducts.setLayoutManager(new LinearLayoutManager(ListItemsActivity.this));
                revProducts.setAdapter(new ItemAdapter(ListItemsActivity.this, products));
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Toast.makeText(ListItemsActivity.this, t.getMessage() + "", Toast.LENGTH_LONG).show();
            }
        });
    }
}
