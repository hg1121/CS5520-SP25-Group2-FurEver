package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnDone;
    private String gptJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        viewPager = findViewById(R.id.view_pager);
        btnDone = findViewById(R.id.btn_done);
        gptJson = getIntent().getStringExtra("genai_result");

        try {
            JSONArray arr = new JSONArray(gptJson);
            List<DogRecommendation> dogList = new ArrayList<>();
            StringBuilder dogBreeds = new StringBuilder();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String breed = obj.getString("breed");
                String matchPercent = obj.getString("match_percent");
                DogRecommendation dog = new DogRecommendation(
                        breed,
                        matchPercent,
                        obj.getString("why"),
                        obj.getString("care_tips")
                );
                dogList.add(dog);
                
                // Build the list of dog breeds for saving
                dogBreeds.append(breed)
                        .append(" (")
                        .append(matchPercent)
                        .append(")")
                        .append(i < arr.length() - 1 ? "\n" : "");
            }

            DogPagerAdapter adapter = new DogPagerAdapter(dogList);
            viewPager.setAdapter(adapter);
            
            // Save dog breeds to Firestore
            saveDogRecommendationsToFirestore(dogBreeds.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        btnDone.setOnClickListener(v -> {
            // Go back to main activity and show About Me tab
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
    
    private void saveDogRecommendationsToFirestore(String recommendations) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            
            Map<String, Object> recommendationData = new HashMap<>();
            recommendationData.put("recommendations", recommendations);
            recommendationData.put("timestamp", new Date());
            
            db.collection("users")
                .document(currentUser.getUid())
                .collection("dogRecommendations")
                .add(recommendationData);
        }
    }
}

