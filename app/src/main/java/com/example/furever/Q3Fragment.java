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

public class Q3Fragment extends Fragment {

    private RadioGroup rgCoat;

    public Q3Fragment() {}

    public static Q3Fragment newInstance() {
        return new Q3Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("Q3Fragment", "Inflating Q3 layout");

        // 加载布局 / Inflate layout
        View view = inflater.inflate(R.layout.fragment_q3, container, false);

        // 绑定控件 / Bind views
        rgCoat = view.findViewById(R.id.rg_question);
        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnNext = view.findViewById(R.id.btn_next);
        Log.d("Q3Fragment", "btnNext = " + btnNext);

        // 上一题 / Previous
        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());

        // 下一题（仅切换页面） / Next (only switches page)
        btnNext.setOnClickListener(v -> {
            Log.d("Q3Fragment", "btnNext clicked");
            ((MainActivity) requireActivity()).goToNextQuestion();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getView() == null || rgCoat == null) return;

        int selectedId = rgCoat.getCheckedRadioButtonId();
        String value = null;

        if (selectedId != -1) {
            RadioButton selected = rgCoat.findViewById(selectedId);
            if (selected != null && selected.getTag() != null) {
                value = selected.getTag().toString();
            }
        }

        ((MainActivity) requireActivity()).getDogPreference().coatLength = value;
        Log.d("Q3Fragment", "Saved coatLength = " + value);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null || rgCoat == null) return;

        String prev = ((MainActivity) requireActivity()).getDogPreference().coatLength;
        if (prev != null) {
            for (int i = 0; i < rgCoat.getChildCount(); i++) {
                View child = rgCoat.getChildAt(i);
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
