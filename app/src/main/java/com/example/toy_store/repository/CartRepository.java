package com.example.toy_store.repository;

import com.example.toy_store.api.APIClient;
import com.example.toy_store.service.CartService;
import com.example.toy_store.service.ProductService;

import okhttp3.OkHttpClient;

public class CartRepository {
    public static CartService getCartService(OkHttpClient client) {
        return APIClient.getClient(client).create(CartService.class);
    }
}
