package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PreviewActivity extends AppCompatActivity {

    private TextView tvPreferencesSummary;
    private ProgressBar progressBar;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // Bind views
        tvPreferencesSummary = findViewById(R.id.tv_preferences_summary);
        progressBar = findViewById(R.id.progress_bar);
        btnConfirm = findViewById(R.id.btn_confirm);


        // Button click: show loading, then go to Results
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                btnConfirm.setEnabled(false);


                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PreviewActivity.this, ResultsActivity.class);
                        startActivity(intent);

                        finish();
                    }
                }, 1500);
            }
        });
    }
}
