package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Q6Fragment extends Fragment {

    public Q6Fragment() { }

    public static Q6Fragment newInstance() {
        return new Q6Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q6, container, false);

        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());

        btnSubmit.setOnClickListener(v -> {
            // Launch the Preview screen, for example PreviewActivity.
            // You can also pass data if needed.
            Intent intent = new Intent(getActivity(), PreviewActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
