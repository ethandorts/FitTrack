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

public class WalkingPersonalBestFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private PersonalBestUtil PBUtil = new PersonalBestUtil();

    public WalkingPersonalBestFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walking_pb, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txt1KM, txt5KM, txt10KM, txt20KM;
        txt1KM = view.findViewById(R.id.OneKMWalkValue);
        txt5KM = view.findViewById(R.id.FiveKMWalkValue);
        txt10KM = view.findViewById(R.id.TenKMWalkValue);
        txt20KM = view.findViewById(R.id.TwentyKMWalkValue);


        PBUtil.findFastestDistanceTime(UserID, 1000, "Walking", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt1KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 5000, "Walking", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt5KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 10000, "Walking", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt10KM.setText(PB);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 20000, "Walking", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB) {
                txt20KM.setText(PB);
            }
        });
    }
}
