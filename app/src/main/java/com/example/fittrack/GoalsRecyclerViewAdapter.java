package com.example.fittrack;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GoalsRecyclerViewAdapter extends FirestoreRecyclerAdapter<GoalModel, GoalsRecyclerViewAdapter.GoalsViewHolder> {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();

    public GoalsRecyclerViewAdapter(Context context, @NonNull FirestoreRecyclerOptions<GoalModel> options) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull GoalsViewHolder holder, int i, @NonNull GoalModel model) {
        String GoalID = getSnapshots().getSnapshot(i).getId();
        String activityType = model.getActivityType();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GoalProgressReportActivity.class);
                intent.putExtra("GoalID", GoalID);
                intent.putExtra("activityType", activityType);
                context.startActivity(intent);
            }
        });
        holder.txtGoalType.setText(model.getGoalType() + " Goal");
        holder.txtGoalDescription.setText(model.getGoalDescription());
        holder.txtDeadline.setText("Completion Target Date: " + ConversionUtil.dateFormatter(model.getEndDate()));
        holder.txtActivityType.setText(model.getActivityType());
        holder.txtProgress.setText(model.getStatus());
        if(model.getStatus().equals("Completed")) {
            holder.txtProgress.setTextColor(Color.RED);
        }
        if (model.getEndDate() != null && "In Progress".equals(model.getStatus())) {
            Date deadlineDate = model.getEndDate().toDate();
            Date today = new Date();

            if (today.after(deadlineDate)) {
                holder.btnExtendGoal.setVisibility(View.VISIBLE);
            } else {
                holder.btnExtendGoal.setVisibility(View.GONE);
            }
        } else {
            holder.btnExtendGoal.setVisibility(View.GONE);
        }
        holder.btnExtendGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, dayOfMonth, 0, 0, 0);
                                Date newEndDate = selectedDate.getTime();

                                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
                                String formattedDate = sdf.format(newEndDate);

                                String goalType = model.getGoalType();
                                String newDescription;

                                if ("Distance".equalsIgnoreCase(goalType)) {
                                    newDescription = "Achieve a distance of " +
                                            String.format("%.2f", model.getTargetDistance() / 1000.0) +
                                            " KM by " + formattedDate;
                                } else if ("Time".equalsIgnoreCase(goalType)) {
                                    long totalSeconds = (long) model.getTargetTime();
                                    long hours = totalSeconds / 3600;
                                    long minutes = (totalSeconds % 3600) / 60;
                                    long seconds = totalSeconds % 60;

                                    String timeFormatted;
                                    if (hours > 0) {
                                        timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                                    } else {
                                        timeFormatted = String.format("%02d:%02d", minutes, seconds);
                                    }
                                    double distanceKm = model.getTargetDistance() / 1000.0;
                                    newDescription = "Achieve a time of " + timeFormatted +
                                            " for a distance of " + String.format("%.1f", distanceKm) +
                                            " KM by " + formattedDate;
                                } else {
                                    newDescription = "Achieve your goal by " + formattedDate;
                                }

                                FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(UserID)
                                        .collection("Goals")
                                        .document(GoalID)
                                        .update("endDate", new Timestamp(newEndDate),
                                                "goalDescription", newDescription
                                        )
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Goal deadline extended!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("Failed to update goal", e.getMessage());
                                            }
                                        });
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

    }

    @NonNull
    @Override
    public GoalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.goal_row, parent, false);
        return new GoalsRecyclerViewAdapter.GoalsViewHolder(view);
    }

    public class GoalsViewHolder extends RecyclerView.ViewHolder {
        private TextView txtGoalType, txtGoalDescription, txtProgress, txtDeadline, txtActivityType;
        private Button btnExtendGoal;

        public GoalsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtGoalType = itemView.findViewById(R.id.goal_title);
            txtActivityType = itemView.findViewById(R.id.goal_activity_type);
            txtGoalDescription = itemView.findViewById(R.id.goal_description);
            txtProgress = itemView.findViewById(R.id.goal_status);
            txtDeadline = itemView.findViewById(R.id.goal_deadline);
            btnExtendGoal = itemView.findViewById(R.id.btn_extend_goal);
        }
    }
}