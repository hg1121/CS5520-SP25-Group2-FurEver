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

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q3, container, false);
        rgCoat = view.findViewById(R.id.rg_coat);
        view.findViewById(R.id.btn_prev3).setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToPrevQuestion()
        );
        view.findViewById(R.id.btn_next3).setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToNextQuestion()
        );
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (rgCoat == null) return;
        int sel = rgCoat.getCheckedRadioButtonId();
        String val = null;
        if (sel != -1) {
            RadioButton rb = rgCoat.findViewById(sel);
            val = rb.getTag() != null ? rb.getTag().toString() : null;
        }
        ((MainActivity) requireActivity()).getDogPreference().coatLength = val;
    }

    @Override
    public void onResume() {
        super.onResume();
        String prev = ((MainActivity) requireActivity()).getDogPreference().coatLength;
        if (prev != null && rgCoat != null) {
            for (int i = 0; i < rgCoat.getChildCount(); i++) {
                View c = rgCoat.getChildAt(i);
                if (c instanceof RadioButton && prev.equals(c.getTag())) {
                    ((RadioButton)c).setChecked(true);
                    break;
                }
            }
        }
    }
}