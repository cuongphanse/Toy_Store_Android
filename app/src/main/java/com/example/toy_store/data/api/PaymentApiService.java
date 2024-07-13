package com.example.toy_store.data.api;

import com.example.toy_store.data.model.PaymentResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PaymentApiService {
    @POST("Payment/create-payment")
    Call<PaymentResponse> createPayment(
            @Query("amount") float amount,
            @Query("orderid") String orderDescription,
            @Query("locale") String locale
    );
}
