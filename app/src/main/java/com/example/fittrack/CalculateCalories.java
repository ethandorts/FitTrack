package com.example.fittrack;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CalculateCalories {
    public CalculateCalories() {

    }

    public void generateRequest(String activity, double weight, int duration ) {

        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //String api_key;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "https://api.api-ninjas.com/v1/caloriesburned?activity=skiing",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            System.out.println(jsonObject);
                            double caloriesBurned = jsonObject.getDouble("calories");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("Error in getting data");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headersMap = new HashMap<>();
                //headersMap.put("X-Api-Key", API_KEY);

                return headersMap;
            }
        };
        //requestQueue.add(request);
    }
}
