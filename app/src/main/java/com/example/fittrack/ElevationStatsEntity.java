package com.example.fittrack;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="ElevationStats")

public class ElevationStatsEntity {
    @PrimaryKey(autoGenerate = true) Integer ElevationStatID;
    @ColumnInfo(name="elevation") Double elevation;

    public Integer getElevationStatID() {
        return ElevationStatID;
    }

    public void setElevationStatID(Integer elevationStatID) {
        ElevationStatID = elevationStatID;
    }

    public Double getElevation() {
        return elevation;
    }

    public void setElevation(Double elevation) {
        this.elevation = elevation;
    }
}
