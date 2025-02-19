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

import com.google.firebase.auth.FirebaseAuth;

public class PersonalRecordsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private PersonalBestUtil PBUtil = new PersonalBestUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_records);

        Spinner activitySpinner = findViewById(R.id.spinnerActivityPB);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.activity_types_PB, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);

        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String option = adapterView.getItemAtPosition(i).toString();
                updateFragment(option);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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