package com.example.toy_store;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.adapter.ShopAdapter;
import com.example.toy_store.model.Product;
import com.example.toy_store.repository.ProductRepository;
import com.example.toy_store.service.ProductService;
import com.example.toy_store.view.MapFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
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
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        searchView = findViewById(R.id.searchview);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        recyclerView = findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.menu_cart) {
                    getSupportFragmentManager().popBackStack();
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.menu_person) {
                    getSupportFragmentManager().popBackStack();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.menu_home) {

                    findViewById(R.id.searchview).setVisibility(View.VISIBLE);
                    findViewById(R.id.img_banner).setVisibility(View.VISIBLE);
                    findViewById(R.id.tv_name).setVisibility(View.VISIBLE);
                    findViewById(R.id.recycler_view_products).setVisibility(View.VISIBLE);
                    getSupportFragmentManager().popBackStack();
                    return true; // Stay on the current activity
                }
                return false; // Return false for other unhandled cases
            }

        });
        ImageButton btnMap = findViewById(R.id.btn_Map);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.searchview).setVisibility(View.GONE);
                findViewById(R.id.img_banner).setVisibility(View.GONE);
                findViewById(R.id.tv_name).setVisibility(View.GONE);
                findViewById(R.id.recycler_view_products).setVisibility(View.GONE);
                loadFragment(new MapFragment());
            }
        });
        loadProductData();
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void filterList(String text) {
        List<Product> filterList = new ArrayList<>();
        for (Product item : productList){
            if(item.getName().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }
        if(filterList.isEmpty()){
            Toast.makeText(this,"No product found",Toast.LENGTH_SHORT).show();
        }else {
            shopAdapter.setFilterProduct(filterList);
        }
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
