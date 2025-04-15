package com.example.fittrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonalRecordRecyclerAdapter extends RecyclerView.Adapter<PersonalRecordRecyclerAdapter.PBViewHolder> {

    private final List<PersonalRecord> recordList;

    public PersonalRecordRecyclerAdapter(List<PersonalRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public PBViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.personal_record_item, parent, false);
        return new PBViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PBViewHolder holder, int position) {
        PersonalRecord record = recordList.get(position);
        holder.txtTitle.setText(record.getTitle());
        holder.txtTime.setText(record.getTime());
        holder.txtDate.setText(record.getDate());
        holder.imgIcon.setImageResource(record.getIcon());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class PBViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtTime, txtDate;

        public PBViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.imgActivityIcon);
            txtTitle = itemView.findViewById(R.id.txtRecordTitle);
            txtTime = itemView.findViewById(R.id.txtRecordTime);
            txtDate = itemView.findViewById(R.id.txtRecordDate);
        }
    }
}
