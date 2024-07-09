package com.example.toy_store.service;

import com.example.toy_store.model.LoginResponse;
import com.example.toy_store.model.RegisterRequest;
import com.example.toy_store.model.User;
import com.example.toy_store.model.UserSignIn;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface UserService {
    String USERS = "Authenticate";
    @GET(USERS + "/getAllUsers")
    Call<User[]> getAllUsers();
    @POST(USERS + "/signUp")
    Call<Void> registerUser(@Body RegisterRequest registerRequest);

    @POST(USERS + "/signIn")
    Call<LoginResponse> login(@Body UserSignIn model);
}
