package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditFitnessEvent extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EventUtil eventUtil = new EventUtil(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fitness_event);

        Intent intent = getIntent();
        String EventID = intent.getStringExtra("EventID");
        String eventName = intent.getStringExtra("EventName");
        String activityType = intent.getStringExtra("ActivityType");
        String description = intent.getStringExtra("Description");
        String date = intent.getStringExtra("DateTime");


        EditText editEventName = findViewById(R.id.enterEditEventName);
        EditText editDescription = findViewById(R.id.enterEditDescription);
        AutoCompleteTextView editActivityType = findViewById(R.id.enterEditActivityType);
        Button btnEditFitnessEvent = findViewById(R.id.btnEditEvent);
        Button btnDeleteFitnessEvent = findViewById(R.id.btnDeleteEvent);

        String[] fitnessActivities = getResources().getStringArray(R.array.fitness_activities);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                fitnessActivities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editActivityType.setAdapter(adapter);
        editActivityType.setText("Running", false);

        editActivityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editActivityType.showDropDown();
            }
        });

        editEventName.setText(eventName);
        editDescription.setText(description);

        if (activityType != null) {
            int spinnerPosition = adapter.getPosition(activityType);
            if (spinnerPosition >= 0) {
                editActivityType.setSelection(spinnerPosition);
            }
        }

        btnEditFitnessEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedEventName = editEventName.getText().toString();
                String updatedDescription = editDescription.getText().toString();
                String updatedActivityType = editActivityType.getText().toString();

                eventUtil.updateEvent(EventID, updatedEventName, updatedDescription, updatedActivityType, date);
                finish();
            }
        });

        btnDeleteFitnessEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventUtil.deleteEvent(EventID);
                finish();
            }
        });
    }
}