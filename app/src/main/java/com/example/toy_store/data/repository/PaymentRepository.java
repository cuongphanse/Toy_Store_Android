package com.example.toy_store.data.repository;

import com.example.toy_store.data.api.PaymentApiService;
import com.example.toy_store.data.model.PaymentResponse;

import retrofit2.Call;
import retrofit2.Callback;

public class PaymentRepository {
    private PaymentApiService paymentApiService;

    public PaymentRepository(PaymentApiService _paymentApiService) {
        paymentApiService = _paymentApiService;
    }

    public void createPayment(float amount, String orderDescription, String locale, Callback<PaymentResponse> callback) {
        Call<PaymentResponse> call = paymentApiService.createPayment(amount, orderDescription, locale);
        call.enqueue(callback);
    }
}
