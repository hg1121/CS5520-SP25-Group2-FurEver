package com.example.furever;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;


public class Q2Fragment extends Fragment {

    public Q2Fragment() { }

    public static Q2Fragment newInstance() {
        return new Q2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Log.d("Q2Fragment", "Inflating Q2 layout");
        View view = inflater.inflate(R.layout.fragment_q2, container, false);

        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnNext = view.findViewById(R.id.btn_next);

        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());
        btnNext.setOnClickListener(v -> ((MainActivity) requireActivity()).goToNextQuestion());

        return view;
    }
}
