package com.example.fittrack;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeGoalFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private GoalsUtil goalsUtil = new GoalsUtil(db);

    private EditText editTargetDistance, editTargetTime, editCompletionDate;
    private Button btnCreateDistanceGoal;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat descriptionFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTargetDistance = view.findViewById(R.id.etDistanceTarget);
        editTargetTime = view.findViewById(R.id.etTimeTarget);
        editCompletionDate = view.findViewById(R.id.etCompletionDate);
        btnCreateDistanceGoal = view.findViewById(R.id.btnCreateTimeGoal);

        editCompletionDate.setOnClickListener(view1 -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            new DatePickerDialog(
                    getContext(),
                    (datePicker, year, month, day) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
                        editCompletionDate.setText(selectedDate);
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
            ).show();
        });

        btnCreateDistanceGoal.setOnClickListener(view1 -> {
            if (validateInputs()) {
                saveGoal();
            }
        });
    }

    private boolean validateInputs() {
        editCompletionDate.setError(null);
        String targetDistance = editTargetDistance.getText().toString().trim();
        String targetTime = editTargetTime.getText().toString().trim();
        String completionDate = editCompletionDate.getText().toString().trim();

        if (targetDistance.isEmpty()) {
            editTargetDistance.setError("Distance is required!");
            return false;
        }
        try {
            double distance = Double.parseDouble(targetDistance);
            if (distance <= 0) {
                editTargetDistance.setError("Enter a valid distance greater than 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            editTargetDistance.setError("Enter a valid numeric distance");
            return false;
        }

        if (targetTime.isEmpty()) {
            editTargetTime.setError("Time value is required!");
            return false;
        }
        if (!isValidTime(targetTime)) {
            editTargetTime.setError("Invalid time format! Use hh:mm:ss");
            return false;
        }

        if (completionDate.isEmpty()) {
            editCompletionDate.setError("Completion date is required!");
            return false;
        }
        try {
            Date endDate = dateFormat.parse(completionDate);
            if (endDate == null || endDate.before(new Date())) {
                editCompletionDate.setError("Enter a valid date!");
                return false;
            }
        } catch (ParseException e) {
            editCompletionDate.setError("Invalid date format!");
            return false;
        }
        return true;
    }

    private boolean isValidTime(String time) {
        if (!time.matches("^\\d{2}:\\d{2}:\\d{2}$")) return false;

        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);

        return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59 && seconds >= 0 && seconds <= 59;
    }

    private int convertTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return (hours * 3600) + (minutes * 60) + seconds;
    }

    private void saveGoal() {
        try {
            String targetDistance = editTargetDistance.getText().toString().trim();
            int targetTimeInSeconds = convertTimeToSeconds(editTargetTime.getText().toString().trim());
            String completionDate = editCompletionDate.getText().toString().trim();

            Date endDate = dateFormat.parse(completionDate);
            Timestamp formattedCompletionDate = new Timestamp(endDate);
            String formattedEndDate = descriptionFormat.format(endDate);

            goalsUtil.setTimeGoal(
                    currentUser,
                    Timestamp.now(),
                    formattedCompletionDate,
                    targetTimeInSeconds,
                    Double.parseDouble(targetDistance) * 1000,
                    "In Progress",
                    0,
                    "Achieve a time of " + ConversionUtil.convertSecondsToTime(targetTimeInSeconds) + " for a distance of " + targetDistance + " KM by " + formattedEndDate
            );

            Toast.makeText(getContext(), "Time Goal has been set successfully", Toast.LENGTH_SHORT).show();

            editTargetDistance.setText("");
            editTargetTime.setText("");
            editCompletionDate.setText("");
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error parsing date", Toast.LENGTH_SHORT).show();
        }
    }
}
