package com.example.fittrack;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

public class CaloriesGoalFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getUid();
    private GoalsUtil goalsUtil = new GoalsUtil(db);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calories_goal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTargetCalories = view.findViewById(R.id.editenterCalorieGoal);
        EditText editCompletionDate = view.findViewById(R.id.editCalorieCompletionDate);
        Button btnCreateCaloriesGoal = view.findViewById(R.id.btnCreateCaloriesGoal);

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

        btnCreateCaloriesGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCompletionDate.setError(null);
                Date endDate;
                String targetCalories = editTargetCalories.getText().toString();
                String completionDate = editCompletionDate.getText().toString();

                if(targetCalories.isEmpty()) {
                    editTargetCalories.setError("Please enter a target calorie goal!");
                    return;
                }
                double calorieGoal;
                try {
                    calorieGoal = Double.parseDouble(targetCalories);
                    if (calorieGoal <= 0) {
                        editTargetCalories.setError("Please enter a valid calorie goal!");
                        return;
                    }
                } catch (NumberFormatException e) {
                    editTargetCalories.setError("Please enter a valid value for the calorie goal!");
                    return;
                }

                if(completionDate.isEmpty()) {
                    editCompletionDate.setError("Please enter a completion date");
                    return;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat descriptionFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());

                try {
                    endDate = dateFormat.parse(completionDate);
                    if (endDate == null) {
                        editCompletionDate.setError("Invalid date format entered! Please use this format: dd/MM/yyyy");
                        return;
                    }

                    Date currentDate = new Date();
                    if (endDate.before(currentDate)) {
                        editCompletionDate.setError("Please enter a valid completion date");
                        return;
                    }

                } catch (ParseException e) {
                    editCompletionDate.setError("Invalid date format entered! Please use this format: dd/MM/yyyy");
                    return;
                }

                Timestamp formattedCompletionDate = new Timestamp(endDate);
                String formattedEndDate = descriptionFormat.format(endDate);
                goalsUtil.setCalorieGoal(currentUser, Timestamp.now(), formattedCompletionDate, Double.parseDouble(targetCalories), "In Progress", 0,
                        "Consume " + targetCalories + " calories by the end of " + formattedEndDate);
                Toast.makeText(view.getContext(), "Calorie goal has been set successfully!", Toast.LENGTH_SHORT).show();

                editTargetCalories.setText("");
                editCompletionDate.setText("");
            }
        });
    }
}
