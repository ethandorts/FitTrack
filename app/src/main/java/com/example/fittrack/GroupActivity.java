package com.example.fittrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class GroupActivity extends AppCompatActivity {
    TextView txtGroupName;
    ViewPager2 pager;
//    private ActivitiesRecyclerViewAdapter groupActivitiesAdapter;
//    private GroupActivitiesViewModel groupActivityViewModel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GroupsDatabaseUtil groupsUtil = new GroupsDatabaseUtil(db);
    private ProgressBar loadingActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        txtGroupName = findViewById(R.id.txtGroupName);
        ImageButton btnGamification = findViewById(R.id.btnGamification);
        ImageButton btnGroupMessage = findViewById(R.id.btnGroupMessage);
        ImageButton btnGroupInformation = findViewById(R.id.btnGroupInfo);
        TabLayout tabs = findViewById(R.id.activityTabLayout);
        Intent intent = getIntent();
        String GroupID = intent.getStringExtra("GroupID");
        String groupName = intent.getStringExtra("GroupName");
        String activityType = intent.getStringExtra("ActivityType");
        int GroupSize = intent.getIntExtra("MembersValue", 0);
        String GroupShortDescription = intent.getStringExtra("GroupShortDescription");
        System.out.println("Buzz Lightyear: " + GroupSize);
        String GroupActivity = intent.getStringExtra("ActivityType");
        txtGroupName.setText(groupName);

        pager = findViewById(R.id.viewPager3);
        GroupActivitiesFragmentStateAdapter adapter = new GroupActivitiesFragmentStateAdapter(this);
        adapter.addFragment(new GroupActivitiesFragment(GroupID));
        adapter.addFragment(new GroupPostsFragment(GroupID));
        adapter.addFragment(new GroupMeetupsFragment(GroupID));
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs, pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Activities");
                                break;
                            case 1:
                                tab.setText("Posts");
                                break;
                            case 2:
                                tab.setText("Meetup Requests");
                                break;
                        }
                    }
                }).attach();

        btnGroupMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, DirectMessagingMenu.class);
                intent.putExtra("GroupID", GroupID);
                startActivity(intent);
            }
        });
        btnGamification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, GamificationGraph.class);
                intent.putExtra("GroupID", GroupID);
                intent.putExtra("ActivityType", activityType);
                startActivity(intent);
            }
        });

        btnGroupInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, GroupOverviewActivity.class);
                System.out.println(GroupID + " " + GroupSize + " " + groupName + " " + GroupActivity);
                intent.putExtra("GroupID", GroupID);
                intent.putExtra("GroupSize", GroupSize);
                intent.putExtra("GroupName", groupName);
                intent.putExtra("GroupActivity", GroupActivity);
                intent.putExtra("GroupShortDescription", GroupShortDescription);
                startActivity(intent);
            }
        });

        Timestamp now = Timestamp.now();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CollectionReference meetupsRef = db
                        .collection("Groups")
                        .document(GroupID)
                        .collection("Meetups");

                meetupsRef.whereLessThan("Date", now)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                WriteBatch batch = db.batch();

                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    batch.delete(document.getReference());
                                }

                                batch.commit()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Meetup Deleted Successfully", "Previous meetups deleted successfully.");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("Firestore Meetup Cleanup", "Error deleting meetups", e);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Can't retrieve meetups", e.getMessage());
                            }
                        });
            }
        }).start();
    }
}