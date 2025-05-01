package com.example.fittrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser signedIn = mAuth.getCurrentUser();
    private UserDeleter userDeleter = new UserDeleter(db);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ArrayList<MenuOptionModel> menuOptions = new ArrayList<>();
        menuOptions.add(new MenuOptionModel("Profile Details", R.drawable.profile_edit));
        menuOptions.add(new MenuOptionModel("My Fitness Activities", R.drawable.running));
        menuOptions.add(new MenuOptionModel("My Fitness Goals", R.drawable.goals_image));
        menuOptions.add(new MenuOptionModel("My Fitness Planner", R.drawable.planner));
        menuOptions.add(new MenuOptionModel("My Fitness Badges", R.drawable.badges));
        menuOptions.add(new MenuOptionModel("Personal Bests", R.drawable.trophy));
        menuOptions.add(new MenuOptionModel("AI Fitness Coach", R.drawable.robot_ai));
        menuOptions.add(new MenuOptionModel("Nutrition", R.drawable.nutrition));


        RecyclerView recyclerMenu = findViewById(R.id.recyclerFitTrackMenu);
        MenuOptionsRecyclerAdapter adapter = new MenuOptionsRecyclerAdapter(this, menuOptions);
        recyclerMenu.setAdapter(adapter);
        recyclerMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerMenu.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

//        Button btnGoals = findViewById(R.id.btnGoals);
//        Button btnCalendar = findViewById(R.id.btnCalendar);
        Button btnLogout = findViewById(R.id.btnLogOut);
//        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
//        Button btnBadges = findViewById(R.id.btnBadges);
//        Button btnAIAssistant = findViewById(R.id.btnAI);
//
//        btnBadges.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingsActivity.this, MyBadgesActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnGoals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingsActivity.this, GoalSettingActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btnCalendar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingsActivity.this, Calendar.class);
//                startActivity(intent);
//            }
//        });
//
//        btnAIAssistant.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingsActivity.this, AIAssistantActivity.class);
//                startActivity(intent);
//            }
//        });
//
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserPI", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                mAuth.signOut();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
//
//        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
//                String UserID = User.getUid();
//                User.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(getApplicationContext(), "User deleted", Toast.LENGTH_LONG);
//                        userDeleter.DeleteUsersChatChannels(UserID);
//                        userDeleter.DeleteActivitiesFromUsers(UserID);
//                        userDeleter.DeleteFromUsersTable(UserID);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("Error deleting the user" + e);
//                    }
//                });
//            }
//        });
    }
}