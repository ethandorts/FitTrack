package com.example.fittrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ManualActivityFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private static final DecimalFormat TwoDecimalRounder = new DecimalFormat("0.00");
    private String activityType;

    public ManualActivityFragment(String activityType) {
        this.activityType = activityType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editDate = view.findViewById(R.id.editManualDate);
        EditText editDistance = view.findViewById(R.id.editManualDistance);
        EditText editTime = view.findViewById(R.id.editManualTime);
        Button btnSaveActivity = view.findViewById(R.id.btnSaveActivity);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(
                        getContext(),
                        (datePicker, year, month, day) -> {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, day);

                            new TimePickerDialog(
                                    getContext(),
                                    (timePicker, hourOfDay, minute) -> {
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);

                                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                        editDate.setText(dateTimeFormat.format(calendar.getTime()));
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true
                            ).show();
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        btnSaveActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringDistance = editDistance.getText().toString().trim();
                String stringTime = editTime.getText().toString().trim();
                String stringDate = editDate.getText().toString().trim();

                if (!isValidDistance(stringDistance)) {
                    Toast.makeText(getContext(), "Invalid distance was entered!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidTime(stringTime)) {
                    Toast.makeText(getContext(), "Invalid time was entered!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidDate(stringDate)) {
                    Toast.makeText(getContext(), "Invalid date was entered!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int time = ConversionUtil.convertTimeToSeconds(stringTime);
                double distance = Double.parseDouble(stringDistance);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date today = new Date();
                String shortDate = dateFormat.format(today);

                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Timestamp timestamp = null;
                try {
                    Date parsedDate = dateTimeFormat.parse(stringDate);
                    timestamp = new Timestamp(parsedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int totalTime = ConversionUtil.convertTimeToSeconds(stringTime);

                CaloriesCalculator caloriesCalculator = new CaloriesCalculator();

                Map<String, Object> data = new HashMap<>();
                System.out.println("Values: " + stringDistance + " " + time);
                data.put("distance", ConversionUtil.kmToMeters(distance));
                data.put("time", totalTime);
                data.put("UserID", UserID);
                data.put("date", timestamp);
                data.put("shortDate", shortDate);
                data.put("pace", calculateAveragePace(stringDistance, time));
                data.put("type", activityType);
                data.put("caloriesBurned", caloriesCalculator.calculateCalories(time, (calculateAveragePace(stringDistance, time)), activityType, getUserWeight()));

                db.collection("Activities")
                        .document(DocumentIDGenerator.GenerateActivityID())
                        .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Successful written activity", "Successfully written activity");
                                Toast.makeText(getContext(), "Successfully saved activity!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getMessage());
                            }
                        });
                editTime.setText("");
                editDistance.setText("");
                editDate.setText("");
            }
        });
    }

    private boolean isValidDistance(String distance) {
        if (distance.isEmpty()) return false;
        return Pattern.matches("\\d+\\.\\d{2}", distance);
    }

    private boolean isValidTime(String time) {
        return Pattern.matches("\\d{2}:\\d{2}", time);
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr.isEmpty()) return false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date inputDate = dateFormat.parse(dateStr);
            return inputDate != null && inputDate.before(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    public static String calculateAveragePace(String distanceStr, int timeInSeconds) {
        double distanceKm = Double.parseDouble(distanceStr);
        double timeInMinutes = timeInSeconds / 60.0;

        if (distanceKm == 0) return "0:00";

        double pace = timeInMinutes / distanceKm;
        int minutes = (int) pace;
        int seconds = (int) ((pace - minutes) * 60);

        return String.format("%d:%02d", minutes, seconds);
    }


    private int getUserWeight() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        long longWeight = sharedPreferences.getLong("Weight", 0);
        return Math.toIntExact(longWeight);
    }
}
