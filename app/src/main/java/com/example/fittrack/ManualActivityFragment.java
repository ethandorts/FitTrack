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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

    public ManualActivityFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manual_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText editDate = view.findViewById(R.id.editManualDate);
        final EditText editDistance = view.findViewById(R.id.editManualDistance);
        final EditText editTime = view.findViewById(R.id.editManualTime);
        final AutoCompleteTextView spinnerManual = view.findViewById(R.id.editManualType);
        final Button btnSaveActivity = view.findViewById(R.id.btnSaveManualActivity);

        editDate.setShowSoftInputOnFocus(false);
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, day);

                                new TimePickerDialog(
                                        getContext(),
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
                                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                                calendar.set(Calendar.MINUTE, minute);
                                                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                                editDate.setText(dateTimeFormat.format(calendar.getTime()));
                                            }
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                ).show();
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        final String[] activityTypes = {"Running", "Walking", "Cycling"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, activityTypes);
        spinnerManual.setAdapter(adapter);
        spinnerManual.setText("Running", false);
        activityType = "Running";

        spinnerManual.setShowSoftInputOnFocus(false);
        spinnerManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerManual.showDropDown();
            }
        });

        spinnerManual.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                activityType = parent.getItemAtPosition(position).toString();
            }
        });

        btnSaveActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringDistance = editDistance.getText().toString().trim();
                String stringTime = editTime.getText().toString().trim();
                String stringDate = editDate.getText().toString().trim();

                if (!isValidDistance(stringDistance)) {
                    Toast.makeText(getContext(), "Please enter a valid distance greater than 0.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidTime(stringTime)) {
                    Toast.makeText(getContext(), "Time must be in format HH:mm:ss and greater than 00:00:00.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidDate(stringDate)) {
                    Toast.makeText(getContext(), "Enter a valid past date (not before year 2000).", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (activityType == null || activityType.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please select an activity type.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int totalTime = ConversionUtil.convertTimeToSeconds(stringTime);
                double distance = Double.parseDouble(stringDistance);
                int userWeight = getUserWeight();

                if (!isValidWeight(userWeight)) {
                    Toast.makeText(getContext(), "Invalid or missing weight. Please update your profile.", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date today = new Date();
                String shortDate = dateFormat.format(today);

                Timestamp timestamp = null;
                try {
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    Date parsedDate = dateTimeFormat.parse(stringDate);
                    timestamp = new Timestamp(parsedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                CaloriesCalculator caloriesCalculator = new CaloriesCalculator();
                String activityID = DocumentIDGenerator.GenerateActivityID();

                Map<String, Object> data = new HashMap<>();
                data.put("ActivityID", activityID);
                data.put("distance", ConversionUtil.kmToMeters(distance));
                data.put("time", totalTime);
                data.put("UserID", UserID);
                data.put("date", timestamp);
                data.put("shortDate", shortDate);
                data.put("pace", calculateAveragePace(stringDistance, totalTime));
                data.put("type", activityType);
                data.put("caloriesBurned", caloriesCalculator.calculateCalories(
                        totalTime,
                        calculateAveragePace(stringDistance, totalTime),
                        activityType,
                        userWeight)
                );

                db.collection("Activities")
                        .document(activityID)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Successfully saved activity!", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Firebase Error", e.getMessage());
                                Toast.makeText(getContext(), "Failed to save activity", Toast.LENGTH_SHORT).show();
                            }
                        });

                editTime.setText("");
                editDistance.setText("");
                editDate.setText("");
            }
        });
    }

    private boolean isValidDistance(String distance) {
        if (distance.isEmpty() || !Pattern.matches("\\d+(\\.\\d{1,2})?", distance)) {
            return false;
        }
        try {
            double d = Double.parseDouble(distance);
            return d > 0 && d <= 1000;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidTime(String time) {
        if (!Pattern.matches("\\d{1,2}:\\d{2}:\\d{2}", time)) {
            return false;
        }

        String[] parts = time.split(":");
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            int totalSeconds = hours * 3600 + minutes * 60 + seconds;

            return hours >= 0 && minutes >= 0 && minutes < 60 && seconds >= 0 && seconds < 60 && totalSeconds > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidDate(String dateStr) {
        if (dateStr.isEmpty()) return false;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date inputDate = dateFormat.parse(dateStr);
            Date now = new Date();
            Calendar minDate = Calendar.getInstance();
            minDate.set(2000, Calendar.JANUARY, 1);
            return inputDate != null && inputDate.before(now) && inputDate.after(minDate.getTime());
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidWeight(int weight) {
        return weight > 30 && weight <= 300;
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
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        long longWeight = sharedPreferences.getLong("Weight", 0);
        return Math.toIntExact(longWeight);
    }
}
