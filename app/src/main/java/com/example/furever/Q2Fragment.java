package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class Q2Fragment extends Fragment {
    private RadioGroup rgExercise;

    public static Q2Fragment newInstance() {
        return new Q2Fragment();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q2, container, false);

        rgExercise = view.findViewById(R.id.rg_exercise);
        MaterialButton btnPrev = view.findViewById(R.id.btn_prev);
        MaterialButton btnNext = view.findViewById(R.id.btn_next);

        btnPrev.setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToPrevQuestion()
        );
        btnNext.setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToNextQuestion()
        );

        return view;
    }

    @Override public void onPause() {
        super.onPause();
        if (rgExercise == null) return;
        int sel = rgExercise.getCheckedRadioButtonId();
        String val = null;
        if (sel != -1) {
            RadioButton rb = rgExercise.findViewById(sel);
            val = rb.getTag() != null ? rb.getTag().toString() : null;
        }
        ((MainActivity) requireActivity()).getDogPreference().exercise = val;
    }

    @Override public void onResume() {
        super.onResume();
        String prev = ((MainActivity) requireActivity()).getDogPreference().exercise;
        if (prev != null && rgExercise != null) {
            for (int i = 0; i < rgExercise.getChildCount(); i++) {
                View c = rgExercise.getChildAt(i);
                if (c instanceof RadioButton && prev.equals(c.getTag())) {
                    ((RadioButton)c).setChecked(true);
                    break;
                }
            }
        }
    }
}
