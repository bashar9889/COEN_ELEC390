package com.example.coenelec390;

import com.example.coenelec390.ui.item.Item;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface API {
    @GET("product/")
    Call<List<Item>> getProducts();

    @POST("product/add/")
    Call<ResponseBody> addProduct(@Body Item p);

    @POST("product/update/{id}")
    Call<ResponseBody> updateProduct(@Path("id") long productId, @Body Item p);
}
