// Q6Fragment.java
package com.example.furever;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q6, container, false);

        rgBudget = view.findViewById(R.id.rg_budget);

        view.findViewById(R.id.btn_prev).setOnClickListener(v ->
                ((MainActivity) requireActivity()).goToPrevQuestion()
        );

        view.findViewById(R.id.btn_submit).setOnClickListener(v -> {
            int sel = rgBudget.getCheckedRadioButtonId();
            String budgetVal = null;
            if (sel != -1) {
                RadioButton rb = rgBudget.findViewById(sel);
                budgetVal = rb.getTag() != null
                        ? rb.getTag().toString()
                        : null;
            }
            ((MainActivity) requireActivity())
                    .getDogPreference().budget = budgetVal;
            Log.d("Q6Fragment", "Saved budget = " + budgetVal);

            Intent intent = new Intent(getActivity(), PreviewActivity.class);
            intent.putExtra("dog_pref",
                    ((MainActivity) requireActivity()).getDogPreference());
            startActivity(intent);
        });

        return view;
    }

    @Override public void onResume() {
        super.onResume();
        String prev = ((MainActivity) requireActivity()).getDogPreference().budget;
        if (prev != null && rgBudget != null) {
            for (int i = 0; i < rgBudget.getChildCount(); i++) {
                View c = rgBudget.getChildAt(i);
                if (c instanceof RadioButton && prev.equals(c.getTag())) {
                    ((RadioButton)c).setChecked(true);
                    break;
                }
            }
        }
    }
}
