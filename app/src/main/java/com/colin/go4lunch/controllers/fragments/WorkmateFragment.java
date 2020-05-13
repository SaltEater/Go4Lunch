package com.colin.go4lunch.controllers.fragments;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseFragment;
import com.colin.go4lunch.models.User;
import com.colin.go4lunch.views.WorkmateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import butterknife.BindView;

public class WorkmateFragment extends BaseFragment {
    @BindView(R.id.workmates_recycler_view)
    RecyclerView workmateRecyclerView;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference users = db.collection("Users");
    private WorkmateAdapter workmatesAdapter;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_workmates;
    }

    @Override
    protected void configureFragment() {
        setUpWorkmatesRecyclerView();
    }

    public WorkmateFragment() {
    }

    public static WorkmateFragment newInstance() {
        return new WorkmateFragment();
    }

    public void setUpWorkmatesRecyclerView() {
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(users, User.class)
                .setLifecycleOwner(this)
                .build();

        workmatesAdapter = new WorkmateAdapter(options, getContext());
        workmateRecyclerView.setHasFixedSize(true);
        workmateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workmateRecyclerView.setAdapter(workmatesAdapter);
    }
}
