package com.example.toy_store;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.adapter.UserAdapter;
import com.example.toy_store.model.User;
import com.example.toy_store.repository.UserRepository;
import com.example.toy_store.service.UserService;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private RecyclerView rcvUsers;
    private UserAdapter userAdapter;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        rcvUsers = findViewById(R.id.rcv_user);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvUsers.setLayoutManager(linearLayoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvUsers.addItemDecoration(itemDecoration);

        OkHttpClient client = new OkHttpClient(); // Create your OkHttpClient instance
        userService = UserRepository.getUserService(client);

        // Call API method to fetch users
        getAllUsersFromApi();
    }

    private void getAllUsersFromApi() {
        Call<User[]> call = userService.getAllUsers();
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Call<User[]> call, Response<User[]> response) {
                if (response.isSuccessful()) {
                    User[] users = response.body();
                    if (users != null) {
                        List<User> userList = Arrays.asList(users);
                        userAdapter = new UserAdapter(userList);
                        rcvUsers.setAdapter(userAdapter);
                    } else {
                        Toast.makeText(UserActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User[]> call, Throwable t) {
                Log.e("API Call", "Error fetching users", t);
                Toast.makeText(UserActivity.this, "Error fetching users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
