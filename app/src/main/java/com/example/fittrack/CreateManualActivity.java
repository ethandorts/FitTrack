package com.example.fittrack;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CreateManualActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_manual);

        Spinner spinner = findViewById(R.id.spinnerManualActivityTypes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.fitness_activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String selected = spinner.getSelectedItem().toString();

        if(selected.equals("Running") || selected.equals("Walking") || selected.equals("Cycling")) {
            loadFragment(new ManualActivityFragment(selected), R.id.fragmentManualActivity);
        } else {
            // do options for another activity.
        }
    }

    public void loadFragment(Fragment fragment, int frameLayoutId) {
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