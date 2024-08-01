package com.example.fittrack;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;
    private Location previousLocation;
    private double distanceTravelled;
    private TextView txtDistanceTravelled;
    private TextView txtRunTime;
    private Button btnStartRun;
    private Button btnStopRun;

    private LatLng currentLocation;
    private boolean isTrackingRun;
    private boolean isTimerStarted;
    private Handler runTimeHandler;
    private Runnable timer;
    private long startTime;
    private int elapsedTime;
    private double milestoneTarget = 100;
    private List<LatLng> activityLocations = new ArrayList<>();
    private TextToSpeech DistanceTalker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtDistanceTravelled = findViewById(R.id.txtDistanceTravelled);
        Button btnClear = findViewById(R.id.btnClearMap);
        Button btnStartRun = findViewById(R.id.btnStart);
        Button btnStopRun = findViewById(R.id.btnStop);
        txtRunTime = findViewById(R.id.txtRunTime);


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

        txtDistanceTravelled.setText(String.format("%.2f metres", distanceTravelled));

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleMap.clear();
                distanceTravelled = 0.00;
                Toast.makeText(MainActivity.this, "Map and Distance Travelled Reset", Toast.LENGTH_SHORT);
            }
        });

        btnStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTrackingRun = true;
            }
        });

        btnStopRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTrackingRun = false;
                System.out.println(activityLocations);
                SaveActivityDialog saveActivityDialog = new SaveActivityDialog();

                Bundle runningData = new Bundle();
                runningData.putDouble("distance", distanceTravelled);
                runningData.putDouble("time", elapsedTime);
                runningData.putParcelableArrayList("activityLocations", (ArrayList<? extends Parcelable>) activityLocations);

                saveActivityDialog.setArguments(runningData);
                saveActivityDialog.show(getSupportFragmentManager(), "SaveActivity");

            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getUserLocation();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationUpdates();
        }
    }

    // Method to receive ongoing updates of device's location.
    private void getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Location permissions were not granted");
            return;
        }

        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult == null) {
                            return;
                        }
                        Location prevLocation = null;
                        for (Location location : locationResult.getLocations()) {
                            updateMapWithLocation(location);
                            previousLocation = location;

                            if(isTrackingRun) {
                                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                activityLocations.add(newLocation);
                            }
                        }
                    }
                },
                null );
    }

   private void startTimer() {
       isTimerStarted = true;
       startTime = System.currentTimeMillis();
       runTimeHandler.post(timer);
   }

    private void stopTimer() {
        isTimerStarted = false;
        runTimeHandler.removeCallbacks(timer);
        elapsedTime = (int) (System.currentTimeMillis() - startTime);
    }

    // Specifies interval of location updates.
    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3000)
                .setFastestInterval(1000);
    }

    // Updates Map with latest location found.
    private void updateMapWithLocation(Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        if(isTrackingRun) {
            if (!isTimerStarted) {
                startTimer();
                DistanceTalker.speak("Activity Started", TextToSpeech.QUEUE_FLUSH, null);
            }
            DrawRoute(location);
            DistanceSpeakerAssistant(distanceTravelled);
        } else {
            stopTimer();
        }
    }

    private void DrawRoute(Location location) {
        if(previousLocation != null) {
            double distanceTravelledInterval = previousLocation.distanceTo(location);
            distanceTravelled += distanceTravelledInterval;
            String.valueOf(distanceTravelled);
            txtDistanceTravelled.setText(String.format("%.2f meters", distanceTravelled));
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

    // Requests appropriate location permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdates();
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
            milestoneTarget += 100;
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
}
