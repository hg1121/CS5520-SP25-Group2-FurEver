package com.example.furever;

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

public class Q5Fragment extends Fragment {
    private RadioGroup rgQuestion;

    public static Q5Fragment newInstance() {
        return new Q5Fragment();
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q5, container, false);

        rgQuestion = view.findViewById(R.id.rg_question);
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
        if (rgQuestion == null) return;
        int sel = rgQuestion.getCheckedRadioButtonId();
        String val = null;
        if (sel != -1) {
            RadioButton rb = rgQuestion.findViewById(sel);
            val = rb.getTag() != null ? rb.getTag().toString() : null;
        }
        ((MainActivity) requireActivity()).getDogPreference().haveChildren = val;
    }

    @Override public void onResume() {
        super.onResume();
        String prev = ((MainActivity) requireActivity()).getDogPreference().haveChildren;
        if (prev != null && rgQuestion != null) {
            for (int i = 0; i < rgQuestion.getChildCount(); i++) {
                View c = rgQuestion.getChildAt(i);
                if (c instanceof RadioButton && prev.equals(c.getTag())) {
                    ((RadioButton)c).setChecked(true);
                    break;
                }
            }
        }
    }
}
