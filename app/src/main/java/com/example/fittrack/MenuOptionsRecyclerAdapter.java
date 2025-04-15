package com.example.fittrack;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MenuOptionsRecyclerAdapter extends RecyclerView.Adapter<MenuOptionsRecyclerAdapter.MenuOptionHolder> {
    private Context context;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID = mAuth.getUid();
    private ArrayList<MenuOptionModel> optionsList = new ArrayList<>();

    public MenuOptionsRecyclerAdapter(Context context, ArrayList<MenuOptionModel> optionsList) {
        this.context = context;
        this.optionsList = optionsList;
    }

    @NonNull
    @Override
    public MenuOptionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_type_row, parent, false);
        return new MenuOptionsRecyclerAdapter.MenuOptionHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuOptionHolder holder, int position) {
        MenuOptionModel options = optionsList.get(position);

        holder.txtOptions.setText(options.getOptionTitle());
        holder.imgOptions.setImageResource(options.getOptionImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option = options.getOptionTitle();
                Intent intent;
                switch(option) {
                    case "My Fitness Goals":
                        intent = new Intent(view.getContext(), GoalSettingActivity.class);
                        context.startActivity(intent);
                        break;
                    case "My Fitness Activities":
                        intent = new Intent(view.getContext(), ActivitiesByMonthActivity.class);
                        intent.putExtra("UserID", UserID);
                        context.startActivity(intent);
                        break;
                    case "My Fitness Planner":
                        intent = new Intent(view.getContext(), Calendar.class);
                        context.startActivity(intent);
                        break;
                    case "AI Fitness Coach":
                        intent = new Intent(view.getContext(), AIAssistantActivity.class);
                        context.startActivity(intent);
                        break;
                    case "My Fitness Badges":
                        intent = new Intent(view.getContext(), MyBadgesActivity.class);
                        context.startActivity(intent);
                        break;
                    case "Personal Bests":
                        intent = new Intent(view.getContext(), PersonalRecordsActivity.class);
                        context.startActivity(intent);
                        break;
                    case "Profile Details":
                        intent = new Intent(view.getContext(), EditProfileActivity.class);
                        context.startActivity(intent);
                        break;
                    case "Nutrition":
                        intent = new Intent(view.getContext(), NutritionTrackingOverview.class);
                        context.startActivity(intent);
                        break;
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    public class MenuOptionHolder extends RecyclerView.ViewHolder {
        private TextView txtOptions;
        private ImageView imgOptions;

        public MenuOptionHolder(@NonNull View itemView) {
            super(itemView);
            txtOptions = itemView.findViewById(R.id.txtBadgeDescription);
            imgOptions = itemView.findViewById(R.id.imgBadge);
        }
    }
}
