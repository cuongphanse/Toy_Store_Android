package com.example.toy_store.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.toy_store.api.APIClient;
public class RetrofitClient {
    private static final String BASE_URL = APIClient.baseURL;
    private static Retrofit retrofit;

    // Singleton instance of Retrofit
    private static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // ApiService instance
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    // PaymentApiService instance
    public static PaymentApiService getPaymentApiService() {
        return getClient().create(PaymentApiService.class);
    }
}
