package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PreviewActivity extends AppCompatActivity {

    private TextView tvPreferencesSummary;
    private ProgressBar progressBar;
    private Button btnConfirm;

    private DogPreference dogPreference; // 用户偏好对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // 绑定视图 / Bind views
        tvPreferencesSummary = findViewById(R.id.tv_preferences_summary);
        progressBar = findViewById(R.id.progress_bar);
        btnConfirm = findViewById(R.id.btn_confirm);

        // 获取从 Q6Fragment 传来的偏好对象 / Receive DogPreference from previous activity
        dogPreference = (DogPreference) getIntent().getSerializableExtra("dog_pref");
        Log.d("PreviewActivity", "exercise = " + dogPreference.exercise);
        Log.d("PreviewActivity", "homeType = " + dogPreference.homeType);

        // 构造偏好总结内容 / Build preview summary
        String summary = buildPreferenceSummary(dogPreference);
        tvPreferencesSummary.setText(summary);

        // 点击确认：显示加载动画，跳转到结果页 / Confirm button behavior
        btnConfirm.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            btnConfirm.setEnabled(false);

            v.postDelayed(() -> {
                // 启动结果页并传递偏好数据 / Pass preference to ResultsActivity
                Intent intent = new Intent(PreviewActivity.this, ResultsActivity.class);
                intent.putExtra("dog_pref", dogPreference);
                startActivity(intent);
                finish();
            }, 1500);
        });
    }

    // 构建用户偏好摘要文本 / Build readable summary of user selections
    private String buildPreferenceSummary(DogPreference pref) {
        return
                "Dog Size: " + pref.size + "\n" +
                        "Exercise Time: " + pref.exercise + "\n" +
                        "Coat Length: " + pref.coatLength + "\n" +
                        "Home Type: " + pref.homeType + "\n" +
                        "Has Children: " + pref.haveChildren + "\n" +
                        "Monthly Budget: " + pref.budget;
    }
}
