package com.example.fittrack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ActivitiesRecyclerViewAdapter extends FirestoreRecyclerAdapter<ActivityModel,ActivitiesRecyclerViewAdapter.MyViewHolder> {
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper userUtil = new FirebaseDatabaseHelper(db);
//    private ArrayList<ActivityModel> userActivities = new ArrayList<ActivityModel>();

    public ActivitiesRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<ActivityModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_stats_view_row, parent, false);
        return new MyViewHolder(view);
    }

//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        ActivityModel activity = userActivities.get(position);
//
//        holder.itemView.setTag(activity.getActivityID());
//        holder.activityType.setText(activity.getActivityType());
//        holder.activityDate.setText(dateFormatter(activity.getActivityDate()));
//        holder.activityDistance.setText(activity.getActivityDistance() + " m");
//        holder.activityTime.setText(activity.getActivityTime());
//        holder.activityPace.setText(activity.getActivityPace() + " /km");
//        holder.activityUser.setText(activity.getActivityUser());
//        holder.activityMapContainer.onCreate(null);
//        holder.activityMapContainer.onResume();
//        holder.activityMapContainer.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull GoogleMap googleMap) {
//                List<Object> geoData = activity.getActivityCoordinates();
//                if (geoData != null) {
//                    PolylineOptions polylineOptions = new PolylineOptions();
//                    polylineOptions.color(Color.RED);
//                    for (Object coordinates : geoData) {
//                        Map<String, Double> activityRoutePoints = (Map<String, Double>) coordinates;
//                        Double lat = activityRoutePoints.get("latitude");
//                        Double lon = activityRoutePoints.get("longitude");
//                        if (lat != null && lon != null) {
//                            LatLng geoPoint = new LatLng(lat, lon);
//                            polylineOptions.add(geoPoint);
//                        }
//                    }
//                    if(!polylineOptions.getPoints().isEmpty()) {
//                        googleMap.addPolyline(polylineOptions);
//                        int middlePoint = geoData.size() / 2;
//                        Map<String, Double> middleData = (Map<String, Double>) geoData.get(middlePoint);
//                        LatLng middleLatLng = new LatLng(middleData.get("latitude"), middleData.get("longitude"));
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(middleLatLng, 13));
//                    } else {
//                        Log.d("ActivitiesRecyclerViewAdapter", "No points to draw");
//                    }
//                    } else {
//                    System.out.println("GeoData object is null");
//                }
//            }
//        });
//    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int i, @NonNull ActivityModel activity) {
        holder.itemView.setTag(activity.getActivityID());
        holder.activityType.setText(activity.getType());
        holder.activityDate.setText(dateFormatter(activity.getDate()));
        holder.activityDistance.setText(formatMetresToKM(activity.getDistance()) + " KM");
        holder.activityTime.setText(formatRunTime(activity.getTime()));
        holder.activityPace.setText(activity.getPace() + " /km");
        userUtil.retrieveUserName(activity.getUserID(), new FirebaseDatabaseHelper.FirestoreUserNameCallback() {
            @Override
            public void onCallback(String FullName, long weight, long height, long activityFrequency, long dailyCalorieGoal) {
                holder.activityUser.setText(FullName);
            }
        });
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
                    holder.activityMapContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    private String dateFormatter(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = formatter.format(date);

        return formattedDate;
    }

//    public void updateAdapter(ArrayList<ActivityModel> activities) {
//        int originalSize = userActivities.size();
//        for(ActivityModel activity : activities) {
//            if(!userActivities.contains(activity)) {
//                userActivities.add(activity);
//            }
//        }
//        notifyItemRangeInserted(originalSize, userActivities.size());
//    }
//
//    public void showAdapter() {
//        for(ActivityModel activity : userActivities) {
//            System.out.println("Adapter Order:" + activity.getActivityID());
//        }
//    }

    private String formatRunTime(double timePassed) {
        int hours = (int) (timePassed / 3600);
        int minutes = (int) ((timePassed % 3600) / 60);
        int seconds = (int) (timePassed % 60);
        String formattedRunTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formattedRunTime;
    }

    private String formatMetresToKM(String distanceMetres) {
        double convertedString = Double.valueOf(distanceMetres);
        double distanceKM = convertedString / 1000;
        String formattedValue = String.format("%.2f", distanceKM);
        return formattedValue;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), OverviewFitnessStats.class);
                    intent.putExtra("ActivityID", String.valueOf(view.getTag()));
                    view.getContext().startActivity(intent);
                }
            });
        }
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

        }
    }
}