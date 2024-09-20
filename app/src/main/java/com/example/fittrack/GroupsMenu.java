package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class GroupsMenu extends AppCompatActivity {
    Button btnFindRunningGroups, btnYourRunningGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_menu);

        btnFindRunningGroups = findViewById(R.id.btnFindRunningGroups);
        btnYourRunningGroups = findViewById(R.id.btnYourRunningGroups);

        loadFragment(new FindRunningGroupsFragment());

        btnFindRunningGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new FindRunningGroupsFragment());
            }
        });
        btnYourRunningGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new MyRunningGroupsFragment());
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.GroupsFrameLayout, fragment);
        transaction.commit();
    }
}