package com.example.furever;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.widget.ImageView;
import android.widget.TextView;

public class DogDetailActivity extends AppCompatActivity {

    private ImageView imgBreed;
    private TextView tvBreedName, tvMatchPercent, tvWhyMatch, tvCareTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        imgBreed = findViewById(R.id.img_breed);
        tvBreedName = findViewById(R.id.tv_breed_name);
        tvMatchPercent = findViewById(R.id.tv_match_percent);
        tvWhyMatch = findViewById(R.id.tv_why_match);
        tvCareTips = findViewById(R.id.tv_care_tips);

        String breedName = getIntent().getStringExtra("BREED_NAME");
        String matchPercent = getIntent().getStringExtra("MATCH_PERCENT");

        tvBreedName.setText(breedName);
        tvMatchPercent.setText(matchPercent + " Match");
        tvWhyMatch.setText("Why it matches you:\nLorem ipsum dolor sit amet, consectetur adipiscing elit.");
        tvCareTips.setText("Care Tips:\nKeep it active and groom regularly.");

        imgBreed.setImageResource(R.drawable.placeholder_image);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
