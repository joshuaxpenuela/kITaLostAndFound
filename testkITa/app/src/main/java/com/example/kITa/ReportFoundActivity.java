package com.example.kITa;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Base64;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import java.util.ArrayList;

import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.*;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportFoundActivity extends AppCompatActivity {

    private EditText itemName, location, date, time, otherDetails;
    private Spinner itemCategory;
    private ImageButton guideIcon, searchIcon, navLost, navChat, navNotifications, navProfile;
    private Calendar calendar;
    private ImageButton img1, img2, img3, img4, img5;
    private Button submitButton, cancelButton;
    private ImageButton selectedImageButton;
    private UserSession userSession;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Map<String, String> encodedImages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reportingfound);

        userSession = UserSession.getInstance(this);

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Please log-in your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close the current activity
            return;
        }

        // Initialize form elements
        initializeUIComponents();
        setupDateTimePickers();
        setupNavigation();
        toggleNavigationBasedOnEmail();

        // Set onClickListeners for ImageButtons to open image picker
        setImagePicker(img1, "img1");
        setImagePicker(img2, "img2");
        setImagePicker(img3, "img3");
        setImagePicker(img4, "img4");
        setImagePicker(img5, "img5");

        // Set onClickListeners for form buttons
        submitButton.setOnClickListener(v -> submitReport());
        cancelButton.setOnClickListener(v -> showDiscardDialog(MainActivity.class));

    }

    private void setupNavigation() {
        guideIcon.setOnClickListener(v -> showDiscardDialog(GuidelinesActivity.class));
        searchIcon.setOnClickListener(v -> showDiscardDialog(SearchActivity.class));
        navLost.setOnClickListener(v -> showDiscardDialog(MainActivity.class));
        navChat.setOnClickListener(v -> showDiscardDialog(ChatActivity.class));
        navNotifications.setOnClickListener(v -> showDiscardDialog(NotificationActivity.class));
        navProfile.setOnClickListener(v -> showDiscardDialog(ProfileActivity.class));
    }

    private void toggleNavigationBasedOnEmail() {
        boolean isEmailEmpty = TextUtils.isEmpty(UserSession.getInstance(this).getEmail());
        navChat.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
        navNotifications.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
    }

    private void showDiscardDialog(Class<?> destinationClass) {
        DiscardReportDialog dialog = new DiscardReportDialog(this, () -> {
            startActivity(new Intent(ReportFoundActivity.this, destinationClass));
            finish();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        // Show discard dialog when the back button is pressed
        DiscardReportDialog dialog = new DiscardReportDialog(this, this::finish);
        dialog.show();
    }

    private void initializeUIComponents() {
        itemName = findViewById(R.id.itemName);
        itemCategory = findViewById(R.id.itemCategory);
        location = findViewById(R.id.location);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        otherDetails = findViewById(R.id.otherDetails);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        submitButton = findViewById(R.id.SubmitReport);
        cancelButton = findViewById(R.id.cancelReport);

        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);
    }


    private void setupDateTimePickers() {
        calendar = Calendar.getInstance();

        // Date Picker
        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        date.setOnClickListener(v -> datePickerDialog.show());

        // Time Picker
        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            time.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

        time.setOnClickListener(v -> timePickerDialog.show());
    }

    private void setImagePicker(ImageButton imageButton, String imageKey) {
        imageButton.setOnClickListener(v -> {
            selectedImageButton = imageButton;  // Keep track of the selected button
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });

        // Add long press listener for image removal
        imageButton.setOnLongClickListener(v -> {
            if (encodedImages.containsKey(imageKey)) {
                // Show confirmation dialog
                new AlertDialog.Builder(this)
                        .setTitle("Remove Image")
                        .setMessage("Are you sure you want to remove this image?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Remove the image from encodedImages
                            encodedImages.remove(imageKey);
                            // Reset the ImageButton to default state
                            imageButton.setImageResource(R.drawable.ic_addphoto);
                            imageButton.setScaleType(ImageView.ScaleType.CENTER);
                            Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                // Get file size in bytes
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    int fileSizeInBytes = inputStream.available();
                    inputStream.close();

                    if (fileSizeInBytes > 5 * 1024 * 1024) {
                        Toast.makeText(this, "Image size exceeds 5MB", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Decode and encode the image
                inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                String encodedImage = encodeImage(bitmap);

                // Set the image to fill the ImageButton
                selectedImageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(this)
                        .load(bitmap)
                        .into(selectedImageButton);

                // Determine which imageButton was selected and store accordingly
                if (selectedImageButton == img1) {
                    encodedImages.put("img1", encodedImage);
                } else if (selectedImageButton == img2) {
                    encodedImages.put("img2", encodedImage);
                } else if (selectedImageButton == img3) {
                    encodedImages.put("img3", encodedImage);
                } else if (selectedImageButton == img4) {
                    encodedImages.put("img4", encodedImage);
                } else if (selectedImageButton == img5) {
                    encodedImages.put("img5", encodedImage);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error processing the image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    private void submitReport() {
        if (validateInputFields() && encodedImages.size() >= 1) {
            String url = "http://10.0.2.2/lost_found_db/submit_report.php";

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            String responseString = new String(response.data, StandardCharsets.UTF_8);
                            JSONObject jsonResponse = new JSONObject(responseString);

                            if (jsonResponse.getBoolean("success")) {
                                Toast toast = Toast.makeText(ReportFoundActivity.this, "Report submitted successfully. Kindly surrender the lost item in CvSU-Main Gate 2.", Toast.LENGTH_LONG);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.show();
                                startActivity(new Intent(ReportFoundActivity.this, MainActivity.class));
                                finish();
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Toast.makeText(ReportFoundActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReportFoundActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = error.getMessage();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String responseString = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Toast.makeText(ReportFoundActivity.this, "Error: " + responseString, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ReportFoundActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ReportFoundActivity.this, "Network Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getStringParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Fname", userSession.getFirstName());
                    params.put("Lname", userSession.getLastName());
                    params.put("email", userSession.getEmail());
                    params.put("contact_no", userSession.getContactNo());
                    params.put("dept_college", userSession.getDept());
                    params.put("item_name", itemName.getText().toString().trim());
                    params.put("item_category", itemCategory.getSelectedItem().toString());
                    params.put("location_found", location.getText().toString().trim());
                    params.put("report_date", date.getText().toString().trim());
                    params.put("report_time", convertTo24HourFormat(time.getText().toString().trim()));
                    params.put("other_details", otherDetails.getText().toString().trim());

                    return params;
                }

                @Override
                public Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    for (Map.Entry<String, String> entry : encodedImages.entrySet()) {
                        byte[] imageBytes = Base64.decode(entry.getValue(), Base64.DEFAULT);
                        params.put(entry.getKey(), new DataPart(entry.getKey() + ".jpg", imageBytes, "image/jpeg"));
                    }
                    return params;
                }
            };

            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(multipartRequest);
        } else {
            Toast.makeText(this, "Please fill all required fields and upload at least 1 image", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputFields() {
        boolean isValid = true;

        if (itemName.getText().toString().trim().isEmpty()) {
            itemName.setError("Item name is required");
            isValid = false;
        }
        if (itemCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select an item category", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        if (location.getText().toString().trim().isEmpty()) {
            location.setError("Location is required");
            isValid = false;
        }
        if (date.getText().toString().trim().isEmpty()) {
            date.setError("Date is required");
            isValid = false;
        }
        if (time.getText().toString().trim().isEmpty()) {
            time.setError("Time is required");
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        // Regex for phone numbers starting with "09" and having exactly 11 digits
        String phoneRegex = "^09\\d{9}$";
        return phone.matches(phoneRegex);
    }

    private String convertTo24HourFormat(String time12Hour) {
        try {
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm:ss", Locale.US);
            return sdf24.format(sdf12.parse(time12Hour));
        } catch (Exception e) {
            e.printStackTrace();
            return time12Hour;
        }
    }
}