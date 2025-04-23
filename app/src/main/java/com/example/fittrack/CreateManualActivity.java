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

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class CreateManualActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_manual);

        loadFragment(new ManualActivityFragment(), R.id.fragmentManualActivity);
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