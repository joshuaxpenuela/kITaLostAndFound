package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClaimedActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;
    private RecyclerView claimedItemsRecyclerView;
    private ClaimedItemsAdapter claimedItemsAdapter;
    private List<ClaimedItem> claimedItemsList;
    private RequestQueue requestQueue;

    private static final String TAG = "ClaimedActivity";
    private static final String URL = "http://10.0.2.2/lost_found_db/get_claimed_items.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_claimed);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        fetchClaimedItems();
    }

    private void initializeViews() {
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);

        claimedItemsRecyclerView = findViewById(R.id.claimedItemsRecyclerView);
    }

    private void setupClickListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(ClaimedActivity.this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(ClaimedActivity.this, SearchActivity.class)));
        navLost.setOnClickListener(v -> startActivity(new Intent(ClaimedActivity.this, MainActivity.class)));
        navFound.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });
        navChat.setOnClickListener(v -> startActivity(new Intent(ClaimedActivity.this, ChatActivity.class)));
        navNotifications.setOnClickListener(v -> startActivity(new Intent(ClaimedActivity.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(ClaimedActivity.this, ProfileActivity.class)));
    }

    private void setupRecyclerView() {
        claimedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        claimedItemsList = new ArrayList<>();
        claimedItemsAdapter = new ClaimedItemsAdapter(claimedItemsList, itemId -> {
            Intent intent = new Intent(ClaimedActivity.this, ClaimedItemDetailActivity.class);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
        });
        claimedItemsRecyclerView.setAdapter(claimedItemsAdapter);
    }

    private void fetchClaimedItems() {
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);

                            int id = item.optInt("id_item", -1);
                            String itemName = item.optString("item_name", "Unknown Item");
                            String location = item.optString("location_found", "Unknown Location");
                            String img1Path = item.optString("img1", ""); // Fetching img1 path

                            // Build image URL
                            String imageUrl = "http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + img1Path;

                            // Add item to the list with image URL
                            claimedItemsList.add(new ClaimedItem(id, itemName, location, imageUrl));
                        }
                        claimedItemsAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ClaimedActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching data: " + error.toString());
                    Toast.makeText(ClaimedActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }
}
