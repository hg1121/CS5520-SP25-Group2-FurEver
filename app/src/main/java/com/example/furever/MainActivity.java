package com.example.furever;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    TextView textView;
    Button btn_logout;
    private ViewPager2 viewPager;
    private QuestionPagerAdapter adapter;
    //global instance to store dogPreference information
    private DogPreference dogPreference = new DogPreference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        textView = findViewById(R.id.user_details);
        btn_logout = findViewById(R.id.btn_log_out);

        if (user == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
            return;
        } else {
            // Fetch user data from the database
            db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User userData = document.toObject(User.class);
                            if (userData != null) {
                                StringBuilder userInfo = new StringBuilder();
                                userInfo.append("Email: ").append(userData.getEmail());
                                
                                if (userData.getAddress() != null && !userData.getAddress().isEmpty()) {
                                    userInfo.append("\nAddress: ").append(userData.getAddress());
                                }
                                
                                textView.setText(userInfo.toString());
                            } else {
                                textView.setText("Email: " + user.getEmail());
                            }
                        } else {
                            // Document doesn't exist, create it
                            User newUser = new User(user.getUid(), user.getEmail());
                            db.collection("users").document(user.getUid())
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    textView.setText("Email: " + user.getEmail());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("MainActivity", "Error creating user document", e);
                                    textView.setText("Error creating user profile");
                                });
                        }
                    } else {
                        Log.e("MainActivity", "Error loading user data", task.getException());
                        textView.setText("Error loading user data. Please try again later.");
                    }
                });
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

        loadUserPreferences();
    }

    private void loadUserPreferences() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users")
                .document(currentUser.getUid())
                .collection("preferences")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        dogPreference.size = document.getString("size");
                        dogPreference.exercise = document.getString("exercise");
                        dogPreference.coatLength = document.getString("coatLength");
                        dogPreference.homeType = document.getString("homeType");
                        dogPreference.haveChildren = document.getString("haveChildren");
                        dogPreference.budget = document.getString("budget");
                        
                        Toast.makeText(MainActivity.this, 
                            "Previous preferences loaded", 
                            Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error loading preferences", e);
                });
        }
    }

    public DogPreference getDogPreference() {
        return dogPreference;
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

    private void savePreferencesToFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            
            // Create a map with the preference data
            Map<String, Object> preferenceData = new HashMap<>();
            preferenceData.put("size", dogPreference.size);
            preferenceData.put("exercise", dogPreference.exercise);
            preferenceData.put("coatLength", dogPreference.coatLength);
            preferenceData.put("homeType", dogPreference.homeType);
            preferenceData.put("haveChildren", dogPreference.haveChildren);
            preferenceData.put("budget", dogPreference.budget);
            preferenceData.put("timestamp", new Date()); // Add timestamp for ordering
            
            // Save to Firestore
            db.collection("users")
                .document(currentUser.getUid())
                .collection("preferences")
                .add(preferenceData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("MainActivity", "Preferences saved with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error saving preferences", e);
                });
        }
    }
}
