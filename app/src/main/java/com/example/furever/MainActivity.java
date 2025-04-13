package com.example.furever;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private QuestionPagerAdapter adapter;
    //global instance to store dogPreference information
    private DogPreference dogPreference = new DogPreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        }

        // Setup bottom navigation
        bottomNavigationView.setOnItemSelectedListener(this);
        
        // Check if we should show questions
        if (getIntent().getBooleanExtra("SHOW_QUESTIONS", false)) {
            showQuestionsScreen();
        } else {
            // Default to Posts fragment
            loadFragment(new PostsFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_posts);
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.navigation_posts) {
            loadFragment(new PostsFragment());
            return true;
        } else if (itemId == R.id.navigation_about_me) {
            loadFragment(new AboutMeFragment());
            return true;
        }
        
        return false;
    }
    
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    
    private void showQuestionsScreen() {
        // Initialize ViewPager for questions
        viewPager = new ViewPager2(this);
        viewPager.setId(View.generateViewId());
        
        // Add the viewPager to the fragment container
        findViewById(R.id.fragment_container).setVisibility(android.view.View.GONE);
        
        // Add viewPager dynamically
        androidx.constraintlayout.widget.ConstraintLayout mainContainer = findViewById(R.id.mainContainer);
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params = new androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_PARENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        params.topToTop = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToTop = R.id.bottom_navigation;
        params.leftToLeft = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        params.rightToRight = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;
        
        mainContainer.addView(viewPager, params);
        
        adapter = new QuestionPagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(1 - Math.abs(position));
            page.setTranslationX(-position * page.getWidth());
        });

    }


    public DogPreference getDogPreference() {
        return dogPreference;
    }

    public void goToNextQuestion() {
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(currentItem + 1, true);
            }
        }
    }

    public void goToPrevQuestion() {
        if (viewPager != null) {
            int currentItem = viewPager.getCurrentItem();
            if (currentItem > 0) {
                viewPager.setCurrentItem(currentItem - 1, true);
            }
        }
    }

    
    // Helper method to save dog recommendations to Firestore
    public void saveDogRecommendationsToFirestore(String recommendations) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            
            Map<String, Object> recommendationData = new HashMap<>();
            recommendationData.put("recommendations", recommendations);
            recommendationData.put("timestamp", new Date());
            
            db.collection("users")
                .document(currentUser.getUid())
                .collection("dogRecommendations")
                .add(recommendationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MainActivity", "Dog recommendations saved with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error saving dog recommendations", e);
                });
        }
    }
}
