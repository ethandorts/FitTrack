package com.example.fittrack;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElevationUtil {
    Context context;

    public ElevationUtil(Context context) {
        this.context = context;
    }

    public void elevationRequest(List<LatLng> activityCoordinates, ElevationCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);

        int totalLocations = activityCoordinates.size();
        int desiredSize = 100;

        desiredSize = Math.min(desiredSize, totalLocations);

        int space = totalLocations / desiredSize;

        List<LatLng> minimisedLocations = new ArrayList<>();
        for(int i = 0; i < totalLocations; i += space) {
            minimisedLocations.add(activityCoordinates.get(i));
            if(minimisedLocations.size() == desiredSize) {
                break;
            }
        }

        StringBuilder locationsEndpoint = new StringBuilder();
        for(LatLng latLng: activityCoordinates) {
            if(locationsEndpoint.length() > 0) {
                locationsEndpoint.append("|");
            }
            locationsEndpoint.append(latLng.latitude).append(",").append(latLng.longitude);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://maps.googleapis.com/maps/api/elevation/json?locations=" + locationsEndpoint + "&key=AIzaSyBpAD_WhJNYjpzvB4gvp0z6CgWzFB8HEtA",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray elevationResults = jsonObject.getJSONArray("results");
                            double [] elevationValues = new double[elevationResults.length()];
                            for(int i =0; i < elevationResults.length(); i++) {
                                JSONObject result = elevationResults.getJSONObject(i);
                                elevationValues[i] = result.getDouble("elevation");
                            }
                            callback.onCallback(elevationValues);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Elevation Request Failure" + volleyError.getMessage());
                    }
                }
        );
        queue.add(request);
    }

    public interface ElevationCallback {
        void onCallback(double[] elevationValues);
    }
}
