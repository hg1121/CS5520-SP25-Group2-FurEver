package com.example.furever;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts = new ArrayList<>();
    private OnPostClickListener listener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public PostAdapter(OnPostClickListener listener) {
        this.listener = listener;
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
        private TextView breedText;
        private TextView sexText;
        private TextView ageText;
        private TextView descriptionText;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.userName);
            postTime = itemView.findViewById(R.id.postTime);
            breedText = itemView.findViewById(R.id.breedText);
            sexText = itemView.findViewById(R.id.sexText);
            ageText = itemView.findViewById(R.id.ageText);
            descriptionText = itemView.findViewById(R.id.descriptionText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPostClick(posts.get(position));
                }
            });
        }

        void bind(Post post) {
            userName.setText(post.getUserName());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            postTime.setText(sdf.format(post.getTimestamp()));
            breedText.setText(post.getBreed());
            sexText.setText(post.getSex());
            ageText.setText(post.getAge());
            descriptionText.setText(post.getDescription());

            // Load user image
            Glide.with(itemView.getContext())
                    .load(R.drawable.default_profile)
                    .circleCrop()
                    .into(userImage);
        }
    }
} 