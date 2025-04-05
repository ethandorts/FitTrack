package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

public class ActivitiesByMonthActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerMonthActivities;
    private ActivitiesRecyclerViewAdapter activitiesAdapter;
    private Spinner monthSpinner;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_by_month);

        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");


        recyclerMonthActivities = findViewById(R.id.recyclerActivitiesMonth);
        monthSpinner = findViewById(R.id.monthSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.activities_by_month, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        String currentMonth = new DateFormatSymbols().getMonths()[Calendar.getInstance().get(Calendar.MONTH)] +
                " " + Calendar.getInstance().get(Calendar.YEAR);
        int currentPosition = adapter.getPosition(currentMonth);
        if (currentPosition >= 0) {
            monthSpinner.setSelection(currentPosition);
        }

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString().trim();
                String[] parts = selected.split(" ");
                if (parts.length != 2) return;

                int year = Integer.parseInt(parts[1]);
                int month = getSelectedMonth(parts[0].trim());

                loadActivitiesForMonth(month, year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        recyclerMonthActivities.setLayoutManager(new LinearLayoutManager(this));
        recyclerMonthActivities.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadActivitiesForMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endDate = calendar.getTime();


        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ActivityModel> options = new FirestoreRecyclerOptions.Builder<ActivityModel>()
                .setQuery(query, ActivityModel.class)
                .setLifecycleOwner(this)
                .build();

        if (activitiesAdapter != null) {
            activitiesAdapter.stopListening();
        }

        activitiesAdapter = new ActivitiesRecyclerViewAdapter(options, this);
        recyclerMonthActivities.setAdapter(activitiesAdapter);
        activitiesAdapter.startListening();
    }

    private int getSelectedMonth(String month) {
        String[] months = new DateFormatSymbols().getMonths();
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(month)) return i;
        }
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (activitiesAdapter != null) {
            activitiesAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activitiesAdapter != null) {
            activitiesAdapter.stopListening();
        }
    }
}
