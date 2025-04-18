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

public class Q1Fragment extends Fragment {

    private RadioGroup rgSize;

    public Q1Fragment() {}

    public static Q1Fragment newInstance() {
        return new Q1Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q1, container, false);

        rgSize = view.findViewById(R.id.rg_size);

        view.findViewById(R.id.btn_next)
                .setOnClickListener(v -> ((MainActivity) requireActivity()).goToNextQuestion());

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rgSize == null) return;

        int selectedId = rgSize.getCheckedRadioButtonId();
        String value = null;
        if (selectedId != -1) {
            RadioButton rb = rgSize.findViewById(selectedId);
            if (rb.getTag() != null) value = rb.getTag().toString();
        }
        ((MainActivity) requireActivity()).getDogPreference().size = value;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rgSize == null) return;

        String prev = ((MainActivity) requireActivity()).getDogPreference().size;
        if (prev != null) {
            for (int i = 0; i < rgSize.getChildCount(); i++) {
                View child = rgSize.getChildAt(i);
                if (child instanceof RadioButton && prev.equals(child.getTag())) {
                    ((RadioButton) child).setChecked(true);
                    break;
                }
            }
        }
    }
}
