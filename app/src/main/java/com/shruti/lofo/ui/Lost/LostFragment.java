package com.shruti.lofo.ui.Lost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shruti.lofo.LocalStore;
import com.shruti.lofo.R;
import com.shruti.lofo.databinding.FragmentLostBinding;

import java.util.ArrayList;
import java.util.List;

public class LostFragment extends Fragment {

    private FragmentLostBinding binding;
    FloatingActionButton addBtn;
    RecyclerView recyclerView;
    LostItemsAdapter adapter;
    TextView filter;
    String selectedCategory = "";
    private List<LostItems> allItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LostViewModel lostViewModel =
                new ViewModelProvider(this).get(LostViewModel.class);

        binding = FragmentLostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.lostRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new LostItemsAdapter(requireContext(), new ArrayList<>(), false);
        recyclerView.setAdapter(adapter);

        addBtn = root.findViewById(R.id.add_lost);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LostItemsFragment dialogFragment = new LostItemsFragment();
                dialogFragment.show(getParentFragmentManager(), "form_dialog");
                // Ideally, we should refresh the list after the dialog closes.
                // DialogFragment doesn't easily callback on dismiss, but onResume might handle it.
            }
        });

        filter = root.findViewById(R.id.filterButton);
        Spinner categorySpinner = root.findViewById(R.id.categorySpinner);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categorySpinner.getVisibility() == View.VISIBLE) {
                    categorySpinner.setVisibility(View.GONE);
                } else {
                    categorySpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                filterList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
                filterList();
            }
        });

        loadData();

        return root;
    }

    private void loadData() {
        LocalStore localStore = new LocalStore(requireContext());
        allItems = localStore.getLostItems();
        filterList();
    }

    private void filterList() {
        if (selectedCategory.isEmpty() || selectedCategory.equals("All")) { // Assuming "All" might be an option or just empty logic
             adapter.setItems(allItems);
        } else {
            List<LostItems> filteredList = new ArrayList<>();
            for (LostItems item : allItems) {
                if (item.getCategory() != null && item.getCategory().equalsIgnoreCase(selectedCategory)) {
                    filteredList.add(item);
                }
            }
            adapter.setItems(filteredList);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Refresh list when returning from add dialog or details
    }
}
