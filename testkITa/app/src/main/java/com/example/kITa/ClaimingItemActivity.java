package com.example.kITa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClaimingItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String SUBMIT_CLAIM_URL = "http://10.0.2.2/lost_found_db/submit_claim.php";

    private ImageButton proofOfID, proofOfOwnership;
    private EditText descriptionText;
    private Button cancelClaim, submitClaim;
    private int itemId;
    private Uri validIDUri;
    private Uri proofOfOwnershipUri;
    private String currentImageType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_claimingitem);

        UserSession userSession = UserSession.getInstance(this);

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Please log-in your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close the current activity
            return;
        }

        itemId = getIntent().getIntExtra("item_id", -1);
        if (itemId == -1) {
            Toast.makeText(this, "Error: Item not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        proofOfID = findViewById(R.id.proofOfID);
        proofOfOwnership = findViewById(R.id.proofOfOwnership);
        descriptionText = findViewById(R.id.DescriptionText);
        cancelClaim = findViewById(R.id.cancelClaim);
        submitClaim = findViewById(R.id.submitClaim);

        // Initialize navigation buttons
        ImageButton guideIcon = findViewById(R.id.guide_icon);
        ImageButton searchIcon = findViewById(R.id.search_icon);
        ImageButton navLost = findViewById(R.id.nav_lost);
        ImageButton navChat = findViewById(R.id.nav_chat);
        ImageButton navNotifications = findViewById(R.id.nav_notifications);
        ImageButton navProfile = findViewById(R.id.nav_profile);

        // Set click listeners for navigation buttons
        guideIcon.setOnClickListener(v -> startActivity(new Intent(ClaimingItemActivity.this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(ClaimingItemActivity.this, SearchActivity.class)));
        navLost.setOnClickListener(v -> startActivity(new Intent(ClaimingItemActivity.this, MainActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(ClaimingItemActivity.this, ChatActivity.class)));
        navNotifications.setOnClickListener(v -> startActivity(new Intent(ClaimingItemActivity.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(ClaimingItemActivity.this, ProfileActivity.class)));
    }

    private void setupClickListeners() {
        proofOfID.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            currentImageType = "validID";
        });

        proofOfOwnership.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            currentImageType = "proofOfOwnership";
        });

        cancelClaim.setOnClickListener(v -> {
            Intent intent = new Intent(ClaimingItemActivity.this, UnclaimedActivity.class);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
            finish();
        });

        submitClaim.setOnClickListener(v -> {
            if (validIDUri == null) {
                Toast.makeText(this, "Please select a valid ID photo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (descriptionText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }
            submitClaim();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            if (currentImageType.equals("validID")) {
                validIDUri = selectedImageUri;
                loadImageWithGlide(validIDUri, proofOfID);
            } else if (currentImageType.equals("proofOfOwnership")) {
                proofOfOwnershipUri = selectedImageUri;
                loadImageWithGlide(proofOfOwnershipUri, proofOfOwnership);
            }
        }
    }

    private void loadImageWithGlide(Uri uri, ImageButton targetImageButton) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(this)
                .load(uri)
                .apply(options)
                .into(targetImageButton);
    }

    private void submitClaim() {
        UserSession userSession = UserSession.getInstance(this);
        String description = descriptionText.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SUBMIT_CLAIM_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.has("message")) {
                            Toast.makeText(ClaimingItemActivity.this,
                                    jsonResponse.getString("message"),
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(ClaimingItemActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (jsonResponse.has("error")) {
                            Toast.makeText(ClaimingItemActivity.this,
                                    "Error: " + jsonResponse.getString("error"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ClaimSubmission", "JSON parsing error", e);
                        Toast.makeText(ClaimingItemActivity.this,
                                "Unexpected response from server",
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("ClaimSubmission", "Error submitting claim", error);
                    String errorMessage = "Unknown error occurred";

                    if (error.networkResponse != null) {
                        try {
                            String errorBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject errorJson = new JSONObject(errorBody);

                            if (errorJson.has("error")) {
                                errorMessage = errorJson.getString("error");
                            }
                        } catch (Exception e) {
                            Log.e("ClaimSubmission", "Error parsing error response", e);
                        }
                    }

                    Toast.makeText(ClaimingItemActivity.this,
                            "Error: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_item", String.valueOf(itemId));
                params.put("validID", imageUriToBase64(validIDUri));
                params.put("ProofOwner", imageUriToBase64(proofOfOwnershipUri));
                params.put("claim_desc", description);
                params.put("claim_Fname", userSession.getFirstName());
                params.put("claim_Lname", userSession.getLastName());
                params.put("claim_email", userSession.getEmail());
                params.put("claim_contact", userSession.getContactNo());
                params.put("claim_dept", userSession.getDept());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };

        // Set a retry policy to handle potential timeout issues
        int socketTimeout = 30000; // 30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String imageUriToBase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}