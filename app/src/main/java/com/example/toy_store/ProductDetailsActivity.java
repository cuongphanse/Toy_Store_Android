package com.example.toy_store;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.toy_store.model.Product;
import com.example.toy_store.repository.CartRepository;
import com.example.toy_store.service.CartService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imgProduct,imgBack;
    private TextView tvProductName, tvProductBrand, tvProductPrice, tvProductDescription, tvProductId, tvQuantity;
    private Button btnDecreaseQuantity, btnIncreaseQuantity, btnAddToCart;
    private int quantity = 1;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        imgProduct = findViewById(R.id.img_product);
        imgBack = findViewById(R.id.img_back);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductBrand = findViewById(R.id.tv_product_brand);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductDescription = findViewById(R.id.tv_product_description);
        tvProductId = findViewById(R.id.tv_product_id);
        tvQuantity = findViewById(R.id.tv_quantity);
        btnDecreaseQuantity = findViewById(R.id.btn_decrease_quantity);
        btnIncreaseQuantity = findViewById(R.id.btn_increase_quantity);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, ShopActivity.class);
                startActivity(intent);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_cart) {
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.menu_person) {
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.menu_home) {
                    return true; // Stay on the current activity
                }
                return false; // Return false for other unhandled cases
            }
        });

        // Retrieve userId from shared preferences
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        userId = sharedPref.getString("userId", null);

        // Get the passed product object and product ID
        Product product = (Product) getIntent().getSerializableExtra("product");
        int productId = getIntent().getIntExtra("product_id", -1);

        if (product != null) {
            tvProductName.setText("Name: " + product.getName());
            tvProductBrand.setText("Stock: " + product.getStockQuantity()); // Assuming description is used for brand
            tvProductPrice.setText("$" + product.getPrice());
            tvProductDescription.setText(product.getDescription());
            Glide.with(this).load(product.getUrlImage()).into(imgProduct);
        } else {
            Log.e("ProductDetailsActivity", "Product is null");
        }

        if (productId != -1) {
            tvProductId.setText(String.valueOf(productId));
        } else {
            Log.e("ProductDetailsActivity", "Product ID is null");
        }

        btnDecreaseQuantity.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnIncreaseQuantity.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        btnAddToCart.setOnClickListener(v -> addToCart(productId, quantity));
    }

    private void addToCart(int productId, int quantity) {
        if (userId == null) {
            Toast.makeText(ProductDetailsActivity.this, "User ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        CartService cartService = CartRepository.getCartService(httpClient.build());
        Call<Void> call = cartService.addToCart(userId, productId, quantity);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductDetailsActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
