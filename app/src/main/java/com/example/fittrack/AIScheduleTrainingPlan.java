package com.example.fittrack;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class AIScheduleTrainingPlan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aischedule_training_plan);

        TextView txtTrainingPlanAdvice = findViewById(R.id.txt_ai_response_training_plan);
        ImageButton btnSendRequest = findViewById(R.id.imageButtonSendRequestTrainingPlan);
        //AskFitTrackCoachingAssistant();
    }

    public void AskFitTrackCoachingAssistant(String question) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);


        JSONObject threadBody = new JSONObject();

        JsonObjectRequest threadRequest = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.openai.com/v1/threads",
                threadBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject threadResponse) {
                        try {
                            String threadId = threadResponse.getString("id");
                            //sendMessageToAssistant(threadId, question, requestQueue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Thread creation failed: " + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("Content-Type", "application/json");
                headersMap.put("Authorization", "Bearer YOUR_OPENAI_API_KEY");
                headersMap.put("OpenAI-Beta", "assistants=v2");
                return headersMap;
            }
        };

        requestQueue.add(threadRequest);
    }
}