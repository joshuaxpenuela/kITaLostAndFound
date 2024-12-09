package com.example.kITa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PreSearchActivity extends AppCompatActivity {

    private ImageButton guideIcon, searchIcon, kitaLogo;
    private EditText searchEditText;
    private ImageButton searchFilterBtn;
    private RecyclerView recentSearchesRecyclerView;
    private RecyclerView searchResultsRecyclerView;
    private RecentSearchAdapter recentSearchAdapter;
    private SearchResultAdapter searchResultAdapter;
    private List<String> recentSearches;
    private List<SearchItem> searchResults;
    private RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_presearch);

        initializeViews();
        setupClickListeners();
        searchEditText = findViewById(R.id.search);
        searchFilterBtn = findViewById(R.id.searchFilter);
        recentSearchesRecyclerView = findViewById(R.id.recentSearchesRecyclerView);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        recentSearches = new ArrayList<>();
        searchResults = new ArrayList<>();

        recentSearchAdapter = new RecentSearchAdapter(recentSearches, this::removeRecentSearch);
        searchResultAdapter = new SearchResultAdapter(searchResults, this::onSearchItemClick);

        recentSearchesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recentSearchesRecyclerView.setAdapter(recentSearchAdapter);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(searchResultAdapter);

        requestQueue = Volley.newRequestQueue(this);

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        searchFilterBtn.setOnClickListener(v -> showFilterDialog());
    }

    private void initializeViews() {
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        kitaLogo = findViewById(R.id.kitaLogo);
    }

    private void setupClickListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(PreSearchActivity.this, PreGuidelineActivity.class)));
        searchIcon.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });
        kitaLogo.setOnClickListener(v -> startActivity(new Intent(PreSearchActivity.this, PreMenuActivity.class)));
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            // Add to recent searches
            if (!recentSearches.contains(query)) {
                recentSearches.add(0, query);
                recentSearchAdapter.notifyDataSetChanged();
            }
            // Perform search and update results
            searchItems(query, "");
        }
    }

    private void searchItems(String query, String category) {
        String url = "http://10.0.2.2/lost_found_db/search_items.php?search=" + query;
        if (!category.isEmpty()) {
            url += "&category=" + category;
        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    searchResults.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            searchResults.add(new SearchItem(
                                    item.getInt("id_item"),
                                    item.getString("item_name"),
                                    item.getString("location_found"),
                                    "http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + item.getString("img1"),
                                    item.getString("other_details")
                            ));
                        }
                        searchResultAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(PreSearchActivity.this, "Error fetching search results", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void removeRecentSearch(int position) {
        recentSearches.remove(position);
        recentSearchAdapter.notifyItemRemoved(position);
    }

    private void onSearchItemClick(SearchItem item) {
        Intent intent = new Intent(this, PreUnclaimedActivity.class);
        intent.putExtra("item_id", item.getId());
        startActivity(intent);
    }

    private void showFilterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogbox_searchfilter, null);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(dialogView);

        Spinner categorySpinner = dialogView.findViewById(R.id.itemCategory);
        Button showResultsBtn = dialogView.findViewById(R.id.showFilteredResult);
        Button cancelBtn = dialogView.findViewById(R.id.cancelFilteredResult);

        showResultsBtn.setOnClickListener(v -> {
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            searchItems(searchEditText.getText().toString().trim(), selectedCategory);
            dialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
