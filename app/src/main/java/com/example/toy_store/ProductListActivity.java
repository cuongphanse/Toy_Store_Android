package com.example.toy_store;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.adapter.ProductAdapter;
import com.example.toy_store.model.Product;
import com.example.toy_store.repository.ProductRepository;
import com.example.toy_store.service.ProductService;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    public static final int UPDATE_PRODUCT_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadProductData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // loadProductData(); // No longer needed here
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
                    productAdapter = new ProductAdapter(ProductListActivity.this, productList);
                    recyclerView.setAdapter(productAdapter);
                } else {
                    Toast.makeText(ProductListActivity.this, "Failed to retrieve products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_PRODUCT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Reload the product data when returning from the UpdateProductActivity
            loadProductData();
        }
    }
}
