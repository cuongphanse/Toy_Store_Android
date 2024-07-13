package com.example.toy_store.adapter;

import android.content.Context;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.R;
import com.example.toy_store.model.User;
import com.example.toy_store.service.UserService;
import com.example.toy_store.repository.UserRepository;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> mListUser;

    public UserAdapter(Context context, List<User> mListUser) {
        this.context = context;
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mListUser.get(position);
        if (user == null) {
            return;
        }
        holder.tvId.setText(user.getId());
        holder.tvLastName.setText(user.getLastName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvAddress.setText(user.getAddress());
        holder.tvPhone.setText(user.getPhone());
        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                        httpClient.addInterceptor(logging);

                        UserService userService = UserRepository.getUserService(httpClient.build());
                        Call<Void> call = userService.deleteUser(user.getId());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show();
                                    int adapterPosition = holder.getAdapterPosition();
                                    if (adapterPosition != RecyclerView.NO_POSITION) {
                                        mListUser.remove(adapterPosition);
                                        notifyItemRemoved(adapterPosition);
                                        notifyItemRangeChanged(adapterPosition, mListUser.size());
                                    }
                                } else {
                                    Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        if (mListUser != null) {
            return mListUser.size();
        }
        return 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvLastName, tvEmail, tvPhone, tvAddress;
        ImageView ivDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvLastName = itemView.findViewById(R.id.tvLastName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
