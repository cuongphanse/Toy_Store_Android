package com.example.toy_store.repository;

import com.example.toy_store.api.APIClient;
import com.example.toy_store.service.UserService;

import okhttp3.OkHttpClient;

public class UserRepository {
    public static UserService getUserService(OkHttpClient client) {
        return APIClient.getClient(client).create(UserService.class);
    }
}
