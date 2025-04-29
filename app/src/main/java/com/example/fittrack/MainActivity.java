package com.example.fittrack;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

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
    private long pausedTime = 0;
    private long pauseStartTime = 0;
    private boolean isRunEnding = false;
    private boolean isPaused = false;
    private boolean isEnd = false;
    private long timeNow;
    private long splitTime;
    private long fullTime;
    private int locationUpdateCount = 0;
    long timeElapsed = 0;
    private ArrayList<ActivityLocationsEntity> entities;
    private ActivityLocationsDao activityLocationsDao;
    private List<Long> kmSplits = new ArrayList<>();
    private TextToSpeech DistanceTalker;
    private PowerManager.WakeLock wakelock;
    private long totalPausedTime = 0;
    private long lastPauseStartTime = 0;
    private long previousEffectiveTime = 0;
    private List<Long> pauseDurations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationUtil.createSplitAchievementNotificationChannel(this);

        activityLocationsDao = ActivityLocationsDatabase.getActivityLocationsDatabase(this).activityLocationsDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                activityLocationsDao.deleteAllLocations();
            }
        }).start();

        Intent intent = getIntent();
        String type = intent.getStringExtra("Type");

        txtDistanceTravelled = findViewById(R.id.txtDistanceTravelled);
        Button btnClear = findViewById(R.id.btnClearMap);
        Button btnStopStart = findViewById(R.id.stopStartBtn);
        txtRunTime = findViewById(R.id.txtRunTime);
        speedView = findViewById(R.id.txtSpeedView);

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Fittrack:PREVENT_DOZE_MODE");
        wakelock.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestBatteryOptimizationExemption();
        runTimeHandler = new Handler();
        timer = new Runnable() {
            @Override
            public void run() {
                if (isTrackingRun) {
                    long currentTime = System.currentTimeMillis();
                    timeElapsed = currentTime - startTime - totalPausedTime;
                    txtRunTime.setText(formatRunTime((int) (timeElapsed / 1000)));
                    runTimeHandler.postDelayed(this, 1000);
                }
            }
        };

        DistanceTalker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    DistanceTalker.setLanguage(Locale.UK);
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        txtDistanceTravelled.setText(String.format("%.2f KM", distanceTravelled / 1000));

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                distanceTravelled = 0.00;
                Toast.makeText(MainActivity.this, "Map and Distance Travelled Reset", Toast.LENGTH_SHORT).show();
            }
        });

        btnStopStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTrackingRun && !isPaused && !isRunEnding) {
                    // Start new run
                    isTrackingRun = true;
                    isRunEnding = false;
                    startTime = System.currentTimeMillis();
                    startTimer();
                    btnStopStart.setText("Hold to Save Activity");
                    btnStopStart.setBackgroundColor(getResources().getColor(R.color.red));
                } else if (isTrackingRun) {
                    // Pause run
                    isTrackingRun = false;
                    isPaused = true;
                    lastPauseStartTime = System.currentTimeMillis();
                    stopTimer();
                    btnStopStart.setText("Resume Run");
                    btnStopStart.setBackgroundColor(getResources().getColor(R.color.green));
                } else if (isPaused) {
                    // Resume run
                    isTrackingRun = true;
                    isPaused = false;
                    long pauseDuration = System.currentTimeMillis() - lastPauseStartTime;
                    totalPausedTime += pauseDuration;
                    pauseDurations.add(pauseDuration);
                    startTimer();
                    btnStopStart.setText("Hold To Save Activity");
                    btnStopStart.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
        });

        btnStopStart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isTrackingRun || isPaused) {
                    finishRun();
                    return true;
                }
                return false;
            }
        });

        startTrackingLocation();
    }

    private void showSaveDialog() {
        SaveActivityDialog saveActivityDialog = new SaveActivityDialog(this);
        long runTime = 0;

        for (long split : kmSplits) {
            runTime += split;
        }
        int activityTime = (int) (runTime / 1000);

        Bundle runningData = new Bundle();
        runningData.putDouble("distance", distanceTravelled);
        runningData.putDouble("time", activityTime);
        runningData.putString("type", getIntent().getStringExtra("Type"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ActivityLocationsEntity> entitiesList = activityLocationsDao.retrieveAllLocations();
                List<LatLng> locations = new ArrayList<>();
                for (ActivityLocationsEntity entity : entitiesList) {
                    locations.add(new LatLng(entity.getLatitude(), entity.getLongitude()));
                }
                runningData.putParcelableArrayList("activityLocations", (ArrayList<? extends Parcelable>) locations);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long[] splits = new long[kmSplits.size()];
                        for (int i = 0; i < kmSplits.size(); i++) {
                            splits[i] = kmSplits.get(i);
                        }
                        runningData.putLongArray("splits", splits);
                        saveActivityDialog.setArguments(runningData);
                        saveActivityDialog.setListener(new SaveActivityDialog.SaveActivityDialogListener() {
                            @Override
                            public void onSaveConfirmed() {

                            }

                            @Override
                            public void onSaveCancelled() {
                                resumeRunAfterDialog();
                            }
                        });
                        saveActivityDialog.show(getSupportFragmentManager(), "SaveActivity");
                    }
                });
            }
        }).start();
    }

    public void finishRun() {
        isRunEnding = true;
        isTrackingRun = false;
        isPaused = false;
        lastPauseStartTime = System.currentTimeMillis();
        stopTimer();

        Location nowLocation = new Location("nowLocation");
        nowLocation.setLatitude(currentLocation.latitude);
        nowLocation.setLongitude(currentLocation.longitude);
        kmSplit(previousLocation, nowLocation, true);
    }

    private void startTimer() {
        isTimerStarted = true;
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }
        runTimeHandler.post(timer);
    }

    private void stopTimer() {
        isTimerStarted = false;
        runTimeHandler.removeCallbacks(timer);
        timeElapsed = System.currentTimeMillis() - startTime - totalPausedTime;
    }

    public void startTrackingLocation() {
        Intent locationTracker = new Intent(MainActivity.this, LocationTracker.class);
        startForegroundService(locationTracker);
        LocationUpdatesBroadcastReceiver receiver = new LocationUpdatesBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("com.example.broadcast.LOCATION_UPDATE");
        registerReceiver(receiver, intentFilter);
    }

    private void updateMapWithLocation(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        Location lastKnownLocation = previousLocation;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        if (isTrackingRun) {
            if (!isTimerStarted) {
                DistanceTalker.speak("Activity Started", TextToSpeech.QUEUE_FLUSH, null);
            }
            DrawRoute(location);
            DistanceSpeakerAssistant(distanceTravelled);
            kmSplit(lastKnownLocation, location, false);
        } else {
            stopTimer();
        }
    }

    private void DrawRoute(Location location) {
        if (previousLocation == null) {
            System.out.println("PreviousLocation is null");
        }
        if (previousLocation != null) {
            double distanceTravelledInterval = previousLocation.distanceTo(location);
            distanceTravelled += distanceTravelledInterval;
            String.valueOf(distanceTravelled);
            txtDistanceTravelled.setText(String.format("%.2f", distanceTravelled / 1000) + " KM");
            LatLng prevLocation = new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude());
            googleMap.addPolyline(new PolylineOptions()
                    .add(prevLocation, currentLocation)
                    .clickable(true)
                    .width(12)
                    .color(Color.RED));
        }
    }

    private void kmSplit(Location prevLocation, Location currLocation, boolean isFinalSplit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (prevLocation == null || currLocation == null) {
                    return;
                }

                double distanceInterval = prevLocation.distanceTo(currLocation);
                if (!isFinalSplit && distanceInterval == 0) {
                    return;
                }

                long currTime = System.currentTimeMillis();
                long effectiveTime = currTime - totalPausedTime;

                synchronized (kmSplits) {
                    while (distanceTravelled >= (kmSplits.size() + 1) * 1000) {
                        double nextSplitDistance = (kmSplits.size() + 1) * 1000;
                        if (distanceTravelled < nextSplitDistance) break;

                        double fraction = (nextSplitDistance - (distanceTravelled - distanceInterval)) / distanceInterval;
                        fraction = Math.max(0, Math.min(1, fraction));
                        long interpolatedEffectiveTime = previousEffectiveTime + Math.round(fraction * (effectiveTime - previousEffectiveTime));

                        if (kmSplits.isEmpty()) {
                            kmSplits.add(interpolatedEffectiveTime - (startTime - totalPausedTime));
                        } else {
                            long sumPreviousSplits = kmSplits.stream().mapToLong(Long::longValue).sum();
                            kmSplits.add(interpolatedEffectiveTime - (startTime - totalPausedTime + sumPreviousSplits));
                        }

                        int currentKm = kmSplits.size();
                        long latestSplitTimeMillis = kmSplits.get(kmSplits.size() - 1);
                        String formattedSplitTime = formatNotificationTime(latestSplitTimeMillis);
                    }

                    if (isFinalSplit) {
                        long remainingDistance = (long) (distanceTravelled % 1000);
                        if (remainingDistance > 0) {
                            long sumSplits = kmSplits.stream().mapToLong(Long::longValue).sum();
                            long effectiveElapsedTime = (currTime - startTime - totalPausedTime);
                            long finalSplit = effectiveElapsedTime - sumSplits;
                            kmSplits.add(finalSplit);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showSaveDialog();
                            }
                        });
                    }
                }
                previousEffectiveTime = effectiveTime;
                previousTime = currTime;
            }
        }).start();
    }

    private String formatRunTime(int timePassed) {
        int hours = timePassed / 3600;
        int minutes = (timePassed % 3600) / 60;
        int seconds = timePassed % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void resumeRunAfterDialog() {
        if (lastPauseStartTime > 0) {
            long pauseDuration = System.currentTimeMillis() - lastPauseStartTime;
            totalPausedTime += pauseDuration;
            pauseDurations.add(pauseDuration);
        }

        isRunEnding = false;
        isTrackingRun = true;
        isPaused = false;
        lastPauseStartTime = 0;

        startTimer();

        Button btnStopStart = findViewById(R.id.stopStartBtn);
        btnStopStart.setText("Hold to Save Activity");
        btnStopStart.setBackgroundColor(getResources().getColor(R.color.red));
    }


    private void DistanceSpeakerAssistant(double DistanceTravelled) {
        while (distanceTravelled >= milestoneTarget) {
            DistanceTalker.speak(milestoneTarget + " metres done", TextToSpeech.QUEUE_FLUSH, null);
            milestoneTarget += 1000;
        }
    }

    private String formatNotificationTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng initialLocation = new LatLng(54.597, -5.930);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 12));
    }

    @Override
    public void onBackPressed() {
        if (isTrackingRun) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                Log.d("MainActivity", "Location permissions denied");
            }
        }
    }

    public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location newLocation = intent.getParcelableExtra("location");
            float currentSpeed = intent.getFloatExtra("speed", 0f);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ActivityLocationsEntity entity = new ActivityLocationsEntity();
                    entity.setLatitude(newLocation.getLatitude());
                    entity.setLongitude(newLocation.getLongitude());
                    entity.setTime(System.currentTimeMillis());
                    if (isTrackingRun) {
                        activityLocationsDao.addLocation(entity);
                    }
                }
            }).start();
            updateMapWithLocation(newLocation);
            previousLocation = newLocation;
            if (isTrackingRun) {
                locationUpdateCount++;
                if (locationUpdateCount >= 5) {
                    speedView.setText(ConversionUtil.convertToMinutesPerKM(currentSpeed));
                    locationUpdateCount = 0;
                }
            } else {
                speedView.setText("0:00 /KM");
            }
        }
    }
}