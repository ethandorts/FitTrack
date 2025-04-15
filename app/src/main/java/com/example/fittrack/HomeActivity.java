package com.example.fittrack;

import static android.content.ContentValues.TAG;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {

    private ImageView profileImage, addFriend, btnMessageFriends;
    private TextView UserName, NoActivities;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseUser mAuth;
    private String UserID;
    private ProgressBar loadingActivities;
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private ActivityLocationsDao activityLocationsDao;

    //private ArrayList<ActivityModel> UserActivities = new ArrayList<ActivityModel>();
//    private ActivityViewModel activityViewModel = new ActivityViewModel();
    private ActivitiesRecyclerViewAdapter activitiesAdapter;
    private WearableDataHandler wearableDataHandler = new WearableDataHandler();
    private DocumentSnapshot lastVisible = null;
    private boolean isLoading = false;
    private boolean isEndofArray = false;
    private Handler handler = new Handler();
    private PersonalBestUtil util = new PersonalBestUtil();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        WorkManager.getInstance(this).cancelAllWork();

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(ActivityCompletedChecker.class)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                ActivityCompletedChecker.class, 3, TimeUnit.HOURS).build();

        OneTimeWorkRequest checkGoalsRequest = new OneTimeWorkRequest.Builder(GoalCompletedChecker.class)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build();

        PeriodicWorkRequest periodicCheckGoalsRequest = new PeriodicWorkRequest.Builder(
                ActivityCompletedChecker.class, 30, TimeUnit.MINUTES).build();


        WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
        WorkManager.getInstance(this).enqueue(periodicWorkRequest);
        WorkManager.getInstance(this).enqueue(checkGoalsRequest);
        WorkManager.getInstance(this).enqueue(periodicCheckGoalsRequest);

        logFoodNotifications(10, 00, "Breakfast");
        logFoodNotifications(15, 10, "Lunch");
        logFoodNotifications(20, 00, "Dinner");

        activityLocationsDao = ActivityLocationsDatabase.getActivityLocationsDatabase(this).activityLocationsDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                activityLocationsDao.deleteAllLocations();
            }
        }).start();

        UserName = findViewById(R.id.txtUserName);
        profileImage = findViewById(R.id.profile_image);
        loadingActivities = findViewById(R.id.LoadingActivitiesProgressBar);
        btnMessageFriends = findViewById(R.id.btnMessageFriends);
        addFriend = findViewById(R.id.btnAddActivity);
        NoActivities = findViewById(R.id.noActivitiesMessage);

        Wearable.getNodeClient(this).getConnectedNodes()
                .addOnSuccessListener(new OnSuccessListener<List<Node>>() {
                    @Override
                    public void onSuccess(List<Node> nodes) {
                        if (nodes.isEmpty()) {
                            Log.d(TAG, "No connected nodes.");
                        } else {
                            Log.d(TAG, "Connected nodes: " + nodes.toString());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to get connected nodes: " + e.getMessage());
                    }
                });


        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        UserID = (mAuth != null) ? mAuth.getUid() : "test_user";

        //util.findFastest5K(UserID, 5000, "Running");

        DatabaseUtil.retrieveUserName(UserID, new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UserName.setText(FullName);
                        SharedPreferences storedPI = getSharedPreferences("UserPI", MODE_PRIVATE);
                        SharedPreferences.Editor PIEditor = storedPI.edit();
                        PIEditor.putLong("Weight", weight);
                        PIEditor.putLong("Height", height);
                        PIEditor.putLong("ActivityFrequency", activityFrequency);
                        PIEditor.putLong("DailyCalorieGoal", dailyCalorieGoal);
                        PIEditor.apply();
                    }
                });
            }
        });

        DatabaseUtil.retrieveProfilePicture(UserID + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                if(PicturePath != null) {
                    Glide.with(HomeActivity.this)
                            .load(PicturePath)
                            .into(profileImage);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                    profileImage.setImageResource(R.drawable.profile);
                }
            }
        });

        Query query = db.collection("Activities")
                .whereEqualTo("UserID", UserID)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(20);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timestamp activityDate = document.getTimestamp("date");
                        String id = document.getId();

                        Log.d("FirestoreQuery","Date: " + activityDate + " ID: " + id);
                    }
                } else {
                    Log.w("FirestoreQuery", "Error getting documents.", task.getException());
                }
            }
        });


        FirestoreRecyclerOptions<ActivityModel> options = new FirestoreRecyclerOptions.Builder<ActivityModel>()
                .setQuery(query, ActivityModel.class)
                .setLifecycleOwner(this)
                .build();

        RecyclerView activitiesRecyclerView = findViewById(R.id.activitiesRecyclerView);
        activitiesAdapter = new ActivitiesRecyclerViewAdapter(options,this);
        activitiesRecyclerView.setAdapter(activitiesAdapter);
//        activityViewModel = new ViewModelProvider(this).get(ActivityViewModel.class);
//        activityViewModel.getUserActivities().observe(this, new Observer<ArrayList<ActivityModel>>() {
//
//            @Override
//            public void onChanged(ArrayList<ActivityModel> activityModels) {
//                loadingActivities.setVisibility(View.GONE);
//                activitiesAdapter.updateAdapter(activityModels);
//                activitiesAdapter.showAdapter();
//            }
//        });
        LinearLayoutManager layout = new LinearLayoutManager(this);
        activitiesRecyclerView.setLayoutManager(layout);
        activitiesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


