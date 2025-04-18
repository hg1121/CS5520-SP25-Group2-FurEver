package com.example.furever;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private OnPostClickListener listener;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onUserImageClick(String userId, String userName);
    }

    public PostAdapter(OnPostClickListener listener) {
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private ImageView userImage;
        private TextView userName;
        private TextView postTime;
        private Chip breedChip;
        private Chip sexChip;
        private Chip ageChip;
        private TextView descriptionText;
        private TextView addressText;
        private MaterialButton deleteButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            breedChip = itemView.findViewById(R.id.breedChip);
            sexChip = itemView.findViewById(R.id.sexChip);
            ageChip = itemView.findViewById(R.id.ageChip);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            addressText = itemView.findViewById(R.id.addressText);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPostClick(posts.get(position));
                }
            });

            userImage.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Post post = posts.get(position);
                    listener.onUserImageClick(post.getUserId(), post.getUserName());
                }
            });
            
            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Post post = posts.get(position);
                    showDeleteConfirmationDialog(post, position, itemView.getContext());
                }
            });
        }

        void bind(Post post) {
            userName.setText(post.getUserName());
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            postTime.setText(sdf.format(post.getTimestamp()));
            
            breedChip.setText(post.getBreed());
            sexChip.setText(post.getSex());
            ageChip.setText(post.getAge());
            descriptionText.setText(post.getDescription());
            
            // Show delete button only if this post belongs to the current user
            if (currentUser != null && currentUser.getUid().equals(post.getUserId())) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.GONE);
            }
            
            // Set address if available
            String address = post.getAddress();
            if (address != null && !address.isEmpty()) {
                // Extract city and state from the full address
                String cityState = extractCityState(address);
                addressText.setText(cityState);
                addressText.setVisibility(View.VISIBLE);
                ((View) addressText.getParent()).setVisibility(View.VISIBLE); // Show the parent layout
            } else {
                addressText.setVisibility(View.GONE);
                ((View) addressText.getParent()).setVisibility(View.GONE); // Hide the parent layout
            }

            // Set appropriate icon for sex chip
            if ("Male".equalsIgnoreCase(post.getSex())) {
                sexChip.setChipIconResource(R.drawable.ic_male);
            } else {
                sexChip.setChipIconResource(R.drawable.ic_female);
            }
            
            // Load user image
            Glide.with(itemView.getContext())
                    .load(R.drawable.default_profile)
                    .circleCrop()
                    .into(userImage);
        }
    }

    /**
     * Extracts city and state from a full address
     * Example: "123 Main St, Boston, MA 02115" -> "Boston, MA"
     */
    private String extractCityState(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }
        
        try {
            // Most addresses follow a pattern with commas
            String[] parts = fullAddress.split(",");
            if (parts.length >= 3) {
                // Format: "Street, City, State ZIP"
                String city = parts[parts.length - 2].trim();
                String stateZip = parts[parts.length - 1].trim();
                String state = stateZip.split(" ")[0].trim();
                return city + ", " + state;
            } else if (parts.length == 2) {
                // Format: "City, State ZIP"
                String city = parts[0].trim();
                String stateZip = parts[1].trim();
                String state = stateZip.split(" ")[0].trim();
                return city + ", " + state;
            } else {
                // If we can't parse it correctly, just return the first 30 chars
                return fullAddress.length() > 30 ? fullAddress.substring(0, 30) + "..." : fullAddress;
            }
        } catch (Exception e) {
            // In case of any parsing errors, return the original address
            return fullAddress.length() > 30 ? fullAddress.substring(0, 30) + "..." : fullAddress;
        }
    }
    
    /**
     * Shows a confirmation dialog before deleting a post
     */
    private void showDeleteConfirmationDialog(Post post, int position, Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> deletePost(post, position, context))
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    /**
     * Deletes a post from Firestore
     */
    private void deletePost(Post post, int position, Context context) {
        // Skip deletion for sample posts
        if (post.getId().startsWith("sample_post_")) {
            Toast.makeText(context, 
                    "Sample posts cannot be deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Delete from Firestore
        db.collection("posts").document(post.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove from local list and update UI
                    posts.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, 
                            "Post deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, 
                            "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
} 