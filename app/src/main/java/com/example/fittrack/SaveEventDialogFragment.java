package com.example.fittrack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveEventDialogFragment extends DialogFragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText editActivityType, editEventName, editDescription;
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

        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String onDate = "";
                if(getArguments() != null && !getArguments().getString("onDate").isEmpty()) {
                    onDate = getArguments().getString("onDate");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date today = new Date();
                    String date = dateFormat.format(today);
                    onDate = date;
                }
                String activityType = editActivityType.getText().toString();
                String eventName = editEventName.getText().toString();
                String description = editDescription.getText().toString();
                EventModel event = new EventModel(activityType, onDate, eventName, description, "To Be Completed");
                eventUtil.addNewEvent(event);
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Save Event")
                .create();
    }
}