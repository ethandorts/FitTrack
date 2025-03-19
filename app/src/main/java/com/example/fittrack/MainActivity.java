package com.example.fittrack;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.health.connect.datatypes.ExerciseRoute;
import android.health.connect.datatypes.units.Power;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private LocationUpdatesBroadcastReceiver updatesBroadcastReceiver;
    private GoogleMap googleMap;
    private Location previousLocation;
    private double distanceTravelled;
    private TextView txtDistanceTravelled;
    private TextView txtRunTime;
    private TextView speedView;
    private LatLng currentLocation;
    private boolean isTrackingRun;
    private boolean isTimerStarted;
    private Handler runTimeHandler;
    private Runnable timer;
    private long startTime;
    private int targetKM = 1000;
    private int elapsedTime;
    private double milestoneTarget = 1000;
    private long previousTime = 0;
    private boolean isEnd = false;
    private long timeNow;
    private long splitTime;
    private long fullTime;
    private ArrayList<ActivityLocationsEntity> entities;
    private ActivityLocationsDao activityLocationsDao;
    private List<Long> kmSplits = new ArrayList<>();
    private TextToSpeech DistanceTalker;
    private PowerManager.WakeLock wakelock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityLocationsDao = ActivityLocationsDatabase.getActivityLocationsDatabase(this).activityLocationsDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                activityLocationsDao.deleteAllLocations();
            }
        });

        Intent intent = getIntent();
        String type = intent.getStringExtra("Type");

        txtDistanceTravelled = findViewById(R.id.txtDistanceTravelled);
        Button btnClear = findViewById(R.id.btnClearMap);
        Button btnStopStart = findViewById(R.id.stopStartBtn);
        txtRunTime = findViewById(R.id.txtRunTime);
        speedView = findViewById(R.id.currentSpeed);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Fittrack:PREVENT_DOZE_MODE");
        wakelock.acquire();
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestBatteryOptimizationExemption();
        runTimeHandler = new Handler();
        timer = new Runnable() {
            @Override
            public void run () {
                if(isTrackingRun) {
                    elapsedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
                    txtRunTime.setText(formatRunTime(elapsedTime));
                    runTimeHandler.postDelayed(this, 1000);
                }
            }
        };

        DistanceTalker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    DistanceTalker.setLanguage(Locale.UK);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        txtDistanceTravelled.setText(String.format("%.2f km", distanceTravelled / 1000));

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                distanceTravelled = 0.00;
                Toast.makeText(MainActivity.this, "Map and Distance Travelled Reset", Toast.LENGTH_SHORT);
            }
        });

        //Need to figure out how to resume the clock

        btnStopStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTrackingRun == false) {
                    isTrackingRun = true;
                    isEnd = false;
                    startTimer();
                    btnStopStart.setText("Stop Run");
                    btnStopStart.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    isTrackingRun = false;
                    isEnd = true;
                    Location nowLocation = new Location("nowLocation");
                    nowLocation.setLatitude(currentLocation.latitude);
                    nowLocation.setLongitude(currentLocation.longitude);
                    kmSplit(previousLocation, nowLocation,true);
                    btnStopStart.setText("Resume Run");
                    btnStopStart.setBackgroundColor(getResources().getColor(R.color.green));
                    SaveActivityDialog saveActivityDialog = new SaveActivityDialog(getApplicationContext());
                    long runTime = 0;

                    for(long split : kmSplits) {
                        runTime = runTime + split;
                    }
                    int activityTime = (int) runTime / 1000;

                    Bundle runningData = new Bundle();
                    runningData.putDouble("distance", distanceTravelled);
                    runningData.putDouble("time", activityTime);
                    runningData.putString("type", type);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            List<ActivityLocationsEntity> entitiesList = activityLocationsDao.retrieveAllLocations();
//                            List<LatLng> locations = new ArrayList<>();
//                            for( ActivityLocationsEntity entity: entitiesList) {
//                                if(entitiesList != null) {
//                                    locations.add(new LatLng(entity.getLatitude(), entity.getLongitude()));
//                                } else {
//                                    System.out.println("No entities in activity database ");
//                                }
//                            }
//                            runningData.putParcelableArrayList("activityLocations", (ArrayList<? extends Parcelable>) locations);
//                        }
//                    }).start();
                    long[] splits = new long[kmSplits.size()];
                    for(int i = 0; i < kmSplits.size(); i++) {
                        splits[i] = kmSplits.get(i);
                    }
                    runningData.putLongArray("splits", splits);

                    saveActivityDialog.setArguments(runningData);
                    saveActivityDialog.show(getSupportFragmentManager(), "SaveActivity");
                }
            }
        });
        startTrackingLocation();
    }

   private void startTimer() {
       isTimerStarted = true;
       startTime = System.currentTimeMillis();
       runTimeHandler.post(timer);
   }

    private void stopTimer() {
        isTimerStarted = false;
        runTimeHandler.removeCallbacks(timer);
        elapsedTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
    }

    public void startTrackingLocation() {
        Intent locationTracker = new Intent(MainActivity.this, LocationTracker.class);
        startForegroundService(locationTracker);
        LocationUpdatesBroadcastReceiver receiver = new LocationUpdatesBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("com.example.broadcast.LOCATION_UPDATE");
        registerReceiver(receiver, intentFilter);
    }

     //Updates Map with latest location found.
    private void updateMapWithLocation(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Location lastKnownLocation = previousLocation;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        if(isTrackingRun) {
            if (!isTimerStarted) {
                //startTimer();
                DistanceTalker.speak("Activity Started", TextToSpeech.QUEUE_FLUSH, null);
            }
            DrawRoute(location);
            DistanceSpeakerAssistant(distanceTravelled);
            kmSplit(lastKnownLocation,location, false);
        } else {
//            if (isEnd) {
//                kmSplit(true);
//                isEnd = false;
//            }
            stopTimer();
        }
    }

    private void DrawRoute(Location location) {
        if(previousLocation == null) {System.out.println("PreviousLocation is null");}
        if(previousLocation != null) {
            double distanceTravelledInterval = previousLocation.distanceTo(location);
            distanceTravelled += distanceTravelledInterval;
            String.valueOf(distanceTravelled);
            txtDistanceTravelled.setText(String.format("%.2f KM", distanceTravelled / 1000));
            LatLng prevLocation = new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude());
            googleMap.addPolyline(new PolylineOptions()
                    .add(prevLocation, currentLocation)
                    .clickable(true)
                    .width(6)
                    .color(Color.RED));
        }
    }

    private void startRunningClock() {
        LocalDate date = LocalDate.now();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent locationTracker = new Intent(MainActivity.this, LocationTracker.class);
        stopService(locationTracker);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Activity Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Activity Resumed");
        System.out.println(isTrackingRun + " :result");
        Intent locationTracker = new Intent(MainActivity.this, LocationTracker.class);
        startForegroundService(locationTracker);
    }

    // Requests appropriate location permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                Log.d("MainActivity", "Location permissions denied");
            }
        }
    }

    private String formatRunTime(int timePassed) {
        int hours = timePassed / 3600;
        int minutes = (timePassed % 3600) / 60;
        int seconds = timePassed % 60;
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }

    private void DistanceSpeakerAssistant(double DistanceTravelled) {
        while(distanceTravelled >= milestoneTarget) {
            DistanceTalker.speak(milestoneTarget + " metres done" , TextToSpeech.QUEUE_FLUSH, null);
            milestoneTarget += 1000;
        }
    }

    private void kmSplit(Location prevLocation, Location currLocation, boolean isFinalSplit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("kmSplit called with isFinalSplit = " + isFinalSplit);

                if (prevLocation == null || currLocation == null) {
                    Log.e("kmSplit", "Previous or Current location is null!");
                    System.out.println("prevLocation: " + prevLocation);
                    System.out.println("currLocation: " + currLocation);
                    return;
                }

                double distanceInterval = prevLocation.distanceTo(currLocation);
                System.out.println("Distance Interval: " + distanceInterval);

                if (!isFinalSplit && distanceInterval == 0) {
                    System.out.println("Distance Interval is 0, returning early");
                    return;
                }

                long currTime = System.currentTimeMillis();

                synchronized (kmSplits) {
                    System.out.println("Entering synchronized block");

                    while (distanceTravelled >= (kmSplits.size() + 1) * 1000) {
                        double nextSplitDistance = (kmSplits.size() + 1) * 1000;
                        if (distanceTravelled < nextSplitDistance) break;

                        double fraction = (nextSplitDistance - (distanceTravelled - distanceInterval)) / distanceInterval;
                        fraction = Math.max(0, Math.min(1, fraction));
                        long interpolatedTime = previousTime + Math.round(fraction * (currTime - previousTime));

                        if (kmSplits.isEmpty()) {
                            kmSplits.add(interpolatedTime - startTime);
                        } else {
                            kmSplits.add(interpolatedTime - (startTime + kmSplits.stream().mapToLong(Long::longValue).sum()));
                        }

                        System.out.println("Splits currently: " + kmSplits);
                        System.out.println("kmSplit Hit " + nextSplitDistance + "m at " + interpolatedTime);
                    }

                    if (isFinalSplit) {
                        System.out.println("Entering isFinalSplit block");
                        long totalSplits = 0;
                        double remainingDistance = distanceTravelled % 1000;
                        System.out.println("Remaining Distance: " + remainingDistance);
                        if (remainingDistance > 0) {
                            System.out.println("Calculating final split...");
                            for(long split : kmSplits) {
                                totalSplits = totalSplits + split;
                            }
                            int splitsSecondsTotal = secondsConversion(totalSplits);
                            int difference = elapsedTime - splitsSecondsTotal;
                            System.out.println(difference);
//                            long totalTime = currTime - startTime;
//                            long finalSplit = totalTime - totalSplits;
                            kmSplits.add((long) (difference * 1000));
                            System.out.println("Final split added: " + remainingDistance + "m in " + difference + "ms");
                            System.out.println("KM Splits Display:" + kmSplits);
                            for(long split : kmSplits) {
                                totalSplits = totalSplits + split;
                            }
                            System.out.println("Time: " + totalSplits);
                            System.out.println("elapsedTime" + elapsedTime);
                        } else {
                            System.out.println("No remaining distance for final split");
                        }
                    }
                }
                previousTime = currTime;
            }
        }).start();
    }



    private void DisplaySplitFragment(int Km, long splitTime, long fullTime) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SplitDialogFragment fragment = new SplitDialogFragment();

        Bundle splitData = new Bundle();
        splitData.putInt("Km", Km);
        splitData.putLong("splitTime", splitTime);
        splitData.putLong("fullTime", fullTime);
        fragment.setArguments(splitData);

        fragment.show(fragmentManager, "SplitDialogFragment");
    }

    private void requestBatteryOptimizationExemption() {
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(android.net.Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    public void ClearMap() {
        googleMap.clear();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng initialLocation = new LatLng(54.597, -5.930);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12));
    }

    @Override
    public void onBackPressed() {
        if(isTrackingRun) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit recording this fitness activity?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.super.onBackPressed();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            AlertDialog exitActivityTracking = builder.create();
            exitActivityTracking.show();
        } else {
            super.onBackPressed();
        }
    }

    private int secondsConversion(long splitsTotal) {
        return (int) (splitsTotal / 1000);
    }

    public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "LocationUpdatesBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Location newLocation = intent.getParcelableExtra("location");
            float currentSpeed = intent.getFloatExtra("speed", 0);
            //activityLocations.add(new LatLng(newLocation.getLatitude(), newLocation.getLongitude()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ActivityLocationsEntity entity = new ActivityLocationsEntity();
                    entity.setLatitude(newLocation.getLatitude());
                    entity.setLongitude(newLocation.getLongitude());
                    entity.setTime(System.currentTimeMillis());
                    System.out.println("Database Input: " + entity.getLatitude() + " " + entity.getLongitude());
                    if(isTrackingRun) {
                        activityLocationsDao.addLocation(entity);
                    }
                }
            }).start();
            //System.out.println(newLocation);
            updateMapWithLocation(newLocation);
            previousLocation = newLocation;
            speedView.setText(ConversionUtil.convertToMinutesPerKM(currentSpeed));
        }
    }
}