//        activityViewModel.getIsLoading().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                loadingActivities.setVisibility(View.GONE);
//            }
//        });
//
//        activitiesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                int onScreen = layoutManager.getChildCount();
//                int totalItems = layoutManager.getItemCount();
//                int firstItem = layoutManager.findFirstVisibleItemPosition();
//
//                if (!activityViewModel.getIsLoading().getValue() && !activityViewModel.getIsEndofArray().getValue()) {
//                    if ((onScreen + firstItem) >= totalItems - 5 && firstItem >= 0) {
//                        if (handler != null) {
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    loadingActivities.setVisibility(View.VISIBLE);
//                                    activityViewModel.loadUserActivities();
//                                }
//                            }, 2000);
//                        }
//                    }
//                }
//            }
//        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.home_bottom) {
                    // for future button
                } else if (menuItem.getItemId() == R.id.record_bottom) {
                    Intent intent = new Intent(HomeActivity.this, SelectExerciseTypeActivity.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.group_bottom) {
                    Intent intent = new Intent(HomeActivity.this, GroupsMenu.class);
                    startActivity(intent);
                } else if (menuItem.getItemId() == R.id.settings_bottom) {
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

        btnMessageFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, NutritionTrackingOverview.class);
                startActivity(intent);
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ActivitiesByMonthActivity.class);
                intent.putExtra("UserID", UserID);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Wearable.getDataClient(this).addListener(this);
        activitiesAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //UserActivities.clear();
        //activityViewModel.loadUserActivities();
        activityLocationsDao = ActivityLocationsDatabase.getActivityLocationsDatabase(this).activityLocationsDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                activityLocationsDao.deleteAllLocations();
            }
        }).start();
        activitiesAdapter.notifyDataSetChanged();
        activitiesAdapter.startListening();
        DatabaseUtil.retrieveProfilePicture(UserID + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                if(PicturePath != null) {
                    Glide.with(HomeActivity.this)
                            .load(PicturePath)
                            .into(profileImage);
                } else {
                    Log.e("No profile picture found", "No profile picture found.");
                    profileImage.setImageResource(R.drawable.profile);
                }
            }
        });
        isEndofArray = false;
        lastVisible = null;
    }

    protected void onStop() {
        super.onStop();
        Wearable.getDataClient(this).removeListener(this);
        System.out.println("listener stopped");
        activitiesAdapter.stopListening();
    }

    private String formatRunTime(double timePassed) {
        int hours = (int) (timePassed / 3600);
        int minutes = (int) ((timePassed % 3600) / 60);
        int seconds = (int) (timePassed % 60);
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }

    private void logFoodNotifications(int hour, int minute, String mealType) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, LogFoodNotificationReceiver.class);
        intent.putExtra("mealType", mealType);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                hour * 60 + minute,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(alarmManager != null && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
            } else {
                Intent permissionsIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package: " + getPackageName()));
                startActivity(permissionsIntent);
            }
        }
    }
//    private void loadUserActivities() {
//        if( isLoading || isEndofArray) {
//            return;
//        }
//
//        isLoading = true;
//        DatabaseUtil.retrieveUserActivities(UserID, new FirebaseDatabaseHelper.FirestoreActivitiesCallback() {
//            @Override
//            public void onCallback(List<Map<String, Object>> data, DocumentSnapshot lastItemVisible) {
//                for(Map<String, Object> activity : data) {
//                    isLoading = false;
//                    loadingActivities.setVisibility(View.GONE);
//
//                    if(data == null || data.isEmpty()) {
//                        isEndofArray = true;
//                        return;
//                    }
//                    ActivityModel activityInfo = new ActivityModel(
//                            String.valueOf(activity.get("type")),
//                            String.valueOf(activity.get("typeImage")),
//                            (Timestamp) activity.get("date"),
//                            String.valueOf(activity.get("distance")),
//                            formatRunTime(Double.parseDouble(String.valueOf(activity.get("time")))),
//                            String.valueOf(activity.get("pace")),
//                            (String) UserName.getText(),
//                            String.valueOf(activity.get("UserImage")),
//                            (List<Object>) activity.get("activityCoordinates")
//                    );
//                        UserActivities.add(activityInfo);
//                }
//                lastVisible = lastItemVisible;
//                System.out.println(UserActivities.size());
//                activitiesAdapter.notifyDataSetChanged();
//            }
//        }, lastVisible);
//    }

    private void createSyncNotificationChannel() {
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        System.out.println("DataEvent count: " + dataEventBuffer.getCount());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("syncData", "syncNotification", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("SyncNotificationAlert");
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "syncData")
//                    .setSmallIcon(R.drawable.baseline_settings_24)
//                    .setContentTitle("Wearable Data Sync")
//                    .setContentText("Data from your wearable device is syncing...")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//            notificationManager.notify( 1, builder.build());
//        }

        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = event.getDataItem();
                String UriPath = String.valueOf(dataItem.getUri());
                System.out.println(UriPath);

                if (UriPath.contains("/activity-data")) {
                    System.out.println("Inside the URI loop");
                    DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                    ArrayList<DataMap> activitiesList = dataMap.getDataMapArrayList("fitness_data");
                    System.out.println("After initialisation code");
                    if(activitiesList != null) {
                        for(DataMap activityData : activitiesList) {
                            System.out.println("Data saving...");
                            wearableDataHandler.saveWearableData(UserID,
                                    activityData.get("distance"),
                                    activityData.get("duration"));
                        }
                    } else {
                        System.out.println("List is not full");
                    }
                }
            }
        }
    }
}