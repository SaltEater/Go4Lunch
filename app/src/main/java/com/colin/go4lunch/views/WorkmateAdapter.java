package com.colin.go4lunch.views;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.colin.go4lunch.R;
import com.colin.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<User, WorkmateAdapter.UserHolder> {
    private Context context;

    public WorkmateAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.workmateText.setText(model.getName());
        if (model.getPhoto() != null){
            Glide.with(context)
                    .load(model.getPhoto())
                    .circleCrop()
                    .into(holder.workmateImage);
        } else {
            Glide.with(context)
                    .load(R.mipmap.ic_launcher_round)
                    .circleCrop()
                    .into(holder.workmateImage);
        }
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmate_item, parent, false);
        return new UserHolder(v);
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.workmate_img)
        ImageView workmateImage;

        @BindView(R.id.workmate_text)
        TextView workmateText;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
