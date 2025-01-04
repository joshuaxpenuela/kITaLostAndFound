package com.example.kITa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

    private ImageButton guideIcon, searchIcon, navLost, navChat, navNotifications, navProfile;
    private ImageSlider imageSlider;
    private TextView itemNameText, statusText, locationText, dateText, timeText, itemCategoryText, otherDetailsText, reportedByText;
    private Button claimItemBtn, sendEmailBtn;

    private static final String TAG = "UnclaimedActivity";
    private static final String URL = "http://10.0.2.2/lost_found_db/get_items_details.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_unclaimeditem);

        UserSession userSession = UserSession.getInstance(this);

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Please log-in your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close the current activity
            return;
        }

        initializeViews();
        setupClickListeners();
        toggleNavigationBasedOnEmail();
        manageButtonVisibility();

        int itemId = getIntent().getIntExtra("item_id", -1);
        Log.d(TAG, "Received item_id: " + itemId);

        if (itemId != -1) {
            fetchItemDetails(itemId);
        } else {
            Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void manageButtonVisibility() {
        String userEmail = UserSession.getInstance(this).getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            claimItemBtn.setVisibility(View.GONE);
            sendEmailBtn.setVisibility(View.GONE);
        } else {
            claimItemBtn.setVisibility(View.VISIBLE);
            sendEmailBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initializeViews() {
        // ... (keep existing view initializations)

        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
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

        sendEmailBtn.setOnClickListener(v -> {
            String userEmail = UserSession.getInstance(this).getEmail();
            if (userEmail == null || userEmail.isEmpty()) {
                Toast.makeText(this, "Please log in as a Seeker to send an email", Toast.LENGTH_SHORT).show();
                return;
            }
            sendEmailToReporter();
        });

        claimItemBtn.setOnClickListener(v -> {
            String userEmail = UserSession.getInstance(this).getEmail();
            if (userEmail == null || userEmail.isEmpty()) {
                Toast.makeText(this, "Please log-in as Seeker to claim this lost item", Toast.LENGTH_SHORT).show();
                return;
            }

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
        navChat.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, ChatActivity.class)));
        navNotifications.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(UnclaimedActivity.this, ProfileActivity.class)));
    }

    private void toggleNavigationBasedOnEmail() {
        boolean isEmailEmpty = TextUtils.isEmpty(UserSession.getInstance(this).getEmail());
        navChat.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
        navNotifications.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
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

        String time = item.getString("report_time");
        String formattedTime = convertTimeTo12HourFormat(time);
        timeText.setText(formattedTime);

        itemCategoryText.setText(item.getString("item_category"));
        otherDetailsText.setText(item.getString("other_details"));

        // Check if Fname is "Anonymous"
        String firstName = item.getString("Fname");
        String lastName = item.getString("Lname");
        if (firstName.equals("Anonymous")) {
            reportedByText.setText("Anonymous");
        } else {
            reportedByText.setText(firstName + " " + lastName);
        }

        // Set up image slider
        List<SlideModel> slideModels = new ArrayList<>();
        String[] imageFields = {"img1", "img2", "img3", "img4", "img5"};
        for (String field : imageFields) {
            if (!item.isNull(field) && !item.getString(field).isEmpty()) {
                slideModels.add(new SlideModel("http://10.0.2.2/lost_found_db/uploads/img_reported_items/" +
                        item.getString(field), ScaleTypes.CENTER_INSIDE));
            }
        }
        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_INSIDE);
    }

    private void sendEmailToReporter() {
        String userEmail = UserSession.getInstance(this).getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Please log in as a Seeker to send an email", Toast.LENGTH_SHORT).show();
            return;
        }

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
        String userEmail = UserSession.getInstance(this).getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Please log in as a Seeker to send an email", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "kITa Lost and Found System");
        startActivity(Intent.createChooser(intent, "Send email"));
    }

    private String convertTimeTo12HourFormat(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }
}