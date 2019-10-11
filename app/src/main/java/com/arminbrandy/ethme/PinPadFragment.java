package com.arminbrandy.ethme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class PinPadFragment extends Fragment {

    public PinPadFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pin_pad, container, false);

        ConstraintLayout outputLayout = rootView.findViewById(R.id.pin_pad_layout);

        if (getArguments() != null &&
                getArguments().getString(Intent.EXTRA_TEXT) != null) {
            TextView pin_info = outputLayout.findViewById(R.id.tv_pin_info);
            //pin_info.setText(getArguments().getString(Intent.EXTRA_TEXT));
        }
        return rootView;
    }
}
