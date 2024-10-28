package com.example.fittrack;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class SplitDialogFragment extends DialogFragment {
    TextView txtKm, txtKmPace, txtTotalTime;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.split_dialog_fragment, container, false);

        txtKm = view.findViewById(R.id.kmSplitDisplay);
        txtKmPace = view.findViewById(R.id.splitTimeDisplay);
        txtTotalTime = view.findViewById(R.id.fullTimeDisplay);

        int Km = getArguments().getInt("Km");
        long splitTime = getArguments().getLong("splitTime");
        String formattedSplitTime = formatRunTime(splitTime);
        System.out.println("Split Time " + formattedSplitTime);
        long fullTime = getArguments().getLong("fullTime");
        String formattedFullTime = formatRunTime(fullTime);
        System.out.println("Full Time " + formattedFullTime);

        txtKm.setText(String.valueOf(Km / 1000) + " KM");
        txtKmPace.setText(formattedSplitTime);
        txtTotalTime.setText(formattedFullTime);

        return view;
    }

    private String formatRunTime(long timePassed) {
        int hours = (int) (timePassed / (1000 * 3600));
        int minutes = (int) ((timePassed % (1000 * 3600)) / (1000 * 60));
        int seconds = (int) ((timePassed % (1000 * 60)) / 1000);
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }
}
