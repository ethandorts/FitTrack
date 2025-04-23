package com.example.fittrack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityOverviewFragment extends Fragment {
    private TextView txtTitle,txtDistance, txtPace, txtHeartRate, txtTime, txtDate, txtCalories, txtLikeStat, txtNoGeoData;
    private ImageButton btnLike, btnComments;
    private MapView mapView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private CommentUtil commentUtil = new CommentUtil(db);
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String currentUser = mAuth.getCurrentUser().getUid();
    private boolean isLiked = false;
    private String ActivityID;
    private String location;


    public ActivityOverviewFragment(String ActivityID) {
        this.ActivityID = ActivityID;
    }

    public ActivityOverviewFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        commentUtil.checkLike(ActivityID, currentUser, new CommentUtil.LikeCheckCallback() {
            @Override
            public void onCallback(boolean hasLiked) {
                if(hasLiked) {
                    isLiked = true;
                    btnLike.setImageResource(R.drawable.liked);
                } else {
                    isLiked = false;
                    btnLike.setImageResource(R.drawable.like);
                }
            }
        });

        commentUtil.retrieveLikeCount(ActivityID, new CommentUtil.LikeNumberCallback() {
            @Override
            public void onCallback(int likesNumber) {
                if(likesNumber == 0) {
                    txtLikeStat.setText("Be First To Like!");
                } else if(likesNumber == 1) {
                    txtLikeStat.setText(likesNumber + " Like");
                } else {
                    txtLikeStat.setText(likesNumber + " Likes");
                }

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commentUtil.checkLike(ActivityID, currentUser, new CommentUtil.LikeCheckCallback() {
            @Override
            public void onCallback(boolean hasLiked) {
                if(hasLiked) {
                    isLiked = true;
                    btnLike.setImageResource(R.drawable.liked);
                } else {
                    isLiked = false;
                    btnLike.setImageResource(R.drawable.like);
                }
            }
        });

        txtTitle = view.findViewById(R.id.txtActivityOverviewTitle);
        txtDistance = view.findViewById(R.id.txtActivityOverviewDistanceValue);
        txtPace = view.findViewById(R.id.txtActivityOverviewPaceValue);
        txtHeartRate = view.findViewById(R.id.txtActivityOverviewHeartRateValue);
        txtTime = view.findViewById(R.id.txtActivityOverviewTotalTime);
        txtDate = view.findViewById(R.id.txtOverviewActivityDate);
        txtCalories = view.findViewById(R.id.txtActivityOverviewCalories);
        txtNoGeoData = view.findViewById(R.id.txtNoGeoData);
        mapView = view.findViewById(R.id.OverviewMapView);
        btnLike = view.findViewById(R.id.btnActivityLike);
        btnComments = view.findViewById(R.id.btnActivityComments);
        txtLikeStat = view.findViewById(R.id.txtLikes);

        commentUtil.retrieveLikeCount(ActivityID, new CommentUtil.LikeNumberCallback() {
            @Override
            public void onCallback(int likesNumber) {
                if(likesNumber == 0) {
                    txtLikeStat.setText("Be First To Like!");
                } else if(likesNumber == 1) {
                    txtLikeStat.setText(likesNumber + " Like");
                } else {
                    txtLikeStat.setText(likesNumber + " Likes");
                }

            }
        });

        txtLikeStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LikesRecyclerActivity.class);
                intent.putExtra("ActivityID", ActivityID);
                startActivity(intent);
            }
        });

        if(isLiked) {
            btnLike.setImageResource(R.drawable.liked);
        }

        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("ActivityID", ActivityID);
                startActivity(intent);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLiked) {
                    commentUtil.removeLike(ActivityID, currentUser);
                    btnLike.setImageResource(R.drawable.like);
                    isLiked = false;
                } else {
                    commentUtil.addLike(ActivityID, currentUser);
                    btnLike.setImageResource(R.drawable.liked);
                    isLiked = true;
                }
            }
        });


        DatabaseUtil.retrieveSpecificActivity(ActivityID, new FirebaseDatabaseHelper.SpecificActivityCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                System.out.println(data);
                txtTitle.setText(String.valueOf(data.get("type")) + " Activity");
                double distanceInMeters = Double.parseDouble(data.get("distance").toString());
                double distanceInKm = distanceInMeters / 1000.0;
                String formattedDistance = String.format("%.2f KM", distanceInKm);
                txtDistance.setText(formattedDistance);
                txtPace.setText(String.valueOf(data.get("pace") + "/KM"));
                Timestamp date = (Timestamp) data.get("date");
                System.out.println(date);
                txtTime.setText(formatRunTime(getTimeAsDouble(data, "time")));
                txtDate.setText(String.valueOf(dateFormatter((Timestamp) data.get("date"))));
                if (data.get("caloriesBurned") != null) {
                    txtCalories.setText(String.valueOf(data.get("caloriesBurned")));
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
                            txtNoGeoData.setVisibility(View.VISIBLE);
                            mapView.setVisibility(View.GONE);
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

//    private void retrieveGeoLocation(LatLng location) {
//        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//        JsonObjectRequest teamRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.latitude + "," + location.longitude + "&result_type=locality&key=",
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//                        try {
//                            String location = " ";
//                            JSONArray geoArray = jsonObject.getJSONArray("results");
//                            for (int i = 0; i < geoArray.length(); i++) {
//                                JSONObject addressObject = geoArray.getJSONObject(i);
//                                JSONArray addressComponents = addressObject.getJSONArray("address_components");
//
//                                for (int x = 0; x < addressComponents.length(); x++) {
//                                    JSONObject addresssObject = addressComponents.getJSONObject(x);
//                                    JSONArray localityTypes = addresssObject.getJSONArray("types");
//
//                                    if (localityTypes.toString().contains("locality")) {
//                                        String geoLocation = addressObject.getString("long_name");
//                                        geoLocation = location;
//                                        System.out.println("Location" + location);
//                                        if(location.equals(" ")) {
//                                            txtTitle.setText("Running");
//                                        } else {
//                                            txtTitle.setText(location);
//                                        }
//                                    } else {
//                                        System.out.println("No locality");
//                                    }
//                                }
//                            }
//                        } catch (JSONException e) {
//                            Log.e("GeoLocation Error", "Failure to get locality geolocation: " + e);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        Log.e("Geolocation Error Response", "Could not retrieve geolocation: " + volleyError);
//                    }
//                }
//        );
//        requestQueue.add(teamRequest);
//    }

    private double getTimeAsDouble(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        }
        return 0;
    }


}
