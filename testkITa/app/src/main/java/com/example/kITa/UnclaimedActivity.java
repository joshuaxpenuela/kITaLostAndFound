package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UnclaimedActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;

    private static final String TAG = "UnclaimedActivity";
    private static final String URL = "http://10.0.2.2/lost_found_db/get_items_details.php";

    private ImageView itemImage;
    private TextView itemNameText, statusText, locationText, dateText, timeText, itemCategoryText, otherDetailsText, reportedByText;
    private Button claimItemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_unclaimeditem);

        // Initialize views
        initializeViews();
        setupClickListeners();

        int itemId = getIntent().getIntExtra("item_id", -1);
        Log.d("UnclaimedActivity", "Received item_id: " + itemId);

        if (itemId != -1) {
            fetchItemDetails(itemId);
        } else {
            Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {

        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        itemNameText = findViewById(R.id.itemName);
        statusText = findViewById(R.id.status);
        locationText = findViewById(R.id.location);
        dateText = findViewById(R.id.date);
        timeText = findViewById(R.id.time);
        itemCategoryText = findViewById(R.id.itemCategory);
        otherDetailsText = findViewById(R.id.otherDetails);
        reportedByText = findViewById(R.id.Fname); // Assuming we'll concatenate first and last name
        claimItemBtn = findViewById(R.id.claimItemBtn);

        claimItemBtn.setOnClickListener(v -> {
            // TODO: Implement claim item functionality
            Toast.makeText(this, "Claim functionality not implemented yet", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupClickListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, SearchActivity.class)));
        navLost.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, MainActivity.class)));
        navFound.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, ClaimedActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, ChatActivity.class)));
        navNotifications.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, ProfileActivity.class)));
    }

    private void fetchItemDetails(int itemId) {
        Log.d("UnclaimedActivity", "Fetching details for item_id: " + itemId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = URL + "?id_item=" + itemId; // Corrected URL
        Log.d("UnclaimedActivity", "Request URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("UnclaimedActivity", "Received response: " + response.toString());
                    try {
                        if (response.has("error")) {
                            Log.e("UnclaimedActivity", "Error in response: " + response.getString("error"));
                            Toast.makeText(UnclaimedActivity.this, "Error fetching data: " + response.getString("error"), Toast.LENGTH_SHORT).show();
                        } else {
                            displayItemDetails(response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UnclaimedActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("UnclaimedActivity", "Error fetching data: " + error.toString());
                    Toast.makeText(UnclaimedActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    // In SearchActivity.java
    private void onSearchItemClick(SearchItem item) {
        Intent intent = new Intent(this, UnclaimedActivity.class);
        intent.putExtra("item_id", item.getId());
        startActivity(intent);
    }

    private void displayItemDetails(JSONObject item) throws JSONException {
        itemNameText.setText(item.getString("item_name"));
        statusText.setText(item.getString("status"));
        locationText.setText(item.getString("location_found"));
        dateText.setText(item.getString("date"));

        // Get the time from the response and convert it to 12-hour format
        String time24Format = item.getString("time");
        String time12Format = convertTimeTo12HourFormat(time24Format); // Call helper method
        timeText.setText(time12Format); // Display the converted time

        itemCategoryText.setText(item.getString("item_category"));
        otherDetailsText.setText(item.getString("other_details"));
        reportedByText.setText(item.getString("Fname") + " " + item.getString("Lname"));
    }

    private String convertTimeTo12HourFormat(String time24) {
        try {
            // Define 24-hour and 12-hour format patterns
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");

            // Parse the 24-hour time and convert to 12-hour format
            Date date = inputFormat.parse(time24);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return time24; // Return original time if parsing fails
        }

    }
}