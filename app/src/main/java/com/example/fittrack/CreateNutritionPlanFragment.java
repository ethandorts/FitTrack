package com.example.fittrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNutritionPlanFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private String lastMessage;
    private TextView txtResponse;
    private ImageButton btnSendRequest;
    private EditText editMessage;
    private ProgressBar progressBarNP;
    private GoalsUtil goalsUtil = new GoalsUtil(db);
    private String goalsList = " ";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_nutrition_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtResponse = view.findViewById(R.id.txtAIResponseNP);
        btnSendRequest = view.findViewById(R.id.btnSendRequestNP);
        editMessage = view.findViewById(R.id.editModifyNP);
        progressBarNP = view.findViewById(R.id.progressBarNP);

        editMessage.setVisibility(View.GONE);
        btnSendRequest.setVisibility(View.GONE);

                AskFitTrackCoachingAssistant("Create me a nutrition plan for tomorrow. I have a goal to eat" + getDailyCalorieGoal() + " calories a day. " +
                        "I weigh" + getUserWeight() + "kg and I am " + getUserHeight() +"cm tall." +
                        " Provide me with a nutritional plan.\n" +
                        "\n My current fitness goal is " + getFitnessGoal() +
                        ". Based on the information provided above, provide a nutritional plan for me to meet my calorie goal and that would help reach my fitness goal provided." +
                        "Refer to my fitness goal and calorie count when you describe your reasoning for selection in Nutrient Benefits section. " +
                        "Make sure the total calories of the food strictly add up to the provided calorie goal. " +
                        "Provide your response in this format, for example:\n" +
                        "\n" +
                        "Breakfast \n" +
                        "Food: Rice Crispies, Toast (add grams)\n" +
                        "Total Calorie Count: 523 calories \n" +
                        "Nutrient Benefits: High carbs for running activities." +
                        "\n" +
                        "Lunch\n" +
                        "Food: Ham Sandwich \n" +
                        "Total Calorie Count: 523 calories \n" +
                        "Nutrient Benefits: High carbs for running activities." +
                        "\n" +
                        "Dinner\n" +
                        "Food: Fish, Chips\n"+
                        "Total Calorie Count: 523 calories \n" +
                        "Nutrient Benefits: High carbs for running activities.");

                if(txtResponse.getText().length() > 0) {
                    editMessage.setVisibility(View.VISIBLE);
                    btnSendRequest.setVisibility(View.VISIBLE);
                }

                btnSendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String newQuestion = editMessage.getText().toString();
                        AskFitTrackCoachingAssistant("This was your last response: " + lastMessage +
                                ". " + "\n" +
                                "Use this information to respond to the question below. " + "\n" +
                                newQuestion + " \n" +
                                "If a modified nutrition plan is necessary, display a nutrition plan in this format, for example:\n" +
                                "\n" +
                                "Breakfast \n" +
                                "Food: Rice Crispies, Toast (add grams)\n" +
                                "\n" +
                                "Lunch\n" +
                                "Food: Ham Sandwich \n" +
                                "\n" +
                                "Dinner\n" +
                                "Food: Fish, Chips\n");
                        editMessage.setText("");
                    }
                });
    }

    public void AskFitTrackCoachingAssistant(String question) {
        JSONObject body = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        txtResponse.setText("");
        progressBarNP.setVisibility(View.VISIBLE);

        try {
            JSONObject aiDescription = new JSONObject();
            aiDescription.put("role", "system");
            aiDescription.put("content", "You are an AI Fitness Coaching Assistant that specialises in creating personalised nutrition plans tailored to individual needs.");
            messagesArray.put(aiDescription);
            if (lastMessage != null && !lastMessage.isEmpty()) {
                JSONObject previousMessage = new JSONObject();
                previousMessage.put("role", "assistant");
                previousMessage.put("content", lastMessage);
                messagesArray.put(previousMessage);
            }
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", question);
            messagesArray.put(userMessage);

            body.put("model", "gpt-3.5-turbo");
            body.put("messages", messagesArray);

        } catch (JSONException e) {
            System.out.println("Error creating request body: " + e);
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://api.openai.com/v1/chat/completions",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray choicesArray = jsonObject.getJSONArray("choices");
                            JSONObject choiceObject = choicesArray.getJSONObject(0);
                            JSONObject messageObject = choiceObject.getJSONObject("message");

                            String message = messageObject.getString("content");
                            String cleanMessage = ConversionUtil.cleanAIResponse(message);
                            String formattedMessage = cleanMessage
                                    .replaceAll("(?m)^(Breakfast|Lunch|Dinner)\\b", "🍽️ <b><u>$1</u></b>")
                                    .replaceAll("(?m)^Food:\\s*(.*)", "🍴 <b>Food:</b> <i>$1</i>")
                                    .replaceAll("(?m)^Total Calorie Count:\\s*(.*)", "🔥 <b>Calories:</b> <i>$1</i>")
                                    .replaceAll("(?m)^Nutrient Benefits:\\s*(.*)", "💪 <b>Benefits:</b> <i>$1</i>")
                                    .replaceAll("\\n", "<br>")
                                    .replaceAll("(?m)(<br>🍽️)", "<hr>$1");

                            txtResponse.setText(Html.fromHtml(formattedMessage, Html.FROM_HTML_MODE_COMPACT));
                            lastMessage = message;

                            progressBarNP.setVisibility(View.GONE);
                            editMessage.setVisibility(View.VISIBLE);
                            btnSendRequest.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("AI failure: " + error + " " + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headersMap = new HashMap<>();
                headersMap.put("Content-Type", "application/json");
                headersMap.put("Authorization", "Bearer ");
                return headersMap;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return new DefaultRetryPolicy(60000, 3, 1);
            }
        };
        requestQueue.add(request);
    }


    private long getUserWeight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("Weight", 0);
    }

    private long getUserHeight() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("Height", 0);
    }

    private long getDailyCalorieGoal() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("DailyCalorieGoal", 0);
    }

    private String getFitnessGoal() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPI", Context.MODE_PRIVATE);
        return sharedPreferences.getString("FitnessGoal", " ");
    }
}
