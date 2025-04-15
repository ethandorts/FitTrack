package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;

public class PersonalRecordsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private PersonalBestUtil PBUtil = new PersonalBestUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_records);

        MaterialAutoCompleteTextView activitySpinner = findViewById(R.id.spinnerActivityPB);

        String[] activities = {"Running", "Walking", "Cycling"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                activities
        );
        activitySpinner.setAdapter(adapter);
        activitySpinner.setText("Running", false);

        activitySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String option = parent.getItemAtPosition(position).toString();
                updateFragment(option);
            }
        });

    }

    private void updateFragment(String activityType) {
        Fragment fragment;
        switch(activityType) {
            case "Running":
                fragment = new RunningPersonalBestFragment();
                break;
            case "Walking":
                fragment = new WalkingPersonalBestFragment();
                break;
            case "Cycling" :
                fragment = new CyclingPersonalBestFragment();
                break;
            default:
                fragment = new RunningPersonalBestFragment();
                break;
        }
        loadFragment(fragment, R.id.fragmentContainerPB);
    }

    private void loadFragment(Fragment fragment, int frameLayoutId) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment existingFragment = fm.findFragmentById(frameLayoutId);
        if (existingFragment != null) {
            transaction.remove(existingFragment);
        }
        transaction.replace(frameLayoutId, fragment);
        transaction.commit();
    }
}