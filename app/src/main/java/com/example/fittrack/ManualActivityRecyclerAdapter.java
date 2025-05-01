package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ManualActivityRecyclerAdapter extends RecyclerView.Adapter<ManualActivityRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<ActivityModel> manualActivities;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);

    public ManualActivityRecyclerAdapter(Context context, List<ActivityModel> manualActivities) {
        this.context = context;
        this.manualActivities = (manualActivities != null) ? manualActivities : new ArrayList<>();
    }

    @NonNull
    @Override
    public ManualActivityRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_stats_view_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManualActivityRecyclerAdapter.ViewHolder holder, int position) {
        ActivityModel activity = manualActivities.get(position);

        holder.activityType.setText(activity.getType());
        holder.activityDate.setText(formatDate(activity.getDate()));
        holder.activityDistance.setText(formatMetresToKM(activity.getDistance()) + " KM");
        holder.activityTime.setText(formatRunTime(activity.getTime()));
        holder.activityPace.setText(activity.getPace() + " /km");

        DatabaseUtil.retrieveChatName(activity.getUserID(), new FirebaseDatabaseHelper.ChatUserCallback() {
            @Override
            public void onCallback(String Chatname) {
                holder.activityUser.setText(Chatname);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OverviewFitnessStats.class);
                intent.putExtra("ActivityID", activity.getActivityID());
                context.startActivity(intent);
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
        return manualActivities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView activityType, activityDate, activityDistance, activityTime, activityPace, activityUser;
        private ImageView activityUserImage;
        private MapView activityMapContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityType = itemView.findViewById(R.id.txtRunningActivityTitle);
            activityDate = itemView.findViewById(R.id.txtActivityDateValue);
            activityDistance = itemView.findViewById(R.id.txtDistanceValue);
            activityTime = itemView.findViewById(R.id.txtTimeValue);
            activityPace = itemView.findViewById(R.id.txtPaceValue);
            activityUser = itemView.findViewById(R.id.txtUserNameValue);
            activityUserImage = itemView.findViewById(R.id.profileImage);
            activityMapContainer = itemView.findViewById(R.id.mapView);

            DatabaseUtil.retrieveProfilePicture(UserID + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
                @Override
                public void onCallback(Uri PicturePath) {
                    if (PicturePath != null) {
                        Glide.with(itemView.getContext())
                                .load(PicturePath)
                                .error(R.drawable.profile)
                                .into(activityUserImage);
                    } else {
                        Log.e("No profile picture found", "No profile picture found.");
                    }
                }
            });
        }
    }

    private String formatDate(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    private String formatRunTime(double timePassed) {
        int hours = (int) (timePassed / 3600);
        int minutes = (int) ((timePassed % 3600) / 60);
        int seconds = (int) (timePassed % 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private String formatMetresToKM(String distanceMetres) {
        double convertedString = Double.parseDouble(distanceMetres);
        double distanceKM = convertedString / 1000;
        return String.format("%.2f", distanceKM);
    }
}
