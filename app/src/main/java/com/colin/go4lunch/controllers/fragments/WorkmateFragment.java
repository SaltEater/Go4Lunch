package com.colin.go4lunch.controllers.fragments;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseFragment;
import com.colin.go4lunch.models.User;
import com.colin.go4lunch.utils.UserHelper;
import com.colin.go4lunch.views.WorkmateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import butterknife.BindView;

public class WorkmateFragment extends BaseFragment {
    @BindView(R.id.workmates_recycler_view)
    RecyclerView workmateRecyclerView;

    private CollectionReference users = UserHelper.getUserCollection();

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_workmates;
    }

    @Override
    protected void configureFragment() {
        setUpWorkmatesRecyclerView();
    }

    private WorkmateFragment() {
    }

    public static WorkmateFragment newInstance() {
        return new WorkmateFragment();
    }

    private void setUpWorkmatesRecyclerView() {
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(users, User.class)
                .setLifecycleOwner(this)
                .build();

        WorkmateAdapter workmatesAdapter = new WorkmateAdapter(options, getContext(), WorkmateAdapter.WORKMATE_FRAGMENT_VERSION);
        workmateRecyclerView.setHasFixedSize(true);
        workmateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        workmateRecyclerView.setAdapter(workmatesAdapter);
    }
}
