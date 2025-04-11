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

public class Q5Fragment extends Fragment {

    private RadioGroup rgChildren;

    public Q5Fragment() {}

    public static Q5Fragment newInstance() {
        return new Q5Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 加载布局 / Inflate layout
        View view = inflater.inflate(R.layout.fragment_q5, container, false);

        // 绑定控件 / Bind views
        rgChildren = view.findViewById(R.id.rg_question);
        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnNext = view.findViewById(R.id.btn_next);

        Log.d("Q5Fragment", "btnNext = " + btnNext);

        // 上一页
        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());

        // 下一页（不处理保存）
        btnNext.setOnClickListener(v -> {
            Log.d("Q5Fragment", "btnNext clicked");
            ((MainActivity) requireActivity()).goToNextQuestion();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getView() == null || rgChildren == null) return;

        int selectedId = rgChildren.getCheckedRadioButtonId();
        String value = null;

        if (selectedId != -1) {
            RadioButton selected = rgChildren.findViewById(selectedId);
            if (selected != null && selected.getTag() != null) {
                value = selected.getTag().toString();
            }
        }

        ((MainActivity) requireActivity()).getDogPreference().haveChildren = value;
        Log.d("Q5Fragment", "Saved haveChildren = " + value);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null || rgChildren == null) return;

        String prev = ((MainActivity) requireActivity()).getDogPreference().haveChildren;
        if (prev != null) {
            for (int i = 0; i < rgChildren.getChildCount(); i++) {
                View child = rgChildren.getChildAt(i);
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
