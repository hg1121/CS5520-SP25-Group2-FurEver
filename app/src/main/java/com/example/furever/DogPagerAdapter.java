package com.example.furever;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        // 可选：根据 dog.breed 显示不同图片
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

