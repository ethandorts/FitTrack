package com.example.fittrack;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateMeetupDialogFragment extends DialogFragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getCurrentUser().getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnSaveMeetup;
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private String GroupID;
    private EditText editTitle, editDate,editTime, editLocation, editDescription;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.save_meetup_dialog, null);

        btnSaveMeetup = view.findViewById(R.id.btnCreateFitnessGroup);
        editTitle = view.findViewById(R.id.editCreateGroupName);
        editDate = view.findViewById(R.id.editFitnessActivity);
        editTime = view.findViewById(R.id.editGroupLocation);
        editLocation = view.findViewById(R.id.editTextLocation);
        editDescription = view.findViewById(R.id.editCreateGroupDescription);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%02d", year, month + 1, day);
                                editDate.setText(selectedDate);
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timeDialog = new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                                editTime.setText(time);
                            }
                        }, hour, minute, true
                );
                timeDialog.show();
            }
        });

        btnSaveMeetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getArguments() != null) {
                    GroupID = getArguments().getString("GroupID");
                }
                String stringDate = editDate.getText().toString();
                String stringTime = editTime.getText().toString();

                Timestamp meetupDate = convertDate(stringDate, stringTime);
                groupsUtil.createNewMeetup(GroupID,
                        UserID,
                        editTitle.getText().toString(),
                        meetupDate,
                        editLocation.getText().toString(),
                        editDescription.getText().toString()
                        );
                dismiss();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Create Meetup")
                .create();
    }

    public Timestamp convertDate(String date, String time) {
        String dateTime = date + " " + time;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        Date selectedDate = Date.from(zonedDateTime.toInstant());

        return new Timestamp(selectedDate);
    }
}
