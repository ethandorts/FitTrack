package com.example.fittrack;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateGoalActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoalCheckerUtil goalCheckerUtil = new GoalCheckerUtil();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);

        Spinner spinnerGoalTypes = findViewById(R.id.spinnerGoalType);
        FrameLayout formDisplay = findViewById(R.id.goalFormFrame);

        //goalCheckerUtil.checkDistanceGoalIsAchieved(UserID);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.goal_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerGoalTypes.setAdapter(adapter);

        spinnerGoalTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedGoal = adapterView.getItemAtPosition(i).toString();
                switch (selectedGoal) {
                    case "Distance Goal":
                        loadFragment(new DistanceGoalFragment());
                        break;
                    case "Time Goal":
                        loadFragment(new TimeGoalFragment());
                        break;
                    default:
                        Log.e("Goal Form Error", "Error showing Goal Form");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.goalFormFrame, fragment);
        transaction.commit();
    }
}