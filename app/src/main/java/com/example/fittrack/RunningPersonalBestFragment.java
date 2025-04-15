package com.example.fittrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

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

        RecyclerView personalRecordsRecycler = view.findViewById(R.id.PersonalRecordRecycler);
        ArrayList<PersonalRecord> records = new ArrayList<>();
        PersonalRecordRecyclerAdapter adapter = new PersonalRecordRecyclerAdapter(records);
        personalRecordsRecycler.setAdapter(adapter);
        personalRecordsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        PBUtil.findFastestDistanceTime(UserID, 1000, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB, String date) {
                PersonalRecord record = new PersonalRecord("1KM", PB, date, R.drawable.running);
                records.add(record);
                adapter.notifyItemInserted(records.size() - 1);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 5000, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB, String date) {
                PersonalRecord record = new PersonalRecord("5KM", PB, date, R.drawable.running);
                records.add(record);
                adapter.notifyItemInserted(records.size() - 1);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 10000, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB, String date) {
                PersonalRecord record = new PersonalRecord("10KM", PB, date, R.drawable.running);
                records.add(record);
                adapter.notifyItemInserted(records.size() - 1);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 21100, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB, String date) {
                PersonalRecord record = new PersonalRecord("Half Marathon", PB, date, R.drawable.running);
                records.add(record);
                adapter.notifyItemInserted(records.size() - 1);
            }
        });

        PBUtil.findFastestDistanceTime(UserID, 42200, "Running", new PersonalBestUtil.PersonalBestCallback() {
            @Override
            public void onCallback(String PB, String date) {
                PersonalRecord record = new PersonalRecord("Marathon", PB, date, R.drawable.running);
                records.add(record);
                adapter.notifyItemInserted(records.size() - 1);
            }
        });
    }
}
