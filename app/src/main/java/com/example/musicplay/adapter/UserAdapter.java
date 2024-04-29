package com.example.musicplay.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplay.domain.OnItemClickListener;
import com.example.musicplay.domain.User;
import com.example.musicplayer.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> mUserList;
    private OnItemClickListener mListener;

    public UserAdapter(List<User> userList) {
        mUserList = userList;
    }

    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserAdapter.UserViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(UserAdapter.UserViewHolder holder, int position) {
        User user = mUserList.get(position);
        String pos = String.valueOf(position);
        holder.mFirstName.setText(user.getFirst_name());
        holder.mLastName.setText(user.getLast_name());
        holder.mEmail.setText(user.getEmail());
        holder.mPhone.setText(user.getPhone());
        //holder.mPassword.setText(user.getPassword());
        holder.tvNumber.setText(pos);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        public TextView tvNumber;
        public TextView mFirstName;
        public TextView mLastName;
        public TextView mPhone;
        //        public TextView mPassword;
        public TextView mEmail;

        public UserViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mFirstName = itemView.findViewById(R.id.tvFirstName);
            mLastName = itemView.findViewById(R.id.tvLastName);
            mPhone = itemView.findViewById(R.id.tvPhone);
            mEmail = itemView.findViewById(R.id.tvEmail);
            tvNumber = itemView.findViewById(R.id.tvNumber);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
