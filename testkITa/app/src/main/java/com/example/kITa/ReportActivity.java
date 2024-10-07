package com.example.kITa;

import static android.opengl.ETC1.encodeImage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, contactNo, itemName, location, date, time, otherDetails;
    private Spinner department, itemCategory;
    private ImageButton img1, img2, img3, img4, img5;
    private Button submitButton, cancelButton;
    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageButton[] imageButtons;
    private String[] encodedImages;
    private int currentImageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reporting);

        // Initialize form elements
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
        imageButtons = new ImageButton[]{img1, img2, img3, img4, img5};
        encodedImages = new String[5];

        for (int i = 0; i < imageButtons.length; i++) {
            final int index = i;
            imageButtons[i].setOnClickListener(v -> {
                currentImageIndex = index;
                openGallery();
            });
        }

        // Initialize navigation elements
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        // Set up date picker dialog
        calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                date.setText(sdf.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Set up time picker dialog
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                String myFormat = "hh:mm a"; // 12-hour format with AM/PM
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                time.setText(sdf.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false); // Set is24HourView to false

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
            }
        });

        // Set onClickListeners for form buttons
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Optionally close the activity
            }
        });

        // Set onClickListeners for navigation buttons
        guideIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, GuidelinesActivity.class);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navLost.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navFound.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, ClaimedActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ReportActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageButtons[currentImageIndex].setImageBitmap(bitmap);
                encodedImages[currentImageIndex] = encodeImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private boolean validateInputFields() {
        boolean isValid = true;

        if (firstName.getText().toString().trim().isEmpty()) {
            firstName.setError("First name is required");
            isValid = false;
        }

        if (lastName.getText().toString().trim().isEmpty()) {
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

        if (department.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
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

        int uploadedImages = 0;
        for (String encodedImage : encodedImages) {
            if (encodedImage != null && !encodedImage.isEmpty()) {
                uploadedImages++;
            }
        }

        if (uploadedImages < 3) {
            Toast.makeText(this, "Please upload at least 3 images", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // Use a regular expression to validate the email format
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        // Use a regular expression to validate the phone number format
        String phoneRegex = "^\\+?[0-9]{10,13}$";
        return phone.matches(phoneRegex);
    }

    private void submitReport() {
        if (validateInputFields()) {
            String url = "http://10.0.2.2/lost_found_db/submit_report.php";

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("Fname", firstName.getText().toString().trim());
                jsonBody.put("Lname", lastName.getText().toString().trim());
                jsonBody.put("email", email.getText().toString().trim());
                jsonBody.put("contact_no", contactNo.getText().toString().trim());
                jsonBody.put("dept_college", department.getSelectedItem().toString());
                jsonBody.put("item_name", itemName.getText().toString().trim());
                jsonBody.put("item_category", itemCategory.getSelectedItem().toString().trim());
                jsonBody.put("location_found", location.getText().toString().trim());
                jsonBody.put("date", date.getText().toString().trim());
                jsonBody.put("time", convertTo24HourFormat(time.getText().toString().trim()));
                jsonBody.put("other_details", otherDetails.getText().toString().trim());

                for (int i = 0; i < encodedImages.length; i++) {
                    if (encodedImages[i] != null && !encodedImages[i].isEmpty()) {
                        jsonBody.put("img" + (i + 1), encodedImages[i]);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    response -> {
                        Log.d("ReportActivity", "Response: " + response.toString());
                        Toast.makeText(ReportActivity.this, "Reporting Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        Log.e("ReportActivity", "Error: " + error.toString());
                        Toast.makeText(ReportActivity.this, "Error Reporting Item: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest);
        }
    }

    private String convertTo24HourFormat(String time12Hour) {
        try {
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.US);
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm:ss", Locale.US);
            return sdf24.format(sdf12.parse(time12Hour));
        } catch (Exception e) {
            e.printStackTrace();
            return time12Hour; // Return original string if parsing fails
        }
    }
}