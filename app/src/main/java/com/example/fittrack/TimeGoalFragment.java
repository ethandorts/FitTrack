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

public class TimeGoalFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private GoalsUtil goalsUtil = new GoalsUtil(db);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTargetDistance = view.findViewById(R.id.etDistanceTarget);
        EditText editTargetTime = view.findViewById(R.id.etTimeTarget);
        EditText editCompletionDate = view.findViewById(R.id.etCompletionDate);
        Button btnCreateDistanceGoal = view.findViewById(R.id.btnCreateTimeGoal);

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
                String targetTime = editTargetTime.getText().toString();
                String completionDate = editCompletionDate.getText().toString();

                System.out.println(targetDistance);
                System.out.println(targetTime);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat descriptionFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
                try {
                    endDate = dateFormat.parse(completionDate);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                Timestamp formattedCompletionDate = new Timestamp(endDate);
                String formattedEndDate = descriptionFormat.format(endDate);
                goalsUtil.setTimeGoal(currentUser, Timestamp.now(), formattedCompletionDate, Double.parseDouble(targetTime), Double.parseDouble(targetDistance),"In Progress",0,
                        "Achieve a time of " + targetTime + " for a distance of " + targetDistance + "KM by " + formattedEndDate);
            }
        });
    }
}