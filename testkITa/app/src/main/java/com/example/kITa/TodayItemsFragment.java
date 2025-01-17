package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class TodayItemsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TodayAllItemsAdapter adapter;
    private ArrayList<TodayItem> itemsList;
    private RequestQueue requestQueue;

    private static final String URL = "http://10.0.2.2/lost_found_db/get_items.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        recyclerView = view.findViewById(R.id.itemsRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        fetchItems();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemsList = new ArrayList<>();
        adapter = new TodayAllItemsAdapter(itemsList, getContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(getActivity(), UnclaimedActivity.class);
            intent.putExtra("item_id", item.getId());
            startActivity(intent);
        });
    }

    private void fetchItems() {
        requestQueue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);
                                String category = item.getString("category");

                                if (category.equals("today")) {
                                    int id = item.getInt("id_item");
                                    String itemName = item.getString("item_name");
                                    String location = item.getString("location_found");
                                    String img1Path = item.getString("img1");
                                    String date = item.getString("report_date");
                                    String time = item.getString("report_time");
                                    String status = item.getString("status");
                                    String itemDetails = item.getString("other_details");

                                    // Load image URL properly
                                    String imageUrl = "http://10.0.2.2/lost_found_db/uploads/img_reported_items/" + img1Path;

                                    itemsList.add(new TodayItem(id, imageUrl, itemName, location, date, time, status, itemDetails));
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(request);
    }
}
