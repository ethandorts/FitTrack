package com.example.fittrack;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

        AutoCompleteTextView spinnerGoalTypes = findViewById(R.id.spinnerGoalType);
        FrameLayout formDisplay = findViewById(R.id.goalFormFrame);

        //goalCheckerUtil.checkDistanceGoalIsAchieved(UserID);
        String[] goalTypes = new String[] {"Distance", "Time"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                goalTypes
        );

        spinnerGoalTypes.setAdapter(adapter);
        spinnerGoalTypes.setText("Distance", false);
        loadFragment(new DistanceGoalFragment());
        spinnerGoalTypes.setOnTouchListener((v, event) -> {
            spinnerGoalTypes.showDropDown();
            return false;
        });

        spinnerGoalTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedGoal = parent.getItemAtPosition(position).toString();
                switch (selectedGoal) {
                    case "Distance":
                        loadFragment(new DistanceGoalFragment());
                        break;
                    case "Time":
                        loadFragment(new TimeGoalFragment());
                        break;
                    default:
                        Log.e("Goal Form Error", "Error showing Goal Form");
                        break;
                }
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