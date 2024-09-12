package com.example.fittrack;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.stats.WakeLock;

import java.util.ArrayList;

public class LocationTracker extends Service {

    private static final String CHANNEL_ID = "LOCATION_TRACKER_NOTIFICATIONS";
    private FusedLocationProviderClient fusedLocationClient;
    //private PendingIntent retrieveLocationPendingIntent;
    private Location previousLocation;
    private PowerManager.WakeLock wakelock;
    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Intent intent = new Intent(this, MainActivity.LocationUpdatesBroadcastReceiver.class);
        //retrieveLocationPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        getLocationUpdates();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FitTrack::LocationWakeLock");
        wakelock.acquire();
        System.out.println("WakeLock: " + wakelock);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForegroundService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakelock != null && wakelock.isHeld()) {
            wakelock.release();
        }
        stopLocationUpdates();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel trackerChannel = new NotificationChannel(CHANNEL_ID, "LOCATION_TRACKER", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(trackerChannel);
        }
    }

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setFastestInterval(200);
    }

    private void getLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Location permissions were not granted");
            return;
        }
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
    }

    public void startForegroundService() {
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Tracking running activity")
                .setSmallIcon(R.drawable.baseline_settings_24)
                .build();

        startForeground(1, notification);
    }

    private void stopLocationUpdates() {
        //fusedLocationClient.removeLocationUpdates(retrieveLocationPendingIntent);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            Location prevLocation = null;
            for (Location location : locationResult.getLocations()) {
                previousLocation = location;
                LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Intent passGeoData = new Intent();
                passGeoData.setAction("com.example.broadcast.LOCATION_UPDATE");
                passGeoData.putExtra("location", location);
                sendBroadcast(passGeoData);
            }
        }
    };
}
