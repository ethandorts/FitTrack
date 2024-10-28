package com.example.fittrack;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActivityLocationsDao {
    @Insert
    void addLocation(ActivityLocationsEntity activityLocation);

    @Query("SELECT * FROM ActivityLocations")
    List<ActivityLocationsEntity> retrieveAllLocations();

    @Query("DELETE FROM ActivityLocations")
    void deleteAllLocations();
}
