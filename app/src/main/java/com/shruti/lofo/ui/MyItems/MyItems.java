package com.shruti.lofo.ui.MyItems;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shruti.lofo.LocalStore;
import com.shruti.lofo.R;
import com.shruti.lofo.databinding.FragmentLostBinding;
import com.shruti.lofo.ui.Found.FoundItems;
import com.shruti.lofo.ui.Found.FoundItemsAdapter;
import com.shruti.lofo.ui.Lost.LostItems;
import com.shruti.lofo.ui.Lost.LostItemsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyItems extends Fragment {

    private FragmentLostBinding binding;
    private LostItemsAdapter lostAdapter;
    private FoundItemsAdapter foundAdapter;
    private String currentUserEmail;

    private RecyclerView lostRecyclerView;
    private RecyclerView foundRecyclerView;
    private FloatingActionButton add;
    TextView filterButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get Local User
        if (getContext() != null) {
            SharedPreferences loginPrefs = getContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
            currentUserEmail = loginPrefs.getString("currentUser", "");
        }

        lostRecyclerView = root.findViewById(R.id.lostRecyclerView);
        foundRecyclerView = root.findViewById(R.id.foundRecyclerView);
        add = root.findViewById(R.id.add_lost);
        filterButton = root.findViewById(R.id.filterButton);

        setupRecyclerView(true);

        return root;
    }

    void setupRecyclerView(boolean showDeleteButton) {
        if (add != null) add.setVisibility(View.GONE);
        if (filterButton != null) {
            filterButton.setText("My LoFo!");
            filterButton.setTextSize(24);
        }

        LocalStore localStore = new LocalStore(requireContext());

        // Filter Lost Items
        List<LostItems> allLost = localStore.getLostItems();
        List<LostItems> myLost = new ArrayList<>();
        if (allLost != null) {
            for (LostItems item : allLost) {
                if (item.getUserId() != null && item.getUserId().equals(currentUserEmail)) {
                    myLost.add(item);
                }
            }
        }

        if (lostRecyclerView != null) {
            lostRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            lostAdapter = new LostItemsAdapter(requireContext(), myLost, showDeleteButton);
            lostRecyclerView.setAdapter(lostAdapter);
        }

        // Filter Found Items
        List<FoundItems> allFound = localStore.getFoundItems();
        List<FoundItems> myFound = new ArrayList<>();
        if (allFound != null) {
            for (FoundItems item : allFound) {
                if (item.getfinderId() != null && item.getfinderId().equals(currentUserEmail)) {
                    myFound.add(item);
                }
            }
        }

        if (foundRecyclerView != null) {
            foundRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            foundAdapter = new FoundItemsAdapter(requireContext(), myFound, showDeleteButton);
            foundRecyclerView.setAdapter(foundAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView(true); // Refresh list on resume
    }
}
