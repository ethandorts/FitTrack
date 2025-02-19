package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class RunningPersonalBestFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private PersonalBestUtil PBUtil = new PersonalBestUtil();

    public RunningPersonalBestFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_best, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txt1KM, txt5KM, txt10KM, txtHalfMarathon, txtMarathon;
        txt1KM = view.findViewById(R.id.OneKMValue);
        txt5KM = view.findViewById(R.id.FiveKMValue);
        txt10KM = view.findViewById(R.id.TenKMValue);
        txtHalfMarathon = view.findViewById(R.id.HalfMarathonValue);
        txtMarathon = view.findViewById(R.id.MarathonValue);

        PBUtil.findFastestDistanceTime(UserID, 1000, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt1KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 5000, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt5KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 10000, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt10KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 21100, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txtHalfMarathon.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 42200, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txtMarathon.setText(PB);
            }
        });

    }
}
