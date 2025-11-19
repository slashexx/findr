package com.shruti.lofo.ui.MyProfile;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.shruti.lofo.R;

public class MyProfileFragment extends Fragment {
    TextView profileName, profileEmail, profilePhone, titleName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_profile, container, false);
        profileName = root.findViewById(R.id.profileName);
        profileEmail = root.findViewById(R.id.profileEmail);
        profilePhone = root.findViewById(R.id.profilephone);
        titleName = root.findViewById(R.id.titlename);

        fetchUserData();
        return root;
    }

    private void fetchUserData() {
        if (getContext() == null) return;

        SharedPreferences loginPrefs = getContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String currentUserEmail = loginPrefs.getString("currentUser", "");

        if (!currentUserEmail.isEmpty()) {
            SharedPreferences userPrefs = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
            
            String name = userPrefs.getString(currentUserEmail + "_name", "N/A");
            String phone = userPrefs.getString(currentUserEmail + "_phone", "N/A");
            String email = currentUserEmail;

            // Set retrieved data to TextViews
            titleName.setText(name);
            profileName.setText(name);
            profileEmail.setText(email);
            profilePhone.setText(phone);
        } else {
            titleName.setText("Guest");
            profileName.setText("Guest");
            profileEmail.setText("N/A");
            profilePhone.setText("N/A");
        }
    }
}
