package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity2 extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton guideIcon, searchIcon, navLost, navChat, navNotifications, navProfile;
    private ExtendedFloatingActionButton reportFound, reportLost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        UserSession userSession = UserSession.getInstance(this);

        // Check if user is logged in
        if (!userSession.isLoggedIn()) {
            Toast.makeText(this, "Please log-in your account.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close the current activity
            return;
        }

        initializeViews();
        setupViewPager();
        setupTabLayout();
        setupClickListeners();
        toggleNavigationBasedOnEmail();

        int tabIndex = getIntent().getIntExtra("TAB_INDEX", 0);
        viewPager.setCurrentItem(tabIndex);
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        navLost = findViewById(R.id.nav_lost);
        navChat = findViewById(R.id.nav_chat);
        navNotifications = findViewById(R.id.nav_notifications);
        navProfile = findViewById(R.id.nav_profile);
        reportFound = findViewById(R.id.reportFound);
        reportLost = findViewById(R.id.reportLost);
    }

    private void toggleNavigationBasedOnEmail() {
        boolean isEmailEmpty = TextUtils.isEmpty(UserSession.getInstance(this).getEmail());
        navChat.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
        navNotifications.setVisibility(isEmailEmpty ? View.GONE : View.VISIBLE);
    }

    private void setupViewPager() {
        ItemsPagerAdapter pagerAdapter = new ItemsPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Today");
                            break;
                        case 1:
                            tab.setText("7 days");
                            break;
                        case 2:
                            tab.setText("Older");
                            break;
                    }
                }).attach();
    }

    private void setupClickListeners() {
        guideIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, GuidelinesActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, SearchActivity.class)));
        navLost.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(MainActivity2.this, MainActivity.class));
        });
        navChat.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, ChatActivity.class)));
        navNotifications.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, NotificationActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, ProfileActivity.class)));
        reportFound.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, ReportFoundActivity.class)));
        reportLost.setOnClickListener(v -> startActivity(new Intent(MainActivity2.this, ReportLostActivity.class)));
    }
}