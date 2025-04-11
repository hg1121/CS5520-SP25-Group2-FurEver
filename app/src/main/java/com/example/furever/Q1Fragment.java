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

        // 加载布局 / Inflate layout
        View view = inflater.inflate(R.layout.fragment_q1, container, false);

        // 绑定控件 / Bind views
        rgSize = view.findViewById(R.id.rg_question);
        Button btnNext = view.findViewById(R.id.btn_next);

        Log.d("Q1Fragment", "btnNext = " + btnNext);

        // 下一题按钮仅切换页面，不负责保存 / Next button only switches page
        btnNext.setOnClickListener(v -> {
            Log.d("Q1Fragment", "btnNext clicked");
            ((MainActivity) requireActivity()).goToNextQuestion();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        // 离开页面时保存当前选项 / Save selected value when leaving the page
        if (getView() == null || rgSize == null) return;

        int selectedId = rgSize.getCheckedRadioButtonId();
        String value = null;

        if (selectedId != -1) {
            RadioButton selected = rgSize.findViewById(selectedId);
            if (selected != null && selected.getTag() != null) {
                value = selected.getTag().toString();
            }
        }

        ((MainActivity) requireActivity()).getDogPreference().size = value;
        Log.d("Q1Fragment", "Saved size = " + value);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 回到页面时自动恢复之前选择 / Restore previous selection
        if (getView() == null || rgSize == null) return;

        String prev = ((MainActivity) requireActivity()).getDogPreference().size;
        if (prev != null) {
            for (int i = 0; i < rgSize.getChildCount(); i++) {
                View child = rgSize.getChildAt(i);
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
