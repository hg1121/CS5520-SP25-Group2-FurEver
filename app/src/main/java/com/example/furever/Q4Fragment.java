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

public class Q4Fragment extends Fragment {

    private RadioGroup rgHome;

    public Q4Fragment() {}

    public static Q4Fragment newInstance() {
        return new Q4Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d("Q4Fragment", "onCreateView: Q4 fragment is being created");
        View view = inflater.inflate(R.layout.fragment_q4, container, false);

        // 绑定控件 / Bind views
        rgHome = view.findViewById(R.id.rg_question);
        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnNext = view.findViewById(R.id.btn_next);

        Log.d("Q4Fragment", "btnNext = " + btnNext);

        // 上一页
        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());

        // 下一页（不保存数据）
        btnNext.setOnClickListener(v -> {
            Log.d("Q4Fragment", "btnNext clicked");
            ((MainActivity) requireActivity()).goToNextQuestion();
        });

        Log.d("Q4Fragment", "onCreateView end reached");
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getView() == null || rgHome == null) return;

        int selectedId = rgHome.getCheckedRadioButtonId();
        String value = null;

        if (selectedId != -1) {
            RadioButton selected = rgHome.findViewById(selectedId);
            if (selected != null && selected.getTag() != null) {
                value = selected.getTag().toString();
            }
        }

        ((MainActivity) requireActivity()).getDogPreference().homeType = value;
        Log.d("Q4Fragment", "Saved homeType = " + value);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null || rgHome == null) return;

        String prev = ((MainActivity) requireActivity()).getDogPreference().homeType;
        if (prev != null) {
            for (int i = 0; i < rgHome.getChildCount(); i++) {
                View child = rgHome.getChildAt(i);
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
