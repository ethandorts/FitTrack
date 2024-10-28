package com.example.fittrack;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ActivityLocationsEntity.class}, version = 2)
public abstract class ActivityLocationsDatabase extends RoomDatabase {
    private static final String DatabaseName = "ActivityLocations";
    private static ActivityLocationsDatabase activityLocationsDatabase;

    public static synchronized ActivityLocationsDatabase getActivityLocationsDatabase(Context context) {
        if(activityLocationsDatabase == null) {
            activityLocationsDatabase = Room.databaseBuilder(context.getApplicationContext(), ActivityLocationsDatabase.class , DatabaseName)
                    .fallbackToDestructiveMigration().build();
        }
        return activityLocationsDatabase;
    }
    public abstract ActivityLocationsDao activityLocationsDao();
}
