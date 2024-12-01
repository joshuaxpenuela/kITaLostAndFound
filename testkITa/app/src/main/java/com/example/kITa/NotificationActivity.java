package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {
    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;
    private RecyclerView todayNotifRecyclerView, olderNotifRecyclerView;
    private NotificationAdapter todayAdapter, olderAdapter;
    private List<NotificationItem> todayNotifications, olderNotifications;
    private static final String FETCH_NOTIFICATIONS_URL = "http://10.0.2.2/lost_found_db/fetch_notifications.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);

        // Check if email is empty before initializing
        if (TextUtils.isEmpty(UserSession.getInstance().getEmail())) {
            Toast.makeText(this, "Please log-in as Seeker to get notification.", Toast.LENGTH_SHORT).show();
            finish(); // Close the current activity
            return;
        }

        initializeViews();
        setupRecyclerViews();
        setupClickListeners();
        fetchNotifications();
    }

    private void initializeViews() {
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        todayNotifRecyclerView = findViewById(R.id.todayNotifRecycleView);
        olderNotifRecyclerView = findViewById(R.id.olderNotifRecycleView);
    }

    private void setupRecyclerViews() {
        todayNotifications = new ArrayList<>();
        olderNotifications = new ArrayList<>();

        todayAdapter = new NotificationAdapter(this, todayNotifications);
        olderAdapter = new NotificationAdapter(this, olderNotifications);

        todayNotifRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        olderNotifRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        todayNotifRecyclerView.setAdapter(todayAdapter);
        olderNotifRecyclerView.setAdapter(olderAdapter);
    }

    private void setupClickListeners() {
        // Add email check to each navigation click listener
        guideIcon.setOnClickListener(v -> navigateIfEmailValid(GuidelinesActivity.class));
        searchIcon.setOnClickListener(v -> navigateIfEmailValid(SearchActivity.class));
        navLost.setOnClickListener(v -> navigateIfEmailValid(MainActivity.class));
        navFound.setOnClickListener(v -> navigateIfEmailValid(ClaimedActivity.class));
        navChat.setOnClickListener(v -> navigateIfEmailValid(ChatActivity.class));
        navNotifications.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });
        navProfile.setOnClickListener(v -> navigateIfEmailValid(ProfileActivity.class));
    }

    // Helper method to check email before navigation
    private void navigateIfEmailValid(Class<?> activityClass) {
        if (TextUtils.isEmpty(UserSession.getInstance().getEmail())) {
            Toast.makeText(this, "Please log in to access this feature", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, activityClass));
    }

    private void fetchNotifications() {
        String currentUserEmail = UserSession.getInstance().getEmail();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_NOTIFICATIONS_URL,
                response -> {
                    Log.d("NotificationResponse", "Response: " + response);
                    try {
                        // First check if response contains error
                        if (response.contains("error")) {
                            JSONObject errorObj = new JSONObject(response);
                            String error = errorObj.getString("error");
                            Toast.makeText(NotificationActivity.this,
                                    "Server error: " + error,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Try to parse as JSONArray
                        JSONArray jsonArray = new JSONArray(response);
                        processNotifications(jsonArray);

                        if (jsonArray.length() == 0) {
                            Toast.makeText(NotificationActivity.this,
                                    "No notifications found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("NotificationError", "Parse error: " + e.getMessage());
                        Toast.makeText(NotificationActivity.this,
                                "Error loading notifications. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String errorMessage = "Network error. Please check your connection.";
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    Log.e("NotificationError", "Volley error: " + error.toString());
                    Toast.makeText(NotificationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", currentUserEmail);
                Log.d("NotificationDebug", "Sending email: " + currentUserEmail);
                return params;
            }
        };

        // Set retry policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,  // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void processNotifications(JSONArray jsonArray) throws JSONException {
        todayNotifications.clear();
        olderNotifications.clear();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject notification = jsonArray.getJSONObject(i);
            String itemName = notification.getString("item_name");
            String claimDate = notification.getString("claim_date");
            String message = "Admin has approved your claim request of the \"" + itemName +
                    "\". Kindly claim your item in the Gate 2.";

            if (today.equals(claimDate)) {
                todayNotifications.add(new NotificationItem(message, claimDate, true));
            } else {
                olderNotifications.add(new NotificationItem(message, claimDate, false));
            }
        }

        todayAdapter.notifyDataSetChanged();
        olderAdapter.notifyDataSetChanged();
    }
}