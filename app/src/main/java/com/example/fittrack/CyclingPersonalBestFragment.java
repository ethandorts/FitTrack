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

public class CyclingPersonalBestFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private PersonalBestUtil PBUtil = new PersonalBestUtil();

    public CyclingPersonalBestFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cycling_pb, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txt1KM, txt5KM, txt10KM, txt20KM, txt50KM, txt100KM, txt200KM;
        txt1KM = view.findViewById(R.id.OneKMCycleValue);
        txt5KM = view.findViewById(R.id.FiveKMCyclealue);
        txt10KM = view.findViewById(R.id.TenKMCycleValue);
        txt20KM = view.findViewById(R.id.TwentyKMCycleValue);
        txt50KM = view.findViewById(R.id.FiftyKMCycleValue);
        txt100KM = view.findViewById(R.id.HundredKMCycleValue);
        txt200KM = view.findViewById(R.id.TwoHundredKMCycleValue);

        PBUtil.findFastestDistanceTime(UserID, 1000, "Cycling", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt1KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 5000, "Cycling", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt5KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 10000, "Cycling", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt10KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 20000, "Cycling", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt20KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 50000, "Cycling", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt50KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 100000, "Cycling", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt100KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 200000, "Cycling", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt200KM.setText(PB);
            }
        });

    }
}
