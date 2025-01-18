package com.example.fittrack;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SaveCalendarEventDialog extends DialogFragment {
    private FirebaseUser mAuth;
    private String UserID;
    private FirebaseFirestore db;
    private LocalDate date;

    private EditText editEventName, editDescription, editDate, editActivityType;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        UserID = mAuth.getUid();
        db = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.save_calendar_event_dialog, null);
        builder.setView(dialogView);

        editEventName = dialogView.findViewById(R.id.editTextEventName);
        editDescription = dialogView.findViewById(R.id.editTextDescription);
        editDate = dialogView.findViewById(R.id.editTextDateTime);
        editActivityType = dialogView.findViewById(R.id.editTextActivityType);

        builder.setPositiveButton("Save Event", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String eventName = editEventName.getText().toString();
                String description = editDescription.getText().toString();
                String date = editDate.getText().toString();
                String activityType = editActivityType.getText().toString();


                Map<String, Object> data = new HashMap<>();
                data.put("EventName", eventName);
                data.put("DateTime", date);
                data.put("Description", description);
                data.put("ActivityType", activityType);


                db.collection("Users")
                        .document(UserID)
                        .collection("Calendar")
                        .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                System.out.println("Event added");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("fAILURE TO ADD EVENT");
                            }
                        });
            }
        });


        return builder.create();
    }
}
