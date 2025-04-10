package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    private ImageView imgMatch1, imgMatch2, imgMatch3;
    private TextView tvMatch1Name, tvMatch1Percent;
    private TextView tvMatch2Name, tvMatch2Percent;
    private TextView tvMatch3Name, tvMatch3Percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Bind ImageView and TextView elements from the layout
        imgMatch1 = findViewById(R.id.img_match1);
        imgMatch2 = findViewById(R.id.img_match2);
        imgMatch3 = findViewById(R.id.img_match3);

        tvMatch1Name = findViewById(R.id.tv_match1_name);
        tvMatch1Percent = findViewById(R.id.tv_match1_percent);

        tvMatch2Name = findViewById(R.id.tv_match2_name);
        tvMatch2Percent = findViewById(R.id.tv_match2_percent);

        tvMatch3Name = findViewById(R.id.tv_match3_name);
        tvMatch3Percent = findViewById(R.id.tv_match3_percent);

        // For demonstration, we set static text.
        tvMatch1Name.setText("Husky");
        tvMatch1Percent.setText("95%");
        tvMatch2Name.setText("Golden Retriever");
        tvMatch2Percent.setText("88%");
        tvMatch3Name.setText("Border Collie");
        tvMatch3Percent.setText("87%");

        // Set click listeners on the placeholder images to open the detail screen
        imgMatch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity("Husky", "95%");
            }
        });

        imgMatch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity("Golden Retriever", "88%");
            }
        });

        imgMatch3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDetailActivity("Border Collie", "87%");
            }
        });
    }

    private void openDetailActivity(String breedName, String matchPercent) {
        Intent intent = new Intent(ResultsActivity.this, DogDetailActivity.class);
        intent.putExtra("BREED_NAME", breedName);
        intent.putExtra("MATCH_PERCENT", matchPercent);
        startActivity(intent);
    }
}
