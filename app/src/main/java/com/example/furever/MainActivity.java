package com.example.furever;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    TextView textView;
    Button btn_logout;
    private ViewPager2 viewPager;
    private QuestionPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        textView = findViewById(R.id.user_details);
        btn_logout = findViewById(R.id.btn_log_out);

        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        } else {
            textView.setText(user.getEmail());
        }

        btn_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        viewPager = findViewById(R.id.viewPager);
        adapter = new QuestionPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(1 - Math.abs(position));
            page.setTranslationX(-position * page.getWidth());
        });
    }

    public void goToNextQuestion() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem < adapter.getItemCount() - 1) {
            viewPager.setCurrentItem(currentItem + 1, true);
        }
    }

    public void goToPrevQuestion() {
        int currentItem = viewPager.getCurrentItem();
        if (currentItem > 0) {
            viewPager.setCurrentItem(currentItem - 1, true);
        }
    }
}
