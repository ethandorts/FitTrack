package com.example.fittrack;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActivitiesRecyclerViewAdapter extends RecyclerView.Adapter<ActivitiesRecyclerViewAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<ActivityModel> userActivities;

    public ActivitiesRecyclerViewAdapter(Context context, ArrayList<ActivityModel> userActivities) {
        this.context = context;
        this.userActivities = userActivities;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_stats_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ActivityModel activity = userActivities.get(position);

        holder.activityType.setText(activity.getActivityType());
        holder.activityDate.setText(activity.getActivityDate());
        holder.activityDistance.setText(activity.getActivityDistance() + " km");
        holder.activityTime.setText(activity.getActivityTime());
        holder.activityPace.setText(activity.getActivityPace() + " km/h");
        holder.activityUser.setText(activity.getActivityUser());
        holder.activityMapContainer.onCreate(null);
        holder.activityMapContainer.onResume();
        holder.activityMapContainer.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                List<Object> geoData = activity.getActivityCoordinates();
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
                    googleMap.addPolyline(polylineOptions);
                    //find the middle point of the route
                    int middlePoint = geoData.size() / 2;
                    Map<String, Double> middleData = (Map<String, Double>) geoData.get(middlePoint);
                    LatLng middleLatLng = new LatLng(middleData.get("latitude"), middleData.get("longitude"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatLng, 13));
                } else {
                    System.out.println("GeoData object is null");
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return userActivities.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        TextView activityType, activityDate, activityDistance, activityTime, activityPace, activityUser;
        ImageView activityTypeImage, activityUserImage;
        MapView activityMapContainer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            activityType = itemView.findViewById(R.id.txtRunningActivityTitle);
            activityDate = itemView.findViewById(R.id.txtActivityDateValue);
            activityDistance = itemView.findViewById(R.id.txtDistanceValue);
            activityTime = itemView.findViewById(R.id.txtTimeValue);
            activityPace = itemView.findViewById(R.id.txtPaceValue);
            activityUser = itemView.findViewById(R.id.txtUserNameValue);
            activityTypeImage = itemView.findViewById(R.id.running_icon);
            activityUserImage = itemView.findViewById(R.id.profileImage);
            activityMapContainer = itemView.findViewById(R.id.mapView);
        }

        void onMapViewResume() {
            if (activityMapContainer != null) {
                activityMapContainer.onResume();
            }
        }
        void onMapViewPause() {
            if (activityMapContainer != null) {
                activityMapContainer.onPause();
            }
        }

        void onMapViewDestroy() {
            if (activityMapContainer != null) {
                activityMapContainer.onDestroy();
            }
        }

        void onMapViewLowMemory() {
            if (activityMapContainer != null) {
                activityMapContainer.onLowMemory();
            }
        }
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

        }
    }
}
