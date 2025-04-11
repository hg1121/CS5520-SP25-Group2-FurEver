package com.example.furever;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Q2Fragment extends Fragment {

    private RadioGroup rgExercise;

    public Q2Fragment() {}

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

        // 绑定控件 / Bind views
        rgExercise = view.findViewById(R.id.rg_exercise);
        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnNext = view.findViewById(R.id.btn_next);

        Log.d("Q2Fragment", "btnNext = " + btnNext);

        // 上一页 / Go to previous question
        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());

        // 下一页（不再保存数据） / Next without saving (data saved onPause)
        btnNext.setOnClickListener(v -> {
            Log.d("Q2Fragment", "btnNext clicked");
            ((MainActivity) requireActivity()).goToNextQuestion();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getView() == null || rgExercise == null) return;

        int selectedId = rgExercise.getCheckedRadioButtonId();
        String value = null;

        if (selectedId != -1) {
            RadioButton selected = rgExercise.findViewById(selectedId);
            if (selected != null && selected.getTag() != null) {
                value = selected.getTag().toString();
            }
        }

        ((MainActivity) requireActivity()).getDogPreference().exercise = value;
        Log.d("Q2Fragment", "Saved exercise = " + value);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null || rgExercise == null) return;

        String prev = ((MainActivity) requireActivity()).getDogPreference().exercise;

        if (prev != null) {
            for (int i = 0; i < rgExercise.getChildCount(); i++) {
                View child = rgExercise.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    if (prev.equals(rb.getTag())) {
                        rb.setChecked(true);
                        break;
                    }
                }
            }
        }
    }
}
