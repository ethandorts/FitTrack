package com.example.fittrack;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

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

public class DistanceGoalFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private GoalsUtil goalsUtil = new GoalsUtil(db);

    public DistanceGoalFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_distance_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTargetDistance = view.findViewById(R.id.editTargetDistance);
        EditText editCompletionDate = view.findViewById(R.id.edit);
        EditText editGoalDescription = view.findViewById(R.id.editDistanceGoalDescription);
        Button btnCreateDistanceGoal = view.findViewById(R.id.btnCreateDistanceGoal);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        editCompletionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
                                editCompletionDate.setText(selectedDate);
                            }
                        },
                        calendar.get(java.util.Calendar.YEAR),
                        calendar.get(java.util.Calendar.MONTH),
                        calendar.get(java.util.Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        btnCreateDistanceGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date endDate;
                String targetDistance = editTargetDistance.getText().toString();
                String completionDate = editCompletionDate.getText().toString();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat descriptionFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
                try {
                    endDate = dateFormat.parse(completionDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                Timestamp formattedCompletionDate = new Timestamp(endDate);
                String formattedEndDate = descriptionFormat.format(endDate);
                goalsUtil.setDistanceGoal(currentUser, Timestamp.now(), formattedCompletionDate, Double.parseDouble(targetDistance), "In Progress", 0,
                        "Achieved a distance of " + targetDistance + " by " + formattedEndDate);
            }
        });
    }
}
