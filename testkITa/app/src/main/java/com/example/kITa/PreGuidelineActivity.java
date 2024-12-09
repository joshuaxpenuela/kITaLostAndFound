package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class PreGuidelineActivity extends AppCompatActivity {

    private ImageButton guideIcon;
    private ImageButton searchIcon;
    private ImageButton kitaLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_preguideline); // Linking to fragment_guideline.xml

        // Initialize ImageButtons BEFORE calling toggleNavigationBasedOnEmail()
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        kitaLogo = findViewById(R.id.kitaLogo);


        // Set onClickListeners
        guideIcon.setOnClickListener(v -> {
            // Since guideIcon is already on the GuidelinesActivity, you can refresh it if needed
            finish();
            startActivity(getIntent());
        });

        searchIcon.setOnClickListener(v -> {
            Intent intent = new Intent(PreGuidelineActivity.this, PreSearchActivity.class);
            startActivity(intent);
        });

        kitaLogo.setOnClickListener(v -> {
            Intent intent = new Intent(PreGuidelineActivity.this, PreMenuActivity.class);
            startActivity(intent);
        });
    }

}