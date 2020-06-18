package com.colin.go4lunch.views;

import android.content.Context;
import android.graphics.Typeface;
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

public class WorkmateAdapter extends FirestoreRecyclerAdapter<User, UserHolder> {
    private Context context;
    private int version;
    public static final int WORKMATE_FRAGMENT_VERSION = 871;
    public static final int DETAIL_ACTIVITY_VERSION = 538;

    public WorkmateAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context, int version) {
        super(options);
        this.context = context;
        this.version = version;
    }


    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        configText(holder, model);
        if (model.getPhoto() != null) {
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

    private void configText(UserHolder holder, User model) {
        String workmateText = model.getName() + " ";
        if (version == WORKMATE_FRAGMENT_VERSION) {
            if (model.getSelectedPlaceId() == null || model.getSelectedPlaceId().equals("")) {
                workmateText += context.getString(R.string.user_not_decided);
                holder.workmateText.setTypeface(holder.workmateText.getTypeface(), Typeface.ITALIC);
                holder.workmateText.setTextColor(context.getResources().getColor(R.color.gray));
            } else {
                workmateText += context.getString(R.string.user_eating_at) + " " + model.getSelectedPlaceName();
                holder.workmateText.setTypeface(Typeface.DEFAULT);
                holder.workmateText.setTextColor(context.getResources().getColor(R.color.black));
            }
        } else {
            workmateText += context.getString(R.string.user_is_joining);
            holder.workmateText.setTypeface(Typeface.DEFAULT);
            holder.workmateText.setTextColor(context.getResources().getColor(R.color.black));
        }
        holder.workmateText.setText(workmateText);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmate_item, parent, false);
        return new UserHolder(v);
    }
}

class UserHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.workmate_img)
    ImageView workmateImage;

    @BindView(R.id.workmate_text)
    TextView workmateText;

    UserHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}

