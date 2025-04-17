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
        View view = inflater.inflate(R.layout.fragment_q4, container, false);
        rgHome = view.findViewById(R.id.rg_home);
        view.findViewById(R.id.btn_prev4).setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToPrevQuestion()
        );
        view.findViewById(R.id.btn_next4).setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToNextQuestion()
        );
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rgHome == null) return;
        int sel = rgHome.getCheckedRadioButtonId();
        String val = null;
        if (sel != -1) {
            RadioButton rb = rgHome.findViewById(sel);
            val = rb.getTag() != null ? rb.getTag().toString() : null;
        }
        ((MainActivity) requireActivity()).getDogPreference().homeType = val;
    }

    @Override
    public void onResume() {
        super.onResume();
        String prev = ((MainActivity) requireActivity()).getDogPreference().homeType;
        if (prev != null && rgHome != null) {
            for (int i = 0; i < rgHome.getChildCount(); i++) {
                View c = rgHome.getChildAt(i);
                if (c instanceof RadioButton && prev.equals(c.getTag())) {
                    ((RadioButton)c).setChecked(true);
                    break;
                }
            }
        }
    }
}
