package com.example.fittrack;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableDataListener extends WearableListenerService {
    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        for(DataEvent event : dataEventBuffer ) {
            if(event.getType() == DataEvent.TYPE_CHANGED
            && event.getDataItem().getUri().getPath().equals("/activity-data")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                System.out.println(dataMapItem);
            }
        }
    }
}
