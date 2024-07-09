package com.example.toy_store;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.adapter.CartAdapter;
import com.example.toy_store.model.Cart;
import com.example.toy_store.repository.CartRepository;
import com.example.toy_store.service.CartService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartAdapterListener{

    private RecyclerView recyclerView;
    private Button btnCheckout;
    private CartService cartService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        initServiceClient();
        loadCartItems();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_cart_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(v -> checkout());
    }

    private void initServiceClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        cartService = CartRepository.getCartService(httpClient.build());
    }

    private void loadCartItems() {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Cart[]> call = cartService.getCart(userId);
        call.enqueue(new Callback<Cart[]>() {
            @Override
            public void onResponse(Call<Cart[]> call, Response<Cart[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cart> carts = new ArrayList<>(Arrays.asList(response.body())); // Convert to ArrayList
                    CartAdapter adapter = new CartAdapter(CartActivity.this, carts, CartActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(CartActivity.this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Cart[]> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkout() {
        Toast.makeText(this, "Checkout process started...", Toast.LENGTH_SHORT).show();
        // Integrate checkout logic according to your business requirements.
    }

    @Override
    public void onCartDataChanged() {
        loadCartItems();
    }
}
