package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
    private ImageButton guideIcon, searchIcon, navLost, navNotifications, navProfile, navChat;
    private RecyclerView todayNotifRecyclerView, olderNotifRecyclerView;
    private NotificationAdapter todayAdapter, olderAdapter;
    private List<NotificationItem> todayNotifications, olderNotifications;
    private static final String FETCH_CLAIM_NOTIFICATIONS_URL = "http://10.0.2.2/lost_found_db/fetch_notifications.php";
    private static final String FETCH_LOST_NOTIFICATIONS_URL = "http://10.0.2.2/lost_found_db/fetch_lost_notifications.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);

        UserSession userSession = UserSession.getInstance(this);

        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Please log-in your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initializeViews();
        toggleNavigationBasedOnEmail();
        setupRecyclerViews();
        setupClickListeners();
        fetchAllNotifications();
    }

    private void toggleNavigationBasedOnEmail() {
        boolean isEmailEmpty = TextUtils.isEmpty(UserSession.getInstance(this).getEmail());
        navChat.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
        navNotifications.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
    }

    private void initializeViews() {
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
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
        guideIcon.setOnClickListener(v -> navigateIfEmailValid(GuidelinesActivity.class));
        searchIcon.setOnClickListener(v -> navigateIfEmailValid(SearchActivity.class));
        navLost.setOnClickListener(v -> navigateIfEmailValid(MainActivity.class));
        navProfile.setOnClickListener(v -> navigateIfEmailValid(ProfileActivity.class));

        navNotifications.setOnClickListener(v -> {
            if (TextUtils.isEmpty(UserSession.getInstance(this).getEmail())) {
                Toast.makeText(this, "Please log-in to access notifications", Toast.LENGTH_SHORT).show();
            } else {
                finish();
                startActivity(getIntent());
            }
        });

        navChat.setOnClickListener(v -> navigateIfEmailValid(ChatActivity.class));
    }

    private void navigateIfEmailValid(Class<?> activityClass) {
        if (TextUtils.isEmpty(UserSession.getInstance(this).getEmail())) {
            Toast.makeText(this, "Please log in to access this feature", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, activityClass));
    }

    private void fetchAllNotifications() {
        fetchClaimNotifications();
        fetchLostNotifications();
    }

    private void fetchClaimNotifications() {
        String currentUserEmail = UserSession.getInstance(this).getEmail();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_CLAIM_NOTIFICATIONS_URL,
                response -> {
                    Log.d("ClaimNotificationResponse", "Response: " + response);
                    try {
                        if (response.contains("error")) {
                            JSONObject errorObj = new JSONObject(response);
                            String error = errorObj.getString("error");
                            Toast.makeText(NotificationActivity.this,
                                    "Server error: " + error,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONArray jsonArray = new JSONArray(response);
                        processClaimNotifications(jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("NotificationError", "Parse error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("NotificationError", "Volley error: " + error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", currentUserEmail);
                return params;
            }
        };

        addToRequestQueue(stringRequest);
    }

    private void fetchLostNotifications() {
        String currentUserEmail = UserSession.getInstance(this).getEmail();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_LOST_NOTIFICATIONS_URL,
                response -> {
                    Log.d("LostNotificationResponse", "Response: " + response);
                    try {
                        if (response.contains("error")) {
                            JSONObject errorObj = new JSONObject(response);
                            String error = errorObj.getString("error");
                            Toast.makeText(NotificationActivity.this,
                                    "Server error: " + error,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONArray jsonArray = new JSONArray(response);
                        processLostNotifications(jsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("NotificationError", "Parse error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("NotificationError", "Volley error: " + error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", currentUserEmail);
                return params;
            }
        };

        addToRequestQueue(stringRequest);
    }

    private void addToRequestQueue(StringRequest request) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void processClaimNotifications(JSONArray jsonArray) throws JSONException {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject notification = jsonArray.getJSONObject(i);
            String itemName = notification.getString("item_name");
            String claimDate = notification.getString("claim_date");
            String message = "Admin has approved your claim request of the \"" + itemName +
                    "\". Claim your item at Gate 2 and bring the proof of identification that you submitted.";

            if (today.equals(claimDate)) {
                todayNotifications.add(new NotificationItem("Claim Approved", message, claimDate));
            } else {
                olderNotifications.add(new NotificationItem("Claim Approved", message, claimDate));
            }
        }

        updateAdapters();
    }

    private void processLostNotifications(JSONArray jsonArray) throws JSONException {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject notification = jsonArray.getJSONObject(i);
            String itemName = notification.getString("item_name");
            String notifDate = notification.getString("notification_date");
            String message = itemName + " was found by the Admin. Check the item in the Main Menu to confirm if it is yours. Request claim the item for claiming.";

            if (today.equals(notifDate)) {
                todayNotifications.add(new NotificationItem("Found Item", message, notifDate));
            } else {
                olderNotifications.add(new NotificationItem("Found Item", message, notifDate));
            }
        }

        updateAdapters();
    }

    private void updateAdapters() {
        todayAdapter.notifyDataSetChanged();
        olderAdapter.notifyDataSetChanged();
    }
}