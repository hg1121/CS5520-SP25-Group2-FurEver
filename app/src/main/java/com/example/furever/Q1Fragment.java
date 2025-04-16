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

public class Q1Fragment extends Fragment {

    private RadioGroup rgSize;

    public Q1Fragment() {}

    public static Q1Fragment newInstance() {
        return new Q1Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q1, container, false);
        rgSize = view.findViewById(R.id.rg_size);
        Button btnNext = view.findViewById(R.id.btn_next1);
        btnNext.setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToNextQuestion()
        );
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rgSize == null) return;
        int sel = rgSize.getCheckedRadioButtonId();
        String val = null;
        if (sel != -1) {
            RadioButton rb = rgSize.findViewById(sel);
            val = rb.getTag() != null ? rb.getTag().toString() : null;
        }
        ((MainActivity) requireActivity()).getDogPreference().size = val;
    }

    @Override
    public void onResume() {
        super.onResume();
        String prev = ((MainActivity) requireActivity()).getDogPreference().size;
        if (prev != null && rgSize != null) {
            for (int i = 0; i < rgSize.getChildCount(); i++) {
                View c = rgSize.getChildAt(i);
                if (c instanceof RadioButton && prev.equals(c.getTag())) {
                    ((RadioButton)c).setChecked(true);
                    break;
                }
            }
        }
    }
}
