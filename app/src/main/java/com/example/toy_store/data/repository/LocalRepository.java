package com.example.toy_store.data.repository;

import com.example.toy_store.data.api.ApiService;
import com.example.toy_store.data.model.Local;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class LocalRepository {
    private ApiService apiService;
    public LocalRepository(ApiService _apiService)
    {
        apiService = _apiService;
    }
    public void getLocals(Callback<List<Local>> callback){
        Call<List<Local> >call = apiService.getLocals();
        call.enqueue(callback);
    }
}
