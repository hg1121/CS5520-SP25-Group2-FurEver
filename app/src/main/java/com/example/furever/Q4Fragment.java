package com.example.furever;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Q4Fragment extends Fragment {

    public Q4Fragment() { }

    public static Q4Fragment newInstance() {
        return new Q4Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q4, container, false);

        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnNext = view.findViewById(R.id.btn_next);

        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());
        btnNext.setOnClickListener(v -> ((MainActivity) requireActivity()).goToNextQuestion());

        return view;
    }
}
