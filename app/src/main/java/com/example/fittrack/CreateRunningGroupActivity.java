package com.example.fittrack;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateRunningGroupActivity extends AppCompatActivity {
    private EditText editGroupName, editLocation, editMotto, editDescription;
    private Button btnCreateFitnessGroup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsDatabaseUtil = new GroupsDatabaseUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_running_group);

        Spinner activitySpinner = findViewById(R.id.editGroupFitnessActivity);
        editGroupName = findViewById(R.id.editGroupName);
        editLocation = findViewById(R.id.editGroupLocation);
        editMotto = findViewById(R.id.editGroupMotto);
        editDescription = findViewById(R.id.editGroupDetailedDescription);
        btnCreateFitnessGroup = findViewById(R.id.btnCreateFitnessGroup);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.fitness_activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        activitySpinner.setAdapter(adapter);

        btnCreateFitnessGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String GroupName = editGroupName.getText().toString().trim();
                String FitnessActivity = activitySpinner.getSelectedItem().toString().trim();
                String Location = editLocation.getText().toString().trim();
                String Motto = editMotto.getText().toString().trim();
                String Description = editDescription.getText().toString().trim();

                if(TextUtils.isEmpty(GroupName)) {
                    editGroupName.setError("Group Name is required!");
                    editGroupName.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Location)) {
                    editLocation.setError("Location is required!");
                    editLocation.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Motto)) {
                    editMotto.setError("Motto is required!");
                    editMotto.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Description)) {
                    editDescription.setError("Description is required!");
                    editDescription.requestFocus();
                    return;
                }
                groupsDatabaseUtil.createNewGroup(currentUser, GroupName, FitnessActivity, Location, Motto, Description);
                finish();
            }
        });
    }
}