package com.example.furever;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Q5Fragment extends Fragment {

    public Q5Fragment() { }

    public static Q5Fragment newInstance() {
        return new Q5Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q5, container, false);

        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnNext = view.findViewById(R.id.btn_next);

        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());
        btnNext.setOnClickListener(v -> ((MainActivity) requireActivity()).goToNextQuestion());

        return view;
    }
}
