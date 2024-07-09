package com.example.toy_store.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toy_store.R;
import com.example.toy_store.model.Cart;
import com.example.toy_store.repository.CartRepository;
import com.example.toy_store.service.CartService;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<Cart> cartList;
    private CartAdapterListener listener;

    public interface CartAdapterListener {
        void onCartDataChanged();
    }

    public CartAdapter(Context context, List<Cart> cartList, CartAdapterListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.tvProductName.setText(cart.getName());
        holder.tvProductPrice.setText(String.format("$%.2f", cart.getPrice()));
        holder.tvQuantity.setText(String.valueOf(cart.getQuantity()));
        Glide.with(context).load(cart.getUrlImage()).into(holder.imageProduct);

        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (currentQuantity > 1) {
                updateCartQuantity(cart.getUserId(), cart.getProductId(), currentQuantity - 1);
            } else {
                confirmDeletion(cart, position);
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            updateCartQuantity(cart.getUserId(), cart.getProductId(), currentQuantity + 1);
        });
    }

    private void updateCartQuantity(String userId, int productId, int newQuantity) {
        CartService cartService = CartRepository.getCartService(new OkHttpClient.Builder().build());
        Call<Void> call = cartService.updateCart(userId, productId, newQuantity);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    if (listener != null) {
                        listener.onCartDataChanged();
                    }
                } else {
                    Toast.makeText(context, "Failed to update cart quantity", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeletion(Cart cart, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Do you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProduct(cart.getUserId(), cart.getProductId(), position))
                .setNegativeButton("No", (dialog, which) -> {
                    cart.setQuantity(1);
                    notifyItemChanged(position);
                })
                .show();
    }

    private void deleteProduct(String userId, int productId, int position) {
        CartService cartService = CartRepository.getCartService(new OkHttpClient.Builder().build());
        Call<Void> call = cartService.deleteCart(userId, productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    ((Activity) context).runOnUiThread(() -> {
                        cartList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartList.size());
                    });
                    if (listener != null) {
                        listener.onCartDataChanged();
                    }
                } else {
                    Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView tvProductName, tvProductPrice, tvQuantity;
        ImageView btnDecrease, btnIncrease;

        public ViewHolder(View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.image_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
        }
    }
}
