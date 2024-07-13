package com.example.toy_store.data.api;

import com.example.toy_store.data.model.Local;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("Map")
    Call<List<Local>> getLocals();

}
