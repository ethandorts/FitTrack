package com.example.fittrack;

import static java.security.AccessController.getContext;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditMeetupActivity extends AppCompatActivity {
    private EditText editMeetupTitle, editDate, editLocation, editDetails;
    private Button btnSaveMeetup, btnDeleteMeetup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meetup);

        editMeetupTitle = findViewById(R.id.editMeetupTitle);
        editDate = findViewById(R.id.editMeetupDate);
        editLocation = findViewById(R.id.editMeetupLocation);
        editDetails = findViewById(R.id.editMeetupDetails);
        btnSaveMeetup = findViewById(R.id.btnSaveMeetup);
        btnDeleteMeetup = findViewById(R.id.btnDeleteMeetup);

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(
                        EditMeetupActivity.this,
                        (datePicker, year, month, day) -> {
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, day);

                            new TimePickerDialog(
                                    EditMeetupActivity.this,
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


        Intent intent = getIntent();
        String GroupID = intent.hasExtra("GroupID") ? intent.getStringExtra("GroupID") : "";
        String MeetupID = intent.hasExtra("MeetupID") ? intent.getStringExtra("MeetupID") : "";
        String UserID = intent.hasExtra("User") ? intent.getStringExtra("User") : "";
        String Title = intent.hasExtra("Title") ? intent.getStringExtra("Title") : "";
        String Date = intent.hasExtra("Date") ? intent.getStringExtra("Date") : "";
        String Location = intent.hasExtra("Location") ? intent.getStringExtra("Location") : "";
        String Description = intent.hasExtra("Description") ? intent.getStringExtra("Description") : "";

        String[] acceptedArray = intent.getStringArrayExtra("Accepted");
        String[] rejectedArray = intent.getStringArrayExtra("Rejected");

        List<String> accepted = (acceptedArray != null) ? Arrays.asList(acceptedArray) : Collections.emptyList();
        List<String> rejected = (rejectedArray != null) ? Arrays.asList(rejectedArray) : Collections.emptyList();

        editMeetupTitle.setText(Title);
        editDate.setText(Date);
        editLocation.setText(Location);
        editDetails.setText(Description);

        btnDeleteMeetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupsUtil.deleteMeetup(GroupID, MeetupID);
                finish();
            }
        });

        btnSaveMeetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String meetupTitle = editMeetupTitle.getText().toString();
                String meetupDate = editDate.getText().toString();
                String meetupLocation = editLocation.getText().toString();
                String meetupDescription = editDetails.getText().toString();

                if(TextUtils.isEmpty(meetupTitle)) {
                    editMeetupTitle.setError("Invalid input for Meetup Title!");
                    return;
                }

                if(TextUtils.isEmpty(meetupDate)) {
                    editDate.setError("Invalid input for Meetup Date!");
                    return;
                }

                if(TextUtils.isEmpty(meetupLocation)) {
                    editLocation.setError("Invalid input for Meetup Location!");
                    return;
                }

                if(TextUtils.isEmpty(meetupDescription)) {
                    editDetails.setError("Invalid input for Meetup Description!");
                    return;
                }

                groupsUtil.updateMeetup(GroupID, MeetupID, UserID, meetupTitle, ConversionUtil.StringtoTimeStamp(meetupDate), meetupLocation, meetupDescription, accepted, rejected);
                finish();
            }
        });
    }
}