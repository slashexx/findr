package com.shruti.lofo.ui.Found;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shruti.lofo.LocalStore;
import com.shruti.lofo.R;
import com.shruti.lofo.databinding.FragmentFoundBinding;

import java.util.ArrayList;
import java.util.List;

public class FoundFragment extends Fragment {

    private FragmentFoundBinding binding;
    FloatingActionButton addBtn;
    RecyclerView recyclerView;
    FoundItemsAdapter adapter;
    TextView filter;
    String selectedCategory = "";
    private List<FoundItems> allItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFoundBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.foundRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        adapter = new FoundItemsAdapter(requireContext(), new ArrayList<>(), false);
        recyclerView.setAdapter(adapter);

        addBtn = root.findViewById(R.id.add_found);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoundItemsFragment dialogFragment = new FoundItemsFragment();
                dialogFragment.show(getParentFragmentManager(), "form_dialog");
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
        allItems = localStore.getFoundItems();
        filterList();
    }

    private void filterList() {
        if (selectedCategory.isEmpty() || selectedCategory.equals("All")) {
             adapter.setItems(allItems);
        } else {
            List<FoundItems> filteredList = new ArrayList<>();
            for (FoundItems item : allItems) {
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
        loadData();
    }
}
