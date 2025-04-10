package com.example.furever;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuestionPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();

    public QuestionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragmentList.add(Q1Fragment.newInstance());
        fragmentList.add(Q2Fragment.newInstance());
        fragmentList.add(Q3Fragment.newInstance());
        fragmentList.add(Q4Fragment.newInstance());
        fragmentList.add(Q5Fragment.newInstance());
        fragmentList.add(Q6Fragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
