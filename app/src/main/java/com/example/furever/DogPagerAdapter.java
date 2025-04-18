package com.example.furever;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class DogPagerAdapter extends RecyclerView.Adapter<DogPagerAdapter.DogViewHolder> {

    private final List<DogRecommendation> dogList;

    public DogPagerAdapter(List<DogRecommendation> dogList) {
        this.dogList = dogList;
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dog_result, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        DogRecommendation dog = dogList.get(position);

        holder.breedName.setText(dog.breed);
        holder.matchPercent.setText(dog.match_percent + " Match");
        holder.why.setText(dog.why);
        holder.tips.setText(dog.care_tips);

        // 加载头像图（来自本地路径）
        String path = dog.imagePath;
        Log.d("DogPagerAdapter", "Image path for " + dog.breed + ": " + path);
        if (path != null && !path.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(new File(path))
                    .placeholder(R.drawable.placeholder_image)
                    .transform(new RoundedCorners(32))
                    .into(holder.image);
        } else {
            // fallback 默认图
            holder.image.setImageResource(R.drawable.placeholder_image); // 或 R.raw.default_dog
        }
    }

    @Override
    public int getItemCount() {
        return dogList.size();
    }

    static class DogViewHolder extends RecyclerView.ViewHolder {
        TextView breedName, matchPercent, why, tips;
        ImageView image;

        DogViewHolder(View itemView) {
            super(itemView);
            matchPercent = itemView.findViewById(R.id.tv_match_percent);
            breedName = itemView.findViewById(R.id.tv_breed_name);
            why = itemView.findViewById(R.id.tv_why);
            tips = itemView.findViewById(R.id.tv_tips);
            image = itemView.findViewById(R.id.img_dog);
        }
    }
}

