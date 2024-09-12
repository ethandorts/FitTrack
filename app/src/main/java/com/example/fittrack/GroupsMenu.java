package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GroupsMenu extends AppCompatActivity {
    Button btnFindRunningGroups, btnYourRunningGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_menu);

        btnFindRunningGroups = findViewById(R.id.btnFindRunningGroups);
        btnYourRunningGroups = findViewById(R.id.btnYourRunningGroups);

        btnFindRunningGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadFragment here
            }
        });
        btnYourRunningGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadFragment here
            }
        });
    }
}