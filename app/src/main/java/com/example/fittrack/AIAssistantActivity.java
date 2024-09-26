package com.example.fittrack;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.logging.type.HttpRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AIAssistantActivity extends AppCompatActivity {
    private TextView txtAI;
    private EditText editTextAskAI;
    private ImageButton btnAskAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aiassistant);

        txtAI = findViewById(R.id.txt_ai_response);
        editTextAskAI = findViewById(R.id.editAskAI);
        btnAskAI = findViewById(R.id.imageButtonSendRequest);

        btnAskAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = editTextAskAI.getText().toString();
                AskFitTrackCoachingAssistant(question);
                editTextAskAI.setText("");
            }
        });

    }

    public void AskFitTrackCoachingAssistant(String question) {
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        try {
            body.put("model", "gpt-3.5-turbo");
            JSONObject messages = new JSONObject();
            messages.put("role", "user");
            messages.put("content", question);
            messagesArray.put(messages);
            body.put("messages", messagesArray);
        } catch(JSONException e) {
            System.out.println("Error creating request body: " + e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //String api_key - API key here
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.openai.com/v1/chat/completions",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            System.out.println(jsonObject);
                            JSONArray array = jsonObject.getJSONArray("choices");
                            JSONObject object = (JSONObject) array.get(0);
                            JSONObject nextObject = (JSONObject) object.get("message");
                            String message = nextObject.getString("content");
                            txtAI.setText(message);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println("AI failure: " + volleyError + " " + volleyError.getMessage() + " ");
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("Content-Type", "application/json");
                headersMap.put("Authorization", "Bearer "); // add API key here

                return headersMap;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(60000, 3, 1);
            }
        };
        requestQueue.add(request);
    }
}