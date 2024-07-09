package com.example.toy_store.repository;

import com.example.toy_store.api.APIClient;
import com.example.toy_store.service.ProductService;

import okhttp3.OkHttpClient;

public class ProductRepository {
    public static ProductService getUserService(OkHttpClient client) {
        return APIClient.getClient(client).create(ProductService.class);
    }
}
