package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AboutMeFragment extends Fragment {

    private TextView userInfoText;
    private Button btnGetRecommendation;
    private TextView dogRecommendationsText;
    private Button btnLogout;
    private CardView recommendationsCard;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // Initialize views
        userInfoText = view.findViewById(R.id.user_info_text);
        btnGetRecommendation = view.findViewById(R.id.btn_get_recommendation);
        btnLogout = view.findViewById(R.id.btn_logout);
        dogRecommendationsText = view.findViewById(R.id.dog_recommendations_text);
        recommendationsCard = view.findViewById(R.id.recommendations_card);

        // Load user info
        loadUserInfo();

        // Load dog recommendations
        loadDogRecommendations();

        // Set button click listeners
        btnGetRecommendation.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("SHOW_QUESTIONS", true);
            startActivity(intent);
        });

        // log out
        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Redirect to login screen
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Display toast message
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        });


        return view;
    }

    private void loadUserInfo() {
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User userData = document.toObject(User.class);
                                if (userData != null) {
                                    StringBuilder userInfo = new StringBuilder();
                                    userInfo.append("✉️ Email: ").append(userData.getEmail());

                                    if (userData.getAddress() != null && !userData.getAddress().isEmpty()) {
                                        userInfo.append("\n📍 Address: ").append(userData.getAddress());
                                    }

                                    // Add join date if available
                                    if (document.getDate("createdAt") != null) {
                                        userInfo.append("\n🗓️ Joined: ").append(document.getDate("createdAt").toString());
                                    }

                                    userInfoText.setText(userInfo.toString());
                                } else {
                                    userInfoText.setText("✉️ Email: " + user.getEmail());
                                }
                            } else {
                                userInfoText.setText("✉️ Email: " + user.getEmail());
                            }
                        } else {
                            userInfoText.setText("Could not load user information.");
                            Toast.makeText(getContext(), "Error loading user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadDogRecommendations() {
        if (user != null) {
            db.collection("users")
                    .document(user.getUid())
                    .collection("dogRecommendations")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String recommendations = document.getString("recommendations");

                            if (recommendations != null && !recommendations.isEmpty()) {
                                // Make recommendations card visible
                                recommendationsCard.setVisibility(View.VISIBLE);

                                // Format the recommendations with paw bullet points
                                String[] dogBreeds = recommendations.split("\n");
                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                for (String dogBreed : dogBreeds) {
                                    SpannableString spannableString = new SpannableString("    " + dogBreed + "\n\n");
                                    try {
                                        ImageSpan imageSpan = new ImageSpan(requireContext(), R.drawable.paw_bullet);
                                        spannableString.setSpan(imageSpan, 0, 1, 0);
                                    } catch (Exception e) {
                                        // If there's an issue with the image span, continue without it
                                    }
                                    builder.append(spannableString);
                                }

                                dogRecommendationsText.setText(builder);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                    });
        }
    }
} 