package com.example.toy_store.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static String baseURL = "https://d4c2-118-69-182-149.ngrok-free.app/api/";
    private static Retrofit retrofit;
    //change git

    public static Retrofit getClient(OkHttpClient client) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)  // Set the custom OkHttpClient here
                    .build();
        }
        return retrofit;
    }
}
