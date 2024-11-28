package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ClaimedItemDetailActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;
    private ImageSlider imageSlider;
    private TextView itemNameText, statusText, locationText, dateText, timeText, itemCategoryText, otherDetailsText, reportedByText, claimaintNameText, claimDateText, claimTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_claimeditem);

        initializeViews();
        setupClickListeners();

        int itemId = getIntent().getIntExtra("item_id", -1);
        Log.d("ClaimedItemDetail", "Received item ID: " + itemId);
        if (itemId != -1) {
            fetchItemDetails(itemId);
        } else {
            Toast.makeText(this, "Invalid item ID", Toast.LENGTH_SHORT).show();
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

        imageSlider = findViewById(R.id.claimedItemImgSlide); // Initialize ImageSlider
        itemNameText = findViewById(R.id.itemName);
        statusText = findViewById(R.id.status);
        locationText = findViewById(R.id.location);
        dateText = findViewById(R.id.date);
        timeText = findViewById(R.id.time);
        itemCategoryText = findViewById(R.id.itemCategory);
        otherDetailsText = findViewById(R.id.otherDetails);
        reportedByText = findViewById(R.id.Fname);
        claimaintNameText = findViewById(R.id.claimaintName);
        claimDateText = findViewById(R.id.claimDate);
        claimTimeText = findViewById(R.id.claimTime);
    }

    private void setupClickListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(ClaimedItemDetailActivity.this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(ClaimedItemDetailActivity.this, SearchActivity.class)));
        navLost.setOnClickListener(v -> startActivity(new Intent(ClaimedItemDetailActivity.this, MainActivity.class)));
        navFound.setOnClickListener(v -> startActivity(new Intent(ClaimedItemDetailActivity.this, ClaimedActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(ClaimedItemDetailActivity.this, ChatActivity.class)));
        navNotifications.setOnClickListener(v -> startActivity(new Intent(ClaimedItemDetailActivity.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(ClaimedItemDetailActivity.this, ProfileActivity.class)));
    }

    private void fetchItemDetails(int itemId) {
        String url = "https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/get_claimed_items_details.php?id_item=" + itemId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("API_RESPONSE", response.toString());
                    try {
                        displayItemDetails(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing item details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("API_ERROR", "Error: " + error.toString());
                    Toast.makeText(this, "Error fetching item details", Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void displayItemDetails(JSONObject item) throws JSONException {
        runOnUiThread(() -> {
            try {
                itemNameText.setText(item.getString("item_name"));
                statusText.setText(item.getString("status"));
                locationText.setText(item.getString("location_found"));
                dateText.setText(item.getString("report_date"));

                String time24Format = item.getString("report_time");
                String time12Format = convertTimeTo12HourFormat(time24Format);
                timeText.setText(time12Format);

                itemCategoryText.setText(item.getString("item_category"));
                otherDetailsText.setText(item.getString("other_details"));
                reportedByText.setText(item.getString("Fname") + " " + item.getString("Lname"));
                claimaintNameText.setText(item.getString("claim_Fname") + " " + item.getString("claim_Lname"));
                claimDateText.setText(item.getString("claim_date"));
                time24Format = item.getString("claim_time");
                time12Format = convertTimeTo12HourFormat(time24Format);
                claimTimeText.setText(time12Format);

                // Populate ImageSlider with available images only (no broken images)
                List<SlideModel> slideModels = new ArrayList<>();
                if (!item.isNull("img1") && !item.getString("img1").isEmpty()) {
                    slideModels.add(new SlideModel("https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/uploads/img_reported_items/" + item.getString("img1"), ScaleTypes.CENTER_INSIDE));
                }
                if (!item.isNull("img2") && !item.getString("img2").isEmpty()) {
                    slideModels.add(new SlideModel("https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/uploads/img_reported_items/" + item.getString("img2"), ScaleTypes.CENTER_INSIDE));
                }
                if (!item.isNull("img3") && !item.getString("img3").isEmpty()) {
                    slideModels.add(new SlideModel("https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/uploads/img_reported_items/" + item.getString("img3"), ScaleTypes.CENTER_INSIDE));
                }
                if (!item.isNull("img4") && !item.getString("img4").isEmpty()) {
                    slideModels.add(new SlideModel("https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/uploads/img_reported_items/" + item.getString("img4"), ScaleTypes.CENTER_INSIDE));
                }
                if (!item.isNull("img5") && !item.getString("img5").isEmpty()) {
                    slideModels.add(new SlideModel("https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/uploads/img_reported_items/" + item.getString("img5"), ScaleTypes.CENTER_INSIDE));
                }

                imageSlider.setImageList(slideModels);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error displaying item details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertTimeTo12HourFormat(String time24) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(time24));
        } catch (ParseException e) {
            e.printStackTrace();
            return time24;
        }
    }
}
