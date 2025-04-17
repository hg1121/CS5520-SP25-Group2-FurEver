package com.example.furever;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "post_id";

    private ImageView userImage;
    private TextView userName;
    private TextView postTime;
    private TextView breedText;
    private TextView sexText;
    private TextView ageText;
    private TextView descriptionText;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Post Details");
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        postTime = findViewById(R.id.postTime);
        breedText = findViewById(R.id.breedText);
        sexText = findViewById(R.id.sexText);
        ageText = findViewById(R.id.ageText);
        descriptionText = findViewById(R.id.descriptionText);

        // Get post ID from intent
        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (postId != null) {
            loadPost(postId);
        }
    }

    private void loadPost(String postId) {
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(this::displayPost)
                .addOnFailureListener(e -> {
                    // Handle error
                    finish();
                });
    }

    private void displayPost(DocumentSnapshot document) {
        Post post = document.toObject(Post.class);
        if (post != null) {
            // Load user image
            Glide.with(this)
                    .load(R.drawable.default_profile)
                    .circleCrop()
                    .into(userImage);

            // Set text fields
            userName.setText(post.getUserName());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            postTime.setText(sdf.format(post.getTimestamp()));
            breedText.setText(post.getBreed());
            sexText.setText(post.getSex());
            ageText.setText(post.getAge());
            descriptionText.setText(post.getDescription());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 