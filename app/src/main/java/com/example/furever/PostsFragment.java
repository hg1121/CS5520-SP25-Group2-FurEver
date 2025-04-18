package com.example.furever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PostsFragment extends Fragment implements PostAdapter.OnPostClickListener {
    private static final String TAG = "PostsFragment";

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        FloatingActionButton fabAddPost = view.findViewById(R.id.fabAddPost);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(this);
        recyclerView.setAdapter(adapter);

        // Setup SwipeRefreshLayout
        swipeRefresh.setOnRefreshListener(this::loadPosts);

        // Setup FAB
        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreatePostActivity.class);
            startActivity(intent);
        });

        // Load initial data
        loadPosts();
    }

    private void loadPosts() {
        // Check if fragment is attached to activity
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) {
                        return;
                    }

                    List<Post> posts = new ArrayList<>();
                    
                    // Add user posts
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setId(document.getId());
                        posts.add(post);
                    }

                    // Add sample posts
                    List<Post> samplePosts = getSamplePosts();
                    posts.addAll(samplePosts);

                    if (adapter != null) {
                        adapter.setPosts(posts);
                    }

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading posts: ", e);
                    if (!isAdded()) {
                        return;
                    }

                    Context ctx = getContext();
                    if (ctx != null) {
                        Toast.makeText(ctx, "Error loading posts: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                });
    }

    private List<Post> getSamplePosts() {
        List<Post> samplePosts = Arrays.asList(
            new Post(
                "sample_user_1",
                "Sarah Johnson",
                "Golden Retriever",
                "Female",
                "Young (1-3 years)",
                "Looking for a loving home for Luna, a friendly and energetic Golden Retriever. " +
                "She's great with kids and other dogs. Fully vaccinated and trained.",
                "123 Main St, Boston, MA 02115",
                42.3601,
                -71.0589
            ),
            new Post(
                "sample_user_2",
                "Mike Wilson",
                "German Shepherd",
                "Male",
                "Puppy (0-1 year)",
                "Max is a 6-month-old German Shepherd puppy looking for an active family. " +
                "He's already showing great potential in basic training and loves to learn.",
                "456 Park Ave, New York, NY 10022",
                40.7128,
                -74.0060
            ),
            new Post(
                "sample_user_3",
                "Emily Chen",
                "French Bulldog",
                "Male",
                "Adult (3-7 years)",
                "Meet Charlie, a calm and affectionate Frenchie who loves cuddles. " +
                "Perfect for apartment living and great with families.",
                "789 Ocean Blvd, Miami, FL 33139",
                25.7617,
                -80.1918
            ),
            new Post(
                "sample_user_4",
                "David Brown",
                "Labrador Retriever",
                "Female",
                "Senior (7+ years)",
                "Sweet senior Lab named Bella seeking a quiet home to spend her golden years. " +
                "Well-behaved and gentle with everyone she meets.",
                "321 Highland Dr, Seattle, WA 98101",
                47.6062,
                -122.3321
            ),
            new Post(
                "sample_user_5",
                "Lisa Martinez",
                "Border Collie",
                "Male",
                "Young (1-3 years)",
                "Cooper is a highly intelligent Border Collie looking for an active home. " +
                "Great for agility training and outdoor activities.",
                "555 River Rd, Chicago, IL 60601",
                41.8781,
                -87.6298
            )
        );

        // Set IDs and timestamps for sample posts
        for (int i = 0; i < samplePosts.size(); i++) {
            Post post = samplePosts.get(i);
            post.setId("sample_post_" + (i + 1));
            post.setTimestamp(new Date(System.currentTimeMillis() - (i * 86400000L))); // Spread over last few days
        }

        return samplePosts;
    }

    @Override
    public void onPostClick(Post post) {
        Context context = getContext();
        if (context != null && post != null) {
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getId());
            intent.putExtra("IS_SAMPLE_POST", post.getId().startsWith("sample_post_"));
            startActivity(intent);
        }
    }

    @Override
    public void onUserImageClick(String userId, String userName) {
        if (userId == null || userId.isEmpty() || userId.startsWith("sample_user_")) {
            // Show sample user email
            String email = userName.toLowerCase().replace(" ", ".") + "@example.com";
            showUserEmailDialog(userName, email);
            return;
        }

        // Show loading toast
        if (getContext() != null) {
            Toast.makeText(getContext(), "Loading contact information...", Toast.LENGTH_SHORT).show();
        }

        // For real users, fetch their email from Firestore
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getEmail() != null) {
                        showUserEmailDialog(userName, user.getEmail());
                    } else {
                        // Handle case where user object is invalid
                        showUserEmailDialog(userName, userId + "@example.com");
                    }
                } else {
                    // Handle case where document doesn't exist
                    Log.d(TAG, "User document not found for ID: " + userId);
                    showUserEmailDialog(userName, userId + "@example.com");
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching user data: " + e.getMessage(), e);
                // Fallback to showing a generic email
                if (getContext() != null) {
                    showUserEmailDialog(userName, userId + "@example.com");
                }
            });
    }

    private void showUserEmailDialog(String userName, String email) {
        Context context = getContext();
        if (context != null) {
            new AlertDialog.Builder(context)
                .setTitle(userName + "'s Contact")
                .setMessage("Email: " + email)
                .setPositiveButton("OK", null)
                .show();
        }
    }
} 