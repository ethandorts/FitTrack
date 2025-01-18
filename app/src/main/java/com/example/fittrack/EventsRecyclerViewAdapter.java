package com.example.fittrack;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventsRecyclerViewAdapter extends FirestoreRecyclerAdapter<EventModel, EventsRecyclerViewAdapter.EventHolder> {
    private Context context;
    private String UserID;
    public EventsRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<EventModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder eventHolder, int i, @NonNull EventModel event) {
        eventHolder.eventLogo.setImageResource(R.drawable.running);
        eventHolder.eventActivityType.setText(event.getActivityType());
        String date = event.getDateTime();
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat transformedDate = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date parseDate = input.parse(date);
            String formattedDate = transformedDate.format(parseDate);
            eventHolder.eventDateTime.setText(formattedDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        eventHolder.eventEventName.setText(event.getDescription());
        SpannableStringBuilder descriptionBuilder = new SpannableStringBuilder();
        descriptionBuilder.append("Activity Description: ");
        descriptionBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, descriptionBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionBuilder.append("\n").append(event.getEventName());
        eventHolder.eventDescription.setText(descriptionBuilder);
        SpannableStringBuilder completedBuilder = new SpannableStringBuilder();
        completedBuilder.append("Completed: ");
        completedBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, completedBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (event.getCompleted() != null) {
            completedBuilder.append(event.getCompleted());
            completedBuilder.setSpan(new ForegroundColorSpan(Color.RED), completedBuilder.length() - event.getCompleted().length(), completedBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            completedBuilder.append("Status Not Found");
        }
        eventHolder.eventCompleted.setText(completedBuilder);
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendar_event_row, parent,false);
        return new EventsRecyclerViewAdapter.EventHolder(view);
    }

    public static class EventHolder extends RecyclerView.ViewHolder {
        TextView eventActivityType, eventDateTime, eventEventName, eventDescription, eventCompleted;
        ImageView eventLogo;
        public EventHolder (@NonNull View itemView) {
            super(itemView);
            eventLogo = itemView.findViewById(R.id.eventLogo);
            eventActivityType = itemView.findViewById(R.id.eventActivityType);
            eventDateTime = itemView.findViewById(R.id.eventDateTime);
            eventEventName = itemView.findViewById(R.id.eventEventName);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            eventCompleted = itemView.findViewById(R.id.txtCompleted);
        }
    }
}