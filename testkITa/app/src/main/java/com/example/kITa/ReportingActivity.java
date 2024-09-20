package com.example.kITa;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportingActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, contactNo, itemName, location, date, time, otherDetails;
    private Spinner department,itemCategory;
    private ImageButton img1, img2, img3, img4, img5;
    private Button submitButton, cancelButton;
    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;

    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

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
                // Set the selected date in the format YYYY-MM-DD
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd"; // Change this date format as needed
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
                // Set the selected time in the format HH:mm
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String myFormat = "hh:mm a"; // Change this time format as needed
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                time.setText(sdf.format(calendar.getTime()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

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
            Intent intent = new Intent(ReportingActivity.this, GuidelinesActivity.class);
            startActivity(intent);
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(ReportingActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        navLost.setOnClickListener(v -> {
            Intent intent = new Intent(ReportingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        navFound.setOnClickListener(v -> {
            Intent intent = new Intent(ReportingActivity.this, ClaimedActivity.class);
            startActivity(intent);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(ReportingActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(ReportingActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ReportingActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void submitReport() {
        // Convert images to Base64 strings

        String url = "http://10.0.2.2/lost_found_db/submit_report.php";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ReportActivity", "Response: " + response);
                        // Handle success
                        Toast.makeText(ReportingActivity.this, "Reporting Successful", Toast.LENGTH_SHORT).show();
                        // Navigate back to MainActivity
                        Intent intent = new Intent(ReportingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Close the current activity
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ReportActivity", "Error: " + error.getMessage());
                        // Handle error
                        Toast.makeText(ReportingActivity.this, "Error Reporting Item", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Fname", firstName.getText().toString().trim());
                params.put("Lname", lastName.getText().toString().trim());
                params.put("email", email.getText().toString().trim());
                params.put("contact_no", contactNo.getText().toString().trim());
                params.put("dept_college", department.getSelectedItem().toString());
                params.put("item_name", itemName.getText().toString().trim());
                params.put("item_category", itemCategory.getSelectedItem().toString().trim());
                params.put("location_found", location.getText().toString().trim());
                params.put("date", date.getText().toString().trim());
                params.put("time", time.getText().toString().trim());
                params.put("other_details", otherDetails.getText().toString().trim());
                return params;
            }
        };

        queue.add(request);
    }
}