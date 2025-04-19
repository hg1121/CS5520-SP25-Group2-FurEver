package com.example.furever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class PostsFragment extends Fragment implements PostAdapter.OnPostClickListener {
    private static final String TAG = "PostsFragment";

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddPost;

    private AutoCompleteTextView regionInput;
    private ArrayAdapter<String> regionAdapter;
    private List<Post> allPosts = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Reference UI elements
        recyclerView  = view.findViewById(R.id.recyclerView);
        progressBar   = view.findViewById(R.id.progressBar);
        swipeRefresh  = view.findViewById(R.id.swipeRefresh);
        fabAddPost    = view.findViewById(R.id.fabAddPost);
        regionInput   = view.findViewById(R.id.regionInput);

        // Set up RecyclerView and its adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PostAdapter(this);
        recyclerView.setAdapter(adapter);

        // Set up dropdown suggestions adapter (initially empty)
        regionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );
        regionInput.setAdapter(regionAdapter);

        // Listen for text changes to filter posts
        regionInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterByRegion(s.toString());
            }
        });

        // Pull-to-refresh
        swipeRefresh.setOnRefreshListener(this::loadPosts);

        // Floating action button to create a new post
        fabAddPost.setOnClickListener(v ->
                startActivity(new Intent(getContext(), CreatePostActivity.class))
        );

        // Load posts initially
        loadPosts();
    }


    private void loadPosts() {
        Context context = getContext();
        if (context == null) return;

        progressBar.setVisibility(View.VISIBLE);

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!isAdded()) return;

                    allPosts.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Post post = doc.toObject(Post.class);
                        post.setId(doc.getId());
                        allPosts.add(post);
                    }

//                    // Append sample posts
//                    allPosts.addAll(getSamplePosts());

                    // Display all posts
                    adapter.setPosts(new ArrayList<>(allPosts));
                    swipeRefresh.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);

                    // Update region dropdown suggestions
                    updateRegionSuggestions();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading posts", e);
                    Toast.makeText(context,
                            "Error loading posts: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void updateRegionSuggestions() {
        Set<String> regionsSet = new TreeSet<>();
        for (Post p : allPosts) {
            String fullAddress = p.getAddress();
            if (fullAddress != null && !fullAddress.isEmpty()) {
                regionsSet.add(extractRegion(fullAddress));
            }
        }

        List<String> regions = new ArrayList<>(regionsSet);
        regions.add(0, "All Regions");

        regionAdapter.clear();
        regionAdapter.addAll(regions);
        regionAdapter.notifyDataSetChanged();
    }

    private String extractRegion(String fullAddress) {
        String[] parts = fullAddress.split(",");
        if (parts.length < 2) {
            return fullAddress.trim();
        }
        String city = parts[1].trim();
        String statePart = parts.length >= 3 ? parts[2].trim() : "";
        String state = statePart.split("\\s+")[0];
        return city + ", " + state;
    }

    private void filterByRegion(String region) {
        if (region == null
                || region.isEmpty()
                || "All Regions".equalsIgnoreCase(region)) {
            adapter.setPosts(new ArrayList<>(allPosts));
        } else {
            List<Post> filtered = new ArrayList<>();
            for (Post p : allPosts) {
                String regionOfPost = extractRegion(p.getAddress() != null ? p.getAddress() : "");
                if (regionOfPost.toLowerCase().contains(region.toLowerCase())) {
                    filtered.add(p);
                }
            }
            adapter.setPosts(filtered);
        }
    }


//    private List<Post> getSamplePosts() {
//        List<Post> samples = Arrays.asList(
//                new Post("sample_user_1", "Sarah Johnson", "Golden Retriever",
//                        "Female", "Young (1-3 years)",
//                        "Looking for a loving home for Luna...",
//                        "123 Main St, Boston, MA 02115", 42.3601, -71.0589),
//                new Post("sample_user_2", "Mike Wilson", "German Shepherd",
//                        "Male", "Puppy (0-1 year)",
//                        "Max is a 6‑month‑old German Shepherd...",
//                        "456 Park Ave, New York, NY 10022", 40.7128, -74.0060),
//                new Post("sample_user_3", "Emily Chen", "French Bulldog",
//                        "Male", "Adult (3-7 years)",
//                        "Meet Charlie, a calm and affectionate Frenchie...",
//                        "789 Ocean Blvd, Miami, FL 33139", 25.7617, -80.1918),
//                new Post("sample_user_4", "David Brown", "Labrador Retriever",
//                        "Female", "Senior (7+ years)",
//                        "Sweet senior Lab named Bella...",
//                        "321 Highland Dr, Seattle, WA 98101", 47.6062, -122.3321),
//                new Post("sample_user_5", "Lisa Martinez", "Border Collie",
//                        "Male", "Young (1-3 years)",
//                        "Cooper is a highly intelligent Border Collie...",
//                        "555 River Rd, Chicago, IL 60601", 41.8781, -87.6298)
//        );
//
//        for (int i = 0; i < samples.size(); i++) {
//            Post p = samples.get(i);
//            p.setId("sample_post_" + (i + 1));
//            p.setTimestamp(new Date(System.currentTimeMillis() - i * 86400000L));
//        }
//        return samples;
//    }

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
            String fakeEmail = userName.toLowerCase().replace(" ", ".") + "@example.com";
            showUserEmailDialog(userName, fakeEmail);
            return;
        }

        Toast.makeText(getContext(),
                "Loading contact information...", Toast.LENGTH_SHORT).show();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    String email = (user != null && user.getEmail() != null)
                            ? user.getEmail()
                            : userId + "@example.com";
                    showUserEmailDialog(userName, email);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    showUserEmailDialog(userName, userId + "@example.com");
                });
    }

    private void showUserEmailDialog(String userName, String email) {
        Context context = getContext();
        if (context != null) {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle(userName + "'s Contact")
                    .setMessage("Email: " + email)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}