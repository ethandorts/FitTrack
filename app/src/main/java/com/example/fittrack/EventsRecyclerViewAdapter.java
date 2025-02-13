package com.example.fittrack;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
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
    private String eventID;
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
        eventHolder.eventEventName.setText(event.getEventName());
        SpannableStringBuilder descriptionBuilder = new SpannableStringBuilder();
        descriptionBuilder.append("Activity Description: ");
        descriptionBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, descriptionBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        descriptionBuilder.append("\n").append(event.getDescription());
        eventHolder.eventDescription.setText(descriptionBuilder);
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
        ImageButton btnEdit;
        public EventHolder (@NonNull View itemView) {
            super(itemView);
            eventLogo = itemView.findViewById(R.id.eventLogo);
            eventActivityType = itemView.findViewById(R.id.eventActivityType);
            eventDateTime = itemView.findViewById(R.id.eventDateTime);
            eventEventName = itemView.findViewById(R.id.eventEventName);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            btnEdit = itemView.findViewById(R.id.btnEventOptions);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventID = "";
                    Context context = view.getContext();
                    int adapterPosition = getAdapterPosition();
                    
                    if(adapterPosition != RecyclerView.NO_POSITION) {
                        String eventId = ((EventsRecyclerViewAdapter) getBindingAdapter())
                                .getSnapshots()
                                .getSnapshot(adapterPosition)
                                .getId();

                        EventModel eventModel = ((EventsRecyclerViewAdapter) getBindingAdapter())
                                .getSnapshots()
                                .get(adapterPosition);

                        String eventName = eventModel.getEventName();
                        String description = eventModel.getDescription();
                        String activityType = eventModel.getActivityType();
                        String dateTime = eventModel.getDateTime();

                        Intent intent = new Intent(context, EditFitnessEvent.class);
                        intent.putExtra("EventID", eventId);
                        intent.putExtra("EventName", eventName);
                        intent.putExtra("Description", description);
                        intent.putExtra("ActivityType", activityType);
                        intent.putExtra("DateTime", dateTime);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}