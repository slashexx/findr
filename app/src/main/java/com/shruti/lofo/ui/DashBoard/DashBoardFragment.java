package com.shruti.lofo.ui.DashBoard;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.shruti.lofo.LocalStore;
import com.shruti.lofo.R;
import com.shruti.lofo.databinding.FragmentDashboardBinding;
import com.shruti.lofo.ui.Found.FoundDetails;
import com.shruti.lofo.ui.Found.FoundItems;
import com.shruti.lofo.ui.Lost.LostDetails;
import com.shruti.lofo.ui.Lost.LostItems;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashBoardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private ArrayList<DashBoardViewModel> arr_recent_lofo;
    private RecyclerRecentLoFoAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //image slider
        ImageSlider imageSlider = root.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.dashboard_img1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.dashboard_img2, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        RecyclerView recentLostFoundList = root.findViewById(R.id.recent_lost_found_list);

        arr_recent_lofo = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false);
        recentLostFoundList.setLayoutManager(gridLayoutManager);
        adapter = new RecyclerRecentLoFoAdapter(requireContext(), arr_recent_lofo);
        recentLostFoundList.setAdapter(adapter);

        loadLocalData();

        adapter.setOnItemClickListener(new RecyclerRecentLoFoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DashBoardViewModel item) {
                String selectedItemName = item.getItemName();
                Intent intent;
                if(item.getTag().equalsIgnoreCase("lost")) {
                    intent = new Intent(requireContext(), LostDetails.class);
                    intent.putExtra("itemId", selectedItemName);
                }
                else{
                    intent = new Intent(requireContext(), FoundDetails.class);
                    intent.putExtra("itemId", selectedItemName);
                }
                startActivity(intent);
            }
        });

        // User Info from Local Preferences
        TextView userName = root.findViewById(R.id.userName);
        SharedPreferences loginPrefs = requireContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String currentUserEmail = loginPrefs.getString("currentUser", "");

        if (!currentUserEmail.isEmpty()) {
            SharedPreferences userPrefs = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String name = userPrefs.getString(currentUserEmail + "_name", "User");
            userName.setText(name);
        } else {
            userName.setText("Guest");
        }

        return root;
    }

    private void loadLocalData() {
        LocalStore localStore = new LocalStore(requireContext());
        List<LostItems> lostItems = localStore.getLostItems();
        List<FoundItems> foundItems = localStore.getFoundItems();
        
        arr_recent_lofo.clear();

        // Convert LostItems to DashBoardViewModel
        for (LostItems item : lostItems) {
             DashBoardViewModel model = new DashBoardViewModel(
                 item.getImageURI(),
                 item.getCategory(),
                 item.getDescription(),
                 item.getOwnerName(),
                 null, // finderName
                 "lost",
                 item.getDateLost(),
                 item.getItemName(),
                 null // dateFound
             );
             arr_recent_lofo.add(model);
        }

        // Convert FoundItems to DashBoardViewModel
        for (FoundItems item : foundItems) {
             DashBoardViewModel model = new DashBoardViewModel(
                 item.getImageURI(),
                 item.getCategory(),
                 item.getDescription(),
                 null, // ownerName
                 item.getfinderName(),
                 "found",
                 null, // dateLost
                 item.getItemName(),
                 item.getDateFound()
             );
             arr_recent_lofo.add(model);
        }

        // Sort by Date
        Collections.sort(arr_recent_lofo, (o1, o2) -> {
            String date1Str = o1.getDateLost() != null ? o1.getDateLost() : o1.getDateFound();
            String date2Str = o2.getDateLost() != null ? o2.getDateLost() : o2.getDateFound();
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Assuming format d/M/yyyy from fragment
            Date date1 = null;
            Date date2 = null;

            try {
                if (date1Str != null) date1 = dateFormat.parse(date1Str);
                if (date2Str != null) date2 = dateFormat.parse(date2Str);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date1 != null && date2 != null) {
                return date2.compareTo(date1); // Descending
            }
            return 0;
        });

        // Limit to 10
        if (arr_recent_lofo.size() > 10) {
            // Create a sublist or just remove elements
             ArrayList<DashBoardViewModel> subList = new ArrayList<>(arr_recent_lofo.subList(0, 10));
             arr_recent_lofo.clear();
             arr_recent_lofo.addAll(subList);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
