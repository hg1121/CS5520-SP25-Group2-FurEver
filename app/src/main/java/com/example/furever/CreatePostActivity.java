package com.example.furever;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreatePostActivity extends AppCompatActivity {

    private TextInputEditText editBreed, editSex, editAge, editDescription;
    private Button btnCreatePost;
    private ProgressBar progressBar;
    
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Post");
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        editBreed = findViewById(R.id.editBreed);
        editSex = findViewById(R.id.editSex);
        editAge = findViewById(R.id.editAge);
        editDescription = findViewById(R.id.editDescription);
        btnCreatePost = findViewById(R.id.btnCreatePost);
        progressBar = findViewById(R.id.progressBar);

        // Setup click listener
        btnCreatePost.setOnClickListener(v -> createPost());
    }

    private void createPost() {
        String breed = editBreed.getText().toString().trim();
        String sex = editSex.getText().toString().trim();
        String age = editAge.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (breed.isEmpty() || sex.isEmpty() || age.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnCreatePost.setEnabled(false);

        // Create and save post
        Post post = new Post(currentUser.getUid(), currentUser.getEmail(), breed, sex, age, description);

        db.collection("posts")
                .add(post)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreatePostActivity.this, "Post created successfully",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnCreatePost.setEnabled(true);
                    Toast.makeText(CreatePostActivity.this, "Failed to create post: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
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