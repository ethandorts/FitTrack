package com.example.fittrack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class Calendar extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private EventUtil eventUtil = new EventUtil(db);
    private EventsRecyclerViewAdapter eventsAdapter;
    private RecyclerView recyclerView;
    private ImageButton btnAddEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        AndroidThreeTen.init(this);

        MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerViewCalendarEvent);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        calendarView.state().edit()
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();

        LocalDate today = LocalDate.now();
        CalendarDay currentDay = CalendarDay.from(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        calendarView.setSelectedDate(currentDay);

        loadFitnessEvents(currentDay);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                btnAddEvent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SaveEventDialogFragment fragment = new SaveEventDialogFragment();
                        Bundle arguments = new Bundle();
                        arguments.putString("onDate", date.getDate().toString());
                        fragment.setArguments(arguments);
                        fragment.show(getSupportFragmentManager(), "SaveEvent");
                    }
                });

                System.out.println(date.getDate().toString());
                loadFitnessEvents(date);
            }
        });

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveEventDialogFragment fragment = new SaveEventDialogFragment();
                fragment.show(getSupportFragmentManager(), "SaveEvent");
            }
        });


    }

    public void loadFitnessEvents(CalendarDay date) {
        Query query = db.collection("Users")
                .document(UserID)
                .collection("Calendar")
                .whereEqualTo("DateTime", date.getDate().toString());

        System.out.println("Size of recyc: " + recyclerView.getChildCount());

        FirestoreRecyclerOptions<EventModel> options =
                new FirestoreRecyclerOptions.Builder<EventModel>()
                        .setQuery(query, EventModel.class)
                        .build();

        if (eventsAdapter == null) {
            eventsAdapter = new EventsRecyclerViewAdapter(options, getApplicationContext());
            recyclerView.setAdapter(eventsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            eventsAdapter.notifyDataSetChanged();
            eventsAdapter.updateOptions(options);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventsAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventsAdapter.stopListening();
    }
}