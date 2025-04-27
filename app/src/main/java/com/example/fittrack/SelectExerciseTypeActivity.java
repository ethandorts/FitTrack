package com.example.fittrack;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectExerciseTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise_type);

        ArrayList<ExerciseTypeModel> exerciseTypes = new ArrayList<>();
        exerciseTypes.add(new ExerciseTypeModel("Running", R.drawable.running));
        exerciseTypes.add(new ExerciseTypeModel("Walking", R.drawable.walking_icon));
        exerciseTypes.add(new ExerciseTypeModel("Cycling", R.drawable.cycling_icon));


        RecyclerView exerciseTypesRecycler = findViewById(R.id.recyclerViewExerciseType);
        ExerciseTypeRecyclerAdapter adapter = new ExerciseTypeRecyclerAdapter(this, exerciseTypes);
        exerciseTypesRecycler.setAdapter(adapter);
        exerciseTypesRecycler.setLayoutManager(new LinearLayoutManager(this));
        exerciseTypesRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}