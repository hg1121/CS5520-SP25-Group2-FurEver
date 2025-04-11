package com.example.furever;

import android.content.Intent;
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

public class Q6Fragment extends Fragment {

    private RadioGroup rgBudget;

    public Q6Fragment() {}

    public static Q6Fragment newInstance() {
        return new Q6Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 加载布局 / Inflate layout
        View view = inflater.inflate(R.layout.fragment_q6, container, false);

        // 绑定控件 / Bind views
        rgBudget = view.findViewById(R.id.rg_question);
        Button btnPrev = view.findViewById(R.id.btn_prev);
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        Log.d("Q6Fragment", "btnSubmit = " + btnSubmit);

        // 上一题 / Go to previous question
        btnPrev.setOnClickListener(v -> ((MainActivity) requireActivity()).goToPrevQuestion());

        // 提交 / Submit and jump to PreviewActivity
        btnSubmit.setOnClickListener(v -> {
            Log.d("Q6Fragment", "btnSubmit clicked");

            // 保存动作已放在 onPause，这里只跳转即可 / Save is done in onPause
            Intent intent = new Intent(getActivity(), PreviewActivity.class);
            intent.putExtra("dog_pref", ((MainActivity) requireActivity()).getDogPreference());
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (getView() == null || rgBudget == null) return;

        int selectedId = rgBudget.getCheckedRadioButtonId();
        String value = null;

        if (selectedId != -1) {
            RadioButton selected = rgBudget.findViewById(selectedId);
            if (selected != null && selected.getTag() != null) {
                value = selected.getTag().toString();
            }
        }

        ((MainActivity) requireActivity()).getDogPreference().budget = value;
        Log.d("Q6Fragment", "Saved budget = " + value);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null || rgBudget == null) return;

        String prev = ((MainActivity) requireActivity()).getDogPreference().budget;
        if (prev != null) {
            for (int i = 0; i < rgBudget.getChildCount(); i++) {
                View child = rgBudget.getChildAt(i);
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
