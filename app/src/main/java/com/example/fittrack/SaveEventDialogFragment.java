package com.example.fittrack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveEventDialogFragment extends DialogFragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editEventName, editDescription;
    private Spinner editActivityType;
    private Button btnSaveEvent;
    private EventUtil eventUtil = new EventUtil(db);

    @Override
    public void onStart() {
        super.onStart();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.save_event_dialog, null);
        editActivityType = view.findViewById(R.id.enterActivityType);
        editEventName = view.findViewById(R.id.enterEventName);
        editDescription = view.findViewById(R.id.enterDescription);
        btnSaveEvent = view.findViewById(R.id.btnSaveEvent);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(getContext(), R.array.fitness_activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        editActivityType.setAdapter(adapter);

        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateSaveInputs()) {
                    String onDate = "";
                    if (getArguments() != null && !getArguments().getString("onDate").isEmpty()) {
                        onDate = getArguments().getString("onDate");
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date today = new Date();
                        String date = dateFormat.format(today);
                        onDate = date;
                    }
                    String activityType = editActivityType.getSelectedItem().toString();
                    String eventName = editEventName.getText().toString().trim();
                    String description = editDescription.getText().toString().trim();
                    EventModel event = new EventModel(activityType, onDate, eventName, description, "To Be Completed");
                    eventUtil.addNewEvent(event);
                    dismiss();
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Save Event")
                .create();
    }

    private boolean validateSaveInputs() {
        String eventName = editEventName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if(TextUtils.isEmpty(eventName)) {
            editEventName.setError("Event Name is Empty!");
            editEventName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            editDescription.setError("Description is required");
            editDescription.requestFocus();
            return false;
        }

        if (editActivityType.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(), "Please select an activity type", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}