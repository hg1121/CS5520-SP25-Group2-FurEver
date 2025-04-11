package com.example.furever;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        viewPager = findViewById(R.id.view_pager);
        String gptJson = getIntent().getStringExtra("genai_result");

        try {
            JSONArray arr = new JSONArray(gptJson);
            List<DogRecommendation> dogList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                DogRecommendation dog = new DogRecommendation(
                        obj.getString("breed"),
                        obj.getString("match_percent"),
                        obj.getString("why"),
                        obj.getString("care_tips")
                );
                dogList.add(dog);
            }

            DogPagerAdapter adapter = new DogPagerAdapter(dogList);
            viewPager.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

