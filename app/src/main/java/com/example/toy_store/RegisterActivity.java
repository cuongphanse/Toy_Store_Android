package com.example.toy_store;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toy_store.adapter.UserAdapter;
import com.example.toy_store.model.RegisterRequest;
import com.example.toy_store.api.APIClient;
import com.example.toy_store.repository.UserRepository;
import com.example.toy_store.service.UserService;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etUserName, etPassword, etAddress, etBirthday, etPhone;
    private Button btnRegister;
    TextView btnChangeLogin;
    private UserAdapter userAdapter;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        etAddress = findViewById(R.id.etAddress);
        etBirthday = findViewById(R.id.etBirthday);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        btnChangeLogin = findViewById(R.id.tv_changeLogin);

        btnChangeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String userName = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String birthday = etBirthday.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean isAdmin = false; // isAdmin luôn là false

        // Validate input fields
        if (!validatePassword(password)) {
            Toast.makeText(this, "Password must be at least 8 characters long, contain an uppercase letter, and a special character.", Toast.LENGTH_LONG).show();
            return;
        }

        if (!validateBirthday(birthday)) {
            Toast.makeText(this, "Birthday must be in the format YYYY-MM-DD.", Toast.LENGTH_LONG).show();
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest(firstName, lastName, email, userName, password, address, birthday, phone, isAdmin);

        OkHttpClient client = new OkHttpClient(); // Create your OkHttpClient instance
        userService = UserRepository.getUserService(client);

        Call<Void> call = userService.registerUser(registerRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Redirect to MainActivity
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close RegisterActivity so user can't go back using back button
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("RegisterActivity", "Error registering user", t);
                Toast.makeText(RegisterActivity.this, "Error registering user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validatePassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$&*]).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean validateBirthday(String birthday) {
        String birthdayPattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return birthday.matches(birthdayPattern);
    }

}
