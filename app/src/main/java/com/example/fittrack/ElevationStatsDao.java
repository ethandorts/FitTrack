package com.example.fittrack;

import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface ElevationStatsDao {
    @Insert
    void addElevation(ElevationStatsEntity entity);

    @Query("SELECT * FROM ElevationStats")
    List<ElevationStatsEntity> retrieveAllElevationStats();

    @Query("SELECT AVG(elevation) FROM ElevationStats")
    Double getAverageElevation();

    @Query("DELETE FROM ElevationStats")
    void deleteAllElevationStats();
}
