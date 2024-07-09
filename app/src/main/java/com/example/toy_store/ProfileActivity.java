package com.example.toy_store;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvLogout,tvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvLogout = findViewById(R.id.tv_logout);
        tvInfo = findViewById(R.id.tv_info);
        tvInfo.setOnClickListener(v -> info());
        tvLogout.setOnClickListener(v -> logout());
    }

    private void info(){
        Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
        startActivity(intent);
        finish();
    }
    private void logout() {
        // Clear the saved user ID
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("userId");
        editor.apply();

        // Confirm logout to user
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

        // Navigate back to LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();  // Ensure this activity is closed so it's removed from the back stack
    }
}
