package com.example.toy_store.service;

import com.example.toy_store.model.Product;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ProductService {
    String USERS = "Product";
    @Multipart
    @POST(USERS)
    Call<Void> addProduct(
            @Part("Name") RequestBody name,
            @Part("Description") RequestBody description,
            @Part("Price") RequestBody price,
            @Part("StockQuantity") RequestBody stockQuantity,
            @Part MultipartBody.Part image
    );
    @GET(USERS)
    Call<List<Product>> getProducts();
    @Multipart
    @PUT(USERS + "/{id}")
    Call<Void> updateProduct(
            @Path("id") int id,
            @Part("Name") RequestBody name,
            @Part("Description") RequestBody description,
            @Part("Price") RequestBody price,
            @Part("StockQuantity") RequestBody stockQuantity,
            @Part MultipartBody.Part image
    );
    @Multipart
    @PUT(USERS + "/{id}")
    Call<Void> updateWithoutImage(
            @Path("id") int id,
            @Part("Name") RequestBody name,
            @Part("Description") RequestBody description,
            @Part("Price") RequestBody price,
            @Part("StockQuantity") RequestBody stockQuantity
    );
    @DELETE(USERS + "/{id}")
    Call<Void> deleteProduct(@Path("id") int id);
}
