package com.example.kITa;

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

public class ReportActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, contactNo, itemName, location, date, time, otherDetails;
    private Spinner department, itemCategory;
    private ImageButton guideIcon, searchIcon, navLost, navChat, navNotifications, navProfile;
    private Calendar calendar;
    private ImageButton img1, img2, img3, img4, img5;
    private Button submitButton, cancelButton;
    private ImageButton selectedImageButton;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Map<String, String> encodedImages = new HashMap<>();
    private ArrayList<String> collegeList;
    private ArrayAdapter<String> collegeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reporting);

        // Initialize form elements
        initializeUIComponents();
        setupDateTimePickers();
        setupNavigation();
        setupCollegeSpinner(); // Add this line
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

        InputFilter nameFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                        return ""; // Reject input that is not a letter or space
                    }
                }
                return null; // Acceptable input
            }
        };
        firstName.setFilters(new InputFilter[]{nameFilter});
        lastName.setFilters(new InputFilter[]{nameFilter});
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
        boolean isEmailEmpty = TextUtils.isEmpty(UserSession.getInstance().getEmail());
        navChat.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
        navNotifications.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
    }

    private void showDiscardDialog(Class<?> destinationClass) {
        DiscardReportDialog dialog = new DiscardReportDialog(this, () -> {
            startActivity(new Intent(ReportActivity.this, destinationClass));
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
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        contactNo = findViewById(R.id.contactNo);
        department = findViewById(R.id.department);
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

    private void setupCollegeSpinner() {
        collegeList = new ArrayList<>();
        collegeList.add("Select College"); // Add default option

        collegeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, collegeList);
        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(collegeAdapter);

        fetchColleges();
    }

    private void fetchColleges() {
        String url = "http://10.0.2.2/lost_found_db/college.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray collegesArray = response.getJSONArray("colleges");
                            for (int i = 0; i < collegesArray.length(); i++) {
                                collegeList.add(collegesArray.getString(i));
                            }
                            collegeAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error parsing colleges data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ReportActivity.this, "Error fetching colleges: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
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
            startActivityForResult(intent, 1); // Use 1 as a request code for all image selections
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
                    int fileSizeInBytes = inputStream.available(); // Get size in bytes
                    inputStream.close();

                    if (fileSizeInBytes > 5 * 1024 * 1024) { // Check if it exceeds 5MB
                        Toast.makeText(this, "Image size exceeds 5MB", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // Decode and encode the image
                inputStream = getContentResolver().openInputStream(imageUri); // Re-open the input stream
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                String encodedImage = encodeImage(bitmap);
                Glide.with(this).load(bitmap).into(selectedImageButton);

                // Use selectedImageButton to determine which key to use in encodedImages
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
                                Toast toast = Toast.makeText(ReportActivity.this, "Report submitted successfully. Kindly surrender the lost item in CvSU-Main Gate 2.", Toast.LENGTH_LONG);
                                toast.setDuration(Toast.LENGTH_LONG); // Extend display time to 6 seconds
                                toast.show();
                                startActivity(new Intent(ReportActivity.this, MainActivity.class));
                                finish();
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Toast.makeText(ReportActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ReportActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        String errorMessage = error.getMessage();
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String responseString = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                Toast.makeText(ReportActivity.this, "Error: " + responseString, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(ReportActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ReportActivity.this, "Network Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getStringParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Fname", firstName.getText().toString().trim());
                    params.put("Lname", lastName.getText().toString().trim());
                    params.put("email", email.getText().toString().trim());
                    params.put("contact_no", contactNo.getText().toString().trim());
                    params.put("dept_college", department.getSelectedItem().toString());
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

        if (TextUtils.isEmpty(firstName.getText().toString().trim())) {
            firstName.setError("First name is required");
            isValid = false;
        }
        // Validate lastName
        if (TextUtils.isEmpty(lastName.getText().toString().trim())) {
            lastName.setError("Last name is required");
            isValid = false;
        }
        if (email.getText().toString().trim().isEmpty()) {
            email.setError("Email is required");
            isValid = false;
        } else if (!isValidEmail(email.getText().toString().trim())) {
            email.setError("Invalid email format");
            isValid = false;
        }
        if (contactNo.getText().toString().trim().isEmpty()) {
            contactNo.setError("Contact number is required");
            isValid = false;
        } else if (!isValidPhone(contactNo.getText().toString().trim())) {
            contactNo.setError("Invalid phone number format");
            isValid = false;
        }
        if (department.getSelectedItem().toString().equals("Select College")) {
            Toast.makeText(this, "Please select a college", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
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