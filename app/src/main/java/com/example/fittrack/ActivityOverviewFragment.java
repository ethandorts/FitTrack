package com.example.fittrack;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityOverviewFragment extends Fragment {
    private TextView txtDistance, txtPace, txtHeartRate, txtTime, txtDate, txtCalories;
    private MapView mapView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private String ActivityID;


    public ActivityOverviewFragment(String ActivityID) {
        this.ActivityID = ActivityID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtDistance = view.findViewById(R.id.txtActivityOverviewDistanceValue);
        txtPace = view.findViewById(R.id.txtActivityOverviewPaceValue);
        txtHeartRate = view.findViewById(R.id.txtActivityOverviewHeartRateValue);
        txtTime = view.findViewById(R.id.txtActivityOverviewTotalTime);
        txtDate = view.findViewById(R.id.txtOverviewActivityDate);
        txtCalories = view.findViewById(R.id.txtActivityOverviewCalories);
        mapView = view.findViewById(R.id.OverviewMapView);


        DatabaseUtil.retrieveSpecificActivity(ActivityID, new FirebaseDatabaseHelper.SpecificActivityCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                System.out.println(data);
                txtDistance.setText(String.valueOf(data.get("distance") + " M"));
                txtPace.setText(String.valueOf(data.get("pace") + "/KM"));
                Timestamp date = (Timestamp) data.get("date");
                System.out.println(date);
                txtTime.setText(String.valueOf(formatRunTime((Double) data.get("time"))));
                txtDate.setText(String.valueOf(dateFormatter((Timestamp) data.get("date"))));
                if (data.get("calories") != null) {
                    txtCalories.setText(String.valueOf(data.get("calories")));
                } else {
                    txtCalories.setText("-");
                }
                txtHeartRate.setText("-");
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        googleMap.getUiSettings().setScrollGesturesEnabled(false);
                        googleMap.getUiSettings().setZoomGesturesEnabled(false);
                        List<Object> geoData = (List<Object>) data.get("activityCoordinates");
                        if (geoData != null) {
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.color(Color.RED);
                            for (Object coordinates : geoData) {
                                Map<String, Double> activityRoutePoints = (Map<String, Double>) coordinates;
                                Double lat = activityRoutePoints.get("latitude");
                                Double lon = activityRoutePoints.get("longitude");
                                if (lat != null && lon != null) {
                                    LatLng geoPoint = new LatLng(lat, lon);
                                    polylineOptions.add(geoPoint);
                                }
                            }
                            if(!polylineOptions.getPoints().isEmpty()) {
                                googleMap.addPolyline(polylineOptions);
                                int middlePoint = geoData.size() / 2;
                                Map<String, Double> middleData = (Map<String, Double>) geoData.get(middlePoint);
                                LatLng middleLatLng = new LatLng(middleData.get("latitude"), middleData.get("longitude"));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatLng, 13));
                            } else {
                                Log.d("ActivitiesRecyclerViewAdapter", "No points to draw");
                            }
                        } else {
                            System.out.println("GeoData object is null");
                        }
                    }
                });
            }
        });
    }

    private String dateFormatter(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US);
        String formattedDate = formatter.format(date);

        return formattedDate;
    }

    private String formatRunTime(double timePassed) {
        int hours = (int) (timePassed / 3600);
        int minutes = (int) ((timePassed % 3600) / 60);
        int seconds = (int) (timePassed % 60);
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }
}
