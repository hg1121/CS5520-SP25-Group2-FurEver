package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;

public class PostsFragment extends Fragment implements PostAdapter.OnPostClickListener {

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
        progressBar.setVisibility(View.VISIBLE);

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Post post = document.toObject(Post.class);
                        post.setId(document.getId());
                        posts.add(post);
                    }
                    adapter.setPosts(posts);
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading posts: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                });
    }

    @Override
    public void onPostClick(Post post) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getId());
        startActivity(intent);
    }
} 