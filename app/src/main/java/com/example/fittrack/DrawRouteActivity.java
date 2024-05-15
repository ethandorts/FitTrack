package com.example.fittrack;

import android.graphics.Color;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class DrawRouteActivity extends AppCompatActivity
 implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_route);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Polyline PolylineExample = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(new LatLng(54.9953, -7.3229))  // Magee Campus
                .add(new LatLng(54.9804, -7.3084))  // Ebrington Square
                .add(new LatLng(54.9929, -7.2570))  // St. Columb's Park
                .add(new LatLng(54.9818, -7.3127))  // Peace Bridge
                .add(new LatLng(54.9920, -7.3179))  // Guildhall
                .add(new LatLng(55.0001, -7.3221))  // St. Eugene's Cathedral
                .color(Color.BLUE));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.9966, -7.3085), 4));

        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnPolygonClickListener(this);
    }

    @Override
    public void onPolylineClick (Polyline polyline) {

    }

    @Override
    public void onPolygonClick(Polygon polygon) {

    }
}