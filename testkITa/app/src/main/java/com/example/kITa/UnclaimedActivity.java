package com.example.kITa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UnclaimedActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;
    private ImageSlider imageSlider;
    private TextView itemNameText, statusText, locationText, dateText, timeText, itemCategoryText, otherDetailsText, reportedByText;
    private Button claimItemBtn, sendEmailBtn;

    private static final String TAG = "UnclaimedActivity";
    private static final String URL = "http://10.0.2.2/lost_found_db/get_items_details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_unclaimeditem);

        // Initialize views
        initializeViews();
        setupClickListeners();

        int itemId = getIntent().getIntExtra("item_id", -1);
        Log.d(TAG, "Received item_id: " + itemId);

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

        imageSlider = findViewById(R.id.unclaimedItemImgSlide);
        itemNameText = findViewById(R.id.itemName);
        statusText = findViewById(R.id.status);
        locationText = findViewById(R.id.location);
        dateText = findViewById(R.id.date);
        timeText = findViewById(R.id.time);
        itemCategoryText = findViewById(R.id.itemCategory);
        otherDetailsText = findViewById(R.id.otherDetails);
        reportedByText = findViewById(R.id.Fname);
        claimItemBtn = findViewById(R.id.claimItemBtn);
        sendEmailBtn = findViewById(R.id.sendEmail);
        sendEmailBtn.setOnClickListener(v -> sendEmailToReporter());
        claimItemBtn.setOnClickListener(v -> {
            int itemId = getIntent().getIntExtra("item_id", -1);
            if (itemId != -1) {
                Intent intent = new Intent(UnclaimedActivity.this, ClaimingItemActivity.class);
                intent.putExtra("item_id", itemId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            }
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
        Log.d(TAG, "Fetching details for item_id: " + itemId);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = URL + "?id_item=" + itemId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has("error")) {
                            Log.e(TAG, "Error in response: " + response.getString("error"));
                            Toast.makeText(UnclaimedActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        } else {
                            displayItemDetails(response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(UnclaimedActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching data: " + error.toString());
                    Toast.makeText(UnclaimedActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void displayItemDetails(JSONObject item) throws JSONException {
        itemNameText.setText(item.getString("item_name"));
        statusText.setText(item.getString("status"));
        locationText.setText(item.getString("location_found"));
        dateText.setText(item.getString("report_date"));

        // Convert time to 12-hour format before displaying it
        String time = item.getString("report_time");
        String formattedTime = convertTimeTo12HourFormat(time);
        timeText.setText(formattedTime);

        itemCategoryText.setText(item.getString("item_category"));
        otherDetailsText.setText(item.getString("other_details"));
        reportedByText.setText(item.getString("Fname") + " " + item.getString("Lname"));

        // Dynamically populate ImageSlider and use CENTER_INSIDE or CENTER_CROP to avoid stretching
        List<SlideModel> slideModels = new ArrayList<>();
        if (!item.isNull("img1") && !item.getString("img1").isEmpty()) {
            slideModels.add(new SlideModel("http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + item.getString("img1"), ScaleTypes.CENTER_INSIDE));
        }
        if (!item.isNull("img2") && !item.getString("img2").isEmpty()) {
            slideModels.add(new SlideModel("http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + item.getString("img2"), ScaleTypes.CENTER_INSIDE));
        }
        if (!item.isNull("img3") && !item.getString("img3").isEmpty()) {
            slideModels.add(new SlideModel("http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + item.getString("img3"), ScaleTypes.CENTER_INSIDE));
        }
        if (!item.isNull("img4") && !item.getString("img4").isEmpty()) {
            slideModels.add(new SlideModel("http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + item.getString("img4"), ScaleTypes.CENTER_INSIDE));
        }
        if (!item.isNull("img5") && !item.getString("img5").isEmpty()) {
            slideModels.add(new SlideModel("http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + item.getString("img5"), ScaleTypes.CENTER_INSIDE));
        }

        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);
    }

    private void sendEmailToReporter() {
        try {
            int itemId = getIntent().getIntExtra("item_id", -1);
            if (itemId != -1) {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String url = URL + "?id_item=" + itemId;

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                if (response.has("error")) {
                                    Log.e(TAG, "Error in response: " + response.getString("error"));
                                    Toast.makeText(UnclaimedActivity.this, "Error fetching email", Toast.LENGTH_SHORT).show();
                                } else {
                                    String email = response.getString("email");
                                    openGmailCompose(email);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(UnclaimedActivity.this, "Error parsing email", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            Log.e(TAG, "Error fetching data: " + error.toString());
                            Toast.makeText(UnclaimedActivity.this, "Error fetching email", Toast.LENGTH_SHORT).show();
                        });

                requestQueue.add(request);
            } else {
                Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error sending email", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGmailCompose(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "kITa Lost and Found System");
        startActivity(Intent.createChooser(intent, "Send email"));
    }

    // Method to convert time from 24-hour to 12-hour format
    private String convertTimeTo12HourFormat(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
            return time; // Return original time in case of error
        }
    }
}