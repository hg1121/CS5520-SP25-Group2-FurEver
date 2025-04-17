package com.example.furever;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
    private static final String TAG = "CreatePostActivity";

    private AutoCompleteTextView spinnerBreed, spinnerSex, spinnerAge;
    private TextInputEditText editDescription;
    private Button btnCreatePost;
    private ProgressBar progressBar;
    
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String selectedBreed = "";
    private String selectedSex = "";
    private String selectedAge = "";

    // Predefined lists for dropdowns
    private final List<String> breeds = Arrays.asList(
        "Labrador Retriever", "German Shepherd", "Golden Retriever", 
        "French Bulldog", "Bulldog", "Poodle", "Beagle", 
        "Rottweiler", "Dachshund", "Yorkshire Terrier",
        "Boxer", "Pug", "Siberian Husky", "Shih Tzu",
        "Great Dane", "Doberman Pinscher", "Miniature Schnauzer",
        "Cavalier King Charles Spaniel", "Border Collie", "Mixed Breed"
    );

    private final List<String> sexOptions = Arrays.asList("Male", "Female");

    private final List<String> ageRanges = Arrays.asList(
        "Puppy (0-1 year)", 
        "Young (1-3 years)", 
        "Adult (3-7 years)", 
        "Senior (7+ years)"
    );

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

        if (currentUser == null) {
            Log.e(TAG, "No user is signed in!");
            Toast.makeText(this, "Please sign in to create a post", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize views
        spinnerBreed = findViewById(R.id.spinnerBreed);
        spinnerSex = findViewById(R.id.spinnerSex);
        spinnerAge = findViewById(R.id.spinnerAge);
        editDescription = findViewById(R.id.editDescription);
        btnCreatePost = findViewById(R.id.btnCreatePost);
        progressBar = findViewById(R.id.progressBar);

        // Setup dropdown adapters
        setupDropdowns();

        // Setup click listener
        btnCreatePost.setOnClickListener(v -> createPost());
    }

    private void setupDropdowns() {
        try {
            // Setup breed dropdown
            ArrayAdapter<String> breedAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, breeds);
            spinnerBreed.setAdapter(breedAdapter);
            spinnerBreed.setOnItemClickListener((parent, view, position, id) -> {
                selectedBreed = breeds.get(position);
                Log.d(TAG, "Selected breed: " + selectedBreed);
            });

            // Setup sex dropdown
            ArrayAdapter<String> sexAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, sexOptions);
            spinnerSex.setAdapter(sexAdapter);
            spinnerSex.setOnItemClickListener((parent, view, position, id) -> {
                selectedSex = sexOptions.get(position);
                Log.d(TAG, "Selected sex: " + selectedSex);
            });

            // Setup age dropdown
            ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(
                this, R.layout.dropdown_item, ageRanges);
            spinnerAge.setAdapter(ageAdapter);
            spinnerAge.setOnItemClickListener((parent, view, position, id) -> {
                selectedAge = ageRanges.get(position);
                Log.d(TAG, "Selected age: " + selectedAge);
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up dropdowns: ", e);
            Toast.makeText(this, "Error setting up form", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPost() {
        Log.d(TAG, "Attempting to create post...");
        
        // Use the selected values instead of getText()
        String breed = selectedBreed;
        String sex = selectedSex;
        String age = selectedAge;
        String description = editDescription.getText().toString().trim();

        Log.d(TAG, "Values - Breed: " + breed + ", Sex: " + sex + ", Age: " + age);

        if (breed.isEmpty() || sex.isEmpty() || age.isEmpty() || description.isEmpty()) {
            String message = "Please fill in all fields";
            Log.w(TAG, "Validation failed: " + message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnCreatePost.setEnabled(false);

        try {
            // Create and save post
            Post post = new Post(currentUser.getUid(), currentUser.getEmail(), breed, sex, age, description);
            
            Log.d(TAG, "Saving post to Firestore...");
            db.collection("posts")
                    .add(post)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Post created with ID: " + documentReference.getId());
                        Toast.makeText(CreatePostActivity.this, "Post created successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error creating post: ", e);
                        progressBar.setVisibility(View.GONE);
                        btnCreatePost.setEnabled(true);
                        Toast.makeText(CreatePostActivity.this, "Failed to create post: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in createPost: ", e);
            progressBar.setVisibility(View.GONE);
            btnCreatePost.setEnabled(true);
            Toast.makeText(this, "Error creating post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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