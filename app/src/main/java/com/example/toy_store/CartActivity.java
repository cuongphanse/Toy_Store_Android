package com.example.toy_store;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.adapter.CartAdapter;
import com.example.toy_store.data.api.RetrofitClient;
import com.example.toy_store.data.model.PaymentResponse;
import com.example.toy_store.data.repository.PaymentRepository;
import com.example.toy_store.model.Cart;
import com.example.toy_store.repository.CartRepository;
import com.example.toy_store.service.CartService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartAdapterListener {

    private RecyclerView recyclerView;
    private Button btnCheckout;
    private TextView totalMoney;
    private CartService cartService;
    private double totalAmount = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_cart);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_cart) {
                    return true;
                }
                if (id == R.id.menu_person) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.menu_home) {
                    startActivity(new Intent(getApplicationContext(), ShopActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false; // Return false for other unhandled cases
            }
        });

        initViews();
        initServiceClient();
        loadCartItems();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_cart_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnCheckout = findViewById(R.id.btn_checkout);
        totalMoney = findViewById(R.id.total_money);
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
                    updateTotalMoney(carts);
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

    private void updateTotalMoney(List<Cart> cartList) {
        totalAmount = 0;  // Reset total amount
        for (Cart cart : cartList) {
            totalAmount += cart.getPrice() * cart.getQuantity();
        }
        totalMoney.setText(String.format("$%.2f", totalAmount));
    }

    private void checkout() {
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Integer> call = RetrofitClient.getApiService().checkout(userId);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int orderId = response.body();
                    Toast.makeText(CartActivity.this, "Order successful. Order ID: " + orderId, Toast.LENGTH_LONG).show();

                    // Assuming you want to initiate a payment process after checkout
                    createPayment( (float)totalAmount, String.valueOf(orderId), "vn");

                    // Clear the cart items in the UI
                    recyclerView.setAdapter(null);
                    totalMoney.setText("$0.00");
                } else {
                    Toast.makeText(CartActivity.this, "Checkout failed.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(CartActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCartDataChanged() {
        loadCartItems();
    }
    private void createPayment(float amount, String orderid, String locale) {
        PaymentRepository paymentRepository = new PaymentRepository(RetrofitClient.getPaymentApiService());
        paymentRepository.createPayment(amount, orderid, locale, new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String paymentUrl = response.body().getPaymentUrl();
                    openBrowserInApp(paymentUrl);
                } else {
                    Log.e("Payment", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.e("Payment", "Failed to create payment", t);
            }
        });
    }
    private void openBrowserInApp(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}
