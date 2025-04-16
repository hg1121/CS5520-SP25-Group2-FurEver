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

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q5, container, false);
        rgChildren = view.findViewById(R.id.rg_children);
        view.findViewById(R.id.btn_prev5).setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToPrevQuestion()
        );
        view.findViewById(R.id.btn_next5).setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToNextQuestion()
        );
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rgChildren == null) return;
        int sel = rgChildren.getCheckedRadioButtonId();
        String val = null;
        if (sel != -1) {
            RadioButton rb = rgChildren.findViewById(sel);
            val = rb.getTag() != null ? rb.getTag().toString() : null;
        }
        ((MainActivity) requireActivity()).getDogPreference().haveChildren = val;
    }

    @Override
    public void onResume() {
        super.onResume();
        String prev = ((MainActivity) requireActivity()).getDogPreference().haveChildren;
        if (prev != null && rgChildren != null) {
            for (int i = 0; i < rgChildren.getChildCount(); i++) {
                View c = rgChildren.getChildAt(i);
                if (c instanceof RadioButton && prev.equals(c.getTag())) {
                    ((RadioButton)c).setChecked(true);
                    break;
                }
            }
        }
    }
}
