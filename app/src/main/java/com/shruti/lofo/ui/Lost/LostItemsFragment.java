package com.shruti.lofo.ui.Lost;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.shruti.lofo.LocalStore;
import com.shruti.lofo.R;
import com.shruti.lofo.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class LostItemsFragment extends DialogFragment {
    private ImageButton datePickerButton;

    private ImageButton timePickerButton;
    private TextView dateEdit;
    private TextView timeEdit;
    private Spinner categorySpinner;
    ImageView image;
    Button upload;
    Uri imageUri;

    EditText description;

    private EditText location ;

    final int REQ_CODE=1000;
    private int mYear, mMonth, mDay, mHour, mMinute;
    String date = null;
    String time = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lost_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        description = view.findViewById(R.id.description);
        datePickerButton = view.findViewById(R.id.datePickerButton);
        timePickerButton= view.findViewById(R.id.timePickerButton);
        datePickerButton.setOnClickListener(v -> showDatePicker());
        timePickerButton.setOnClickListener(v -> showTimePicker());
        dateEdit= view.findViewById(R.id.selectedDateEditText);
        timeEdit= view.findViewById(R.id.selectedTimeEditText);
        location= view.findViewById(R.id.location);


        categorySpinner = view.findViewById(R.id.categorySpinner);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        final String[] selectedCategory = new String[1];
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedCategory[0] = categorySpinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
            }
        });

        upload = view.findViewById(R.id.uploadImageButton);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,REQ_CODE);
            }
        });

        Button submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {

            EditText item =  view.findViewById(R.id.item_name_edittext);
            String itemName =  item.getText().toString();

            // validation
            if (itemName.isEmpty()) {
                Utility.showToast(getContext(), "Name cannot be empty");
                return;
            }

            // Check if category is selected
            if (selectedCategory[0] == null) {
                Utility.showToast(getContext(), "Please select a category");
                return;
            }



            if (date == null) {
                showDatePicker();
                return;
            }

            if (time == null) {
                showTimePicker();
                return;
            }

            // Check that location is not empty
            String loc = location.getText().toString();
            if (loc.isEmpty()) {
                Utility.showToast(getContext(), "Please provide location");
                return;
            }

            // Check that description is not empty
            String desc = description.getText().toString();
            if (desc.isEmpty()) {
                Utility.showToast(getContext(), "Please add description");
                return;
            }

            if (imageUri == null) {
                // Set the default image URI to the default image in the layout file
                imageUri = Uri.parse("android.resource://com.shruti.lofo/drawable/sample_img");
            }

            LostItems lostItem = new LostItems();
            lostItem.setItemName(itemName);
            lostItem.setCategory(selectedCategory[0]);
            lostItem.setDateLost(date);
            lostItem.setTimeLost(time);

            lostItem.setLocation(location.getText().toString());
            lostItem.setDescription(description.getText().toString());

            // Get User Info from Local Preferences
            SharedPreferences loginPrefs = requireContext().getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String currentUserEmail = loginPrefs.getString("currentUser", "");

            if (!currentUserEmail.isEmpty()) {
                SharedPreferences userPrefs = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String name = userPrefs.getString(currentUserEmail + "_name", "Unknown");
                String phone = userPrefs.getString(currentUserEmail + "_phone", "0");
                
                lostItem.setOwnerName(name);
                try {
                    lostItem.setPhnum(Long.valueOf(phone));
                } catch (NumberFormatException e) {
                    lostItem.setPhnum(0L);
                }
                lostItem.setEmail(currentUserEmail);
                lostItem.setUserId(currentUserEmail);
            }

             // Save Image Locally
            String imagePath = saveImageToInternalStorage(imageUri);
            if (imagePath != null) {
                lostItem.setImageURI(imagePath);
                
                // Save Item to Local Store
                LocalStore localStore = new LocalStore(requireContext());
                localStore.saveLostItem(lostItem);
                
                Utility.showToast(getContext(), "Item added locally!");
                dismiss();
            } else {
                 // Try saving item even if image fails (or is default)
                if (imageUri.toString().contains("android.resource")) {
                     lostItem.setImageURI(imageUri.toString());
                     LocalStore localStore = new LocalStore(requireContext());
                     localStore.saveLostItem(lostItem);
                     Utility.showToast(getContext(), "Item added locally (default image)!");
                     dismiss();
                } else {
                    Utility.showToast(getContext(), "Failed to save image locally");
                }
            }

        });

    }
    
    private String saveImageToInternalStorage(Uri uri) {
        // Handle resource URIs differently or just return null/uri string
        if (uri.toString().startsWith("android.resource")) {
            return uri.toString();
        }

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            
            Context context = requireContext();
            String filename = "lost_" + System.currentTimeMillis() + ".jpg";
            File file = new File(context.getFilesDir(), filename);
            
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQ_CODE){
                // for gallery
                imageUri = data.getData();
                upload.setText("Image added");

            }
        }
    }


    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDateButton();
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        updateDateButton();
    }

    private void showTimePicker() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    mHour = hourOfDay;
                    mMinute = minute;
                    updateTimeButton();
                }, mHour, mMinute, false);
        timePickerDialog.show();
        updateTimeButton();
    }

    private String updateDateButton() {
        String date = mDay + "/" + (mMonth + 1) + "/" + mYear;
        dateEdit.setText(date);
        this.date = date;
        return date;
    }

    private String updateTimeButton() {
        String AM_PM;
        if (mHour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        int hour = mHour % 12;
        if (hour == 0) {
            hour = 12;
        }
        String time = hour + ":" + mMinute + " " + AM_PM;
      timeEdit.setText(time);
        this.time=time;
        return time;
    }

}
