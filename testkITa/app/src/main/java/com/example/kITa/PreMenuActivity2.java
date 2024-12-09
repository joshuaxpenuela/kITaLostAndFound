package com.example.kITa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PreMenuActivity2 extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton guideIcon, searchIcon, kitaLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_premenu2);

        initializeViews();
        setupViewPager();
        setupTabLayout();
        setupClickListeners();

        int tabIndex = getIntent().getIntExtra("TAB_INDEX", 0);
        viewPager.setCurrentItem(tabIndex);
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        guideIcon = findViewById(R.id.guide_icon);
        searchIcon = findViewById(R.id.search_icon);
        kitaLogo = findViewById(R.id.kitaLogo);
    }

    private void setupViewPager() {
        PreItemsPagerAdapter pagerAdapter = new PreItemsPagerAdapter(this);
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
        guideIcon.setOnClickListener(v -> startActivity(new Intent(PreMenuActivity2.this, PreGuidelineActivity.class)));
        searchIcon.setOnClickListener(v -> startActivity(new Intent(PreMenuActivity2.this, PreSearchActivity.class)));
        kitaLogo.setOnClickListener(v -> startActivity(new Intent(PreMenuActivity2.this, PreMenuActivity.class)));
    }
}