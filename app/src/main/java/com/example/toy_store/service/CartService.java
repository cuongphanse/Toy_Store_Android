package com.example.toy_store.service;

import com.example.toy_store.model.Cart;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CartService {
    String CART = "Cart";
    @GET(CART + "/{userId}")
    Call<Cart[]> getCart(@Path("userId") String userId);
    @POST(CART + "/addToCart")
    Call<Void> addToCart(
            @Query("userId") String userId,
            @Query("productId") int productId,
            @Query("quantity") int quantity
    );
    @PUT(CART + "/updateQuantity")
    Call<Void> updateCart(
            @Query("userId") String userId,
            @Query("productId") int productId,
            @Query("quantity") int quantity
    );
    @DELETE(CART + "/deleteFromCart")
    Call<Void> deleteCart(
            @Query("userId") String userId,
            @Query("productId") int productId
    );
}
