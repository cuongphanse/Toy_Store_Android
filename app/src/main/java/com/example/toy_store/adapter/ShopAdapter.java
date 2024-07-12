package com.example.toy_store.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.toy_store.ProductDetailsActivity;
import com.example.toy_store.R;
import com.example.toy_store.model.Product;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    public void setFilterProduct(List<Product> filterProduct){
        this.productList = filterProduct;
        notifyDataSetChanged();
    }

    public ShopAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_user_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvProductBrand.setText(product.getDescription()); // Assuming description is used for brand
        holder.tvProductPrice.setText("$" + product.getPrice());
        Glide.with(context).load(product.getUrlImage()).into(holder.imgProduct);

        // Store the product ID in a hidden field
        holder.itemView.setTag(product.getId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductBrand, tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductBrand = itemView.findViewById(R.id.tv_product_brand);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}
