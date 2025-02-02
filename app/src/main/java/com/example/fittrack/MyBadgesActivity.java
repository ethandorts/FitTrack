package com.example.fittrack;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MyBadgesActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private RecyclerView recyclerBadges;
    private BadgesUtil badgesUtil = new BadgesUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_badges);

        recyclerBadges = findViewById(R.id.recyclerBadges);

        badgesUtil.retrieveUserBadges(UserID, new BadgesUtil.BadgesCallback() {
            @Override
            public void onCallback(List<String> badges) {
                ArrayList<BadgeModel> badgesList = new ArrayList<>();
                for(String badge : badges) {
                    BadgeModel newBadge = new BadgeModel(badge, 0);
                    badgesList.add(newBadge);
                }
                BadgesRecyclerViewAdapter adapter = new BadgesRecyclerViewAdapter(getApplicationContext(), badgesList);
                recyclerBadges.setAdapter(adapter);
                recyclerBadges.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });

    }
}