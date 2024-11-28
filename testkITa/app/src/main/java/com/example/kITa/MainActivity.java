package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, navLost, navFound, navChat, navNotifications, navProfile;
    private FloatingActionButton fab;
    private TextView todaySeeMore, weekSeeMore, olderSeeMore;

    private RecyclerView todayItemsRecyclerView, weekItemsRecyclerView, olderItemsRecyclerView;
    private TodayItemsAdapter todayItemsAdapter;
    private OlderItemsAdapter weekItemsAdapter, olderItemsAdapter;
    private ArrayList<TodayItem> todayItemsList;
    private ArrayList<OlderItem> weekItemsList, olderItemsList;
    private RequestQueue requestQueue;

    private static final String TAG = "MainActivity";
    private static final String URL = "https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/get_items.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
        setupRecyclerViews();
        fetchItems();
    }

    private void initializeViews() {
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navFound = findViewById(R.id.nav_found);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);
        fab = findViewById(R.id.fab);

        todayItemsRecyclerView = findViewById(R.id.todayItemsRecycleView);
        weekItemsRecyclerView = findViewById(R.id.weekItemsRecycleView);
        olderItemsRecyclerView = findViewById(R.id.olderItemsRecycleView);

        todaySeeMore = findViewById(R.id.todaySeeMore);
        weekSeeMore = findViewById(R.id.weekSeeMore);
        olderSeeMore = findViewById(R.id.olderSeeMore);
    }

    private void setupClickListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        navLost.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });
        navFound.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ClaimedActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatActivity.class)));
        navNotifications.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));
        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReportActivity.class)));

        todaySeeMore.setOnClickListener(v -> openMainActivity2(0));
        weekSeeMore.setOnClickListener(v -> openMainActivity2(1));
        olderSeeMore.setOnClickListener(v -> openMainActivity2(2));
    }

    private void openMainActivity2(int tabIndex) {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.putExtra("TAB_INDEX", tabIndex);
        startActivity(intent);
    }

    private void setupRecyclerViews() {
        todayItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        weekItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        olderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        todayItemsList = new ArrayList<>();
        weekItemsList = new ArrayList<>();
        olderItemsList = new ArrayList<>();

        todayItemsAdapter = new TodayItemsAdapter(todayItemsList, this);
        weekItemsAdapter = new OlderItemsAdapter(weekItemsList, this); // Pass context here
        olderItemsAdapter = new OlderItemsAdapter(olderItemsList, this); // Pass context here

        todayItemsRecyclerView.setAdapter(todayItemsAdapter);
        weekItemsRecyclerView.setAdapter(weekItemsAdapter);
        olderItemsRecyclerView.setAdapter(olderItemsAdapter);

        todayItemsAdapter.setOnItemClickListener(item -> startUnclaimedActivity(item.getId()));
        weekItemsAdapter.setOnItemClickListener(item -> startUnclaimedActivity(item.getId()));
        olderItemsAdapter.setOnItemClickListener(item -> startUnclaimedActivity(item.getId()));
    }

    private void startUnclaimedActivity(int itemId) {
        Intent intent = new Intent(MainActivity.this, UnclaimedActivity.class);
        intent.putExtra("item_id", itemId);
        startActivity(intent);
    }

    private void fetchItems() {
        requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);

                                int id = item.getInt("id_item");
                                String itemName = item.getString("item_name");
                                String location = item.getString("location_found");
                                String img1Path = item.getString("img1");
                                String date = item.getString("report_date");
                                String time = item.getString("report_time");
                                String category = item.getString("category");
                                String status = item.getString("status");
                                String itemDetails = item.getString("other_details");

                                // Properly fetch the image from the uploads directory
                                String imageUrl = "https://hookworm-advanced-shortly.ngrok-free.app/lost_found_db/uploads/img_reported_items/" + img1Path;

                                if (status.equals("Unclaimed")) {
                                    if (category.equals("today")) {
                                        todayItemsList.add(new TodayItem(id, imageUrl, itemName, location, date, time, status, itemDetails));
                                    } else if (category.equals("week")) {
                                        weekItemsList.add(new OlderItem(id, imageUrl, itemName, location, date, time, status, itemDetails));
                                    } else if (category.equals("older")) {
                                        olderItemsList.add(new OlderItem(id, imageUrl, itemName, location, date, time, status, itemDetails));
                                    }
                                }
                            }
                            todayItemsAdapter.notifyDataSetChanged();
                            weekItemsAdapter.notifyDataSetChanged();
                            olderItemsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data: " + error.toString());
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }
}
