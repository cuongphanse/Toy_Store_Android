package com.example.toy_store.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toy_store.R;
import com.example.toy_store.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<User> mListUser;

    public UserAdapter(List<User> mListUser) {
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mListUser.get(position);
        if(user == null){
            return;
        }
        holder.tvId.setText(user.getId());
        holder.tvLastName.setText(user.getLastName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvAddress.setText(user.getAddress());
        holder.tvPhone.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        if(mListUser != null){
            return mListUser.size();
        }
        return 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLastName;
        private final TextView tvEmail;
        private final TextView tvPhone;
        private final TextView tvAddress;
        private final TextView tvId;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvLastName = itemView.findViewById(R.id.tvLastName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }
}
