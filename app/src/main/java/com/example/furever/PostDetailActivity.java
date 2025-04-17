package com.example.furever;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailActivity";
    public static final String EXTRA_POST_ID = "post_id";

    private ImageView userImage;
    private TextView userName, postTime, breedText, sexText, ageText, descriptionText;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String postId;
    private Post currentPost;

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
        auth = FirebaseAuth.getInstance();

        // Initialize views
        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        postTime = findViewById(R.id.postTime);
        breedText = findViewById(R.id.breedText);
        sexText = findViewById(R.id.sexText);
        ageText = findViewById(R.id.ageText);
        descriptionText = findViewById(R.id.descriptionText);

        // Get post ID from intent
        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (postId != null) {
            loadPost(postId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show delete option if user is the post owner
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null && currentPost != null && 
            currentPost.getUserId().equals(currentUser.getUid())) {
            getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPost(String postId) {
        if (postId.startsWith("sample_post_")) {
            // Handle sample post
            Post samplePost = null;
            int postNumber = Integer.parseInt(postId.substring("sample_post_".length()));
            
            switch (postNumber) {
                case 1:
                    samplePost = new Post(
                        "sample_user_1",
                        "Sarah Johnson",
                        "Golden Retriever",
                        "Female",
                        "Young (1-3 years)",
                        "Looking for a loving home for Luna, a friendly and energetic Golden Retriever. " +
                        "She's great with kids and other dogs. Fully vaccinated and trained."
                    );
                    break;
                case 2:
                    samplePost = new Post(
                        "sample_user_2",
                        "Mike Wilson",
                        "German Shepherd",
                        "Male",
                        "Puppy (0-1 year)",
                        "Max is a 6-month-old German Shepherd puppy looking for an active family. " +
                        "He's already showing great potential in basic training and loves to learn."
                    );
                    break;
                case 3:
                    samplePost = new Post(
                        "sample_user_3",
                        "Emily Chen",
                        "French Bulldog",
                        "Male",
                        "Adult (3-7 years)",
                        "Meet Charlie, a calm and affectionate Frenchie who loves cuddles. " +
                        "Perfect for apartment living and great with families."
                    );
                    break;
                case 4:
                    samplePost = new Post(
                        "sample_user_4",
                        "David Brown",
                        "Labrador Retriever",
                        "Female",
                        "Senior (7+ years)",
                        "Sweet senior Lab named Bella seeking a quiet home to spend her golden years. " +
                        "Well-behaved and gentle with everyone she meets."
                    );
                    break;
                case 5:
                    samplePost = new Post(
                        "sample_user_5",
                        "Lisa Martinez",
                        "Border Collie",
                        "Male",
                        "Young (1-3 years)",
                        "Cooper is a highly intelligent Border Collie looking for an active home. " +
                        "Great for agility training and outdoor activities."
                    );
                    break;
            }
            
            if (samplePost != null) {
                samplePost.setId(postId);
                samplePost.setTimestamp(new Date(System.currentTimeMillis() - ((postNumber - 1) * 86400000L)));
                displayPost(samplePost);
            }
        } else {
            // Handle regular post from Firestore
            db.collection("posts").document(postId)
                    .get()
                    .addOnSuccessListener(document -> {
                        currentPost = document.toObject(Post.class);
                        if (currentPost != null) {
                            currentPost.setId(document.getId());
                            displayPost(currentPost);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading post", e);
                        Toast.makeText(this, "Error loading post", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }

    private void displayPost(Post post) {
        currentPost = post;
        
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

        // Check if current user is the post owner and update menu
        invalidateOptionsMenu();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deletePost() {
        if (postId == null) return;

        db.collection("posts").document(postId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting post", e);
                    Toast.makeText(this, "Error deleting post: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
    }
} 