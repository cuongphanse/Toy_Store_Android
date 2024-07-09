package com.example.toy_store;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.adapter.ProductAdapter;
import com.example.toy_store.adapter.ShopAdapter;
import com.example.toy_store.model.Product;
import com.example.toy_store.repository.ProductRepository;
import com.example.toy_store.service.ProductService;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShopAdapter shopAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadProductData();
    }

    private void loadProductData() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        ProductService productService = ProductRepository.getUserService(httpClient.build());

        Call<List<Product>> call = productService.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    shopAdapter = new ShopAdapter(ShopActivity.this, productList);
                    recyclerView.setAdapter(shopAdapter);
                } else {
                    Toast.makeText(ShopActivity.this, "Failed to retrieve products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ShopActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
