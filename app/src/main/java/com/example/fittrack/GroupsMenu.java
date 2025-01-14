package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class GroupsMenu extends AppCompatActivity {
    ImageButton btnFindRunningGroups, btnCreateRunningGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_menu);

        btnFindRunningGroups = findViewById(R.id.btnSearchGroups);
        btnCreateRunningGroup = findViewById(R.id.btnCreateGroup);

        loadFragment(new MyRunningGroupsFragment());

        btnFindRunningGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupsMenu.this, FindRunningGroupsActivity.class);
                startActivity(intent);
            }
        });
        btnCreateRunningGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupsMenu.this, CreateRunningGroupActivity.class);
                startActivity(intent);
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