package com.example.fittrack;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

@Entity(tableName = "ActivityLocations")
public class ActivityLocationsEntity {
    @PrimaryKey(autoGenerate = true) Integer LocationID;
    @ColumnInfo(name = "latitude") double latitude;
    @ColumnInfo(name = "longitude") double longitude;
    @ColumnInfo(name = "time") long time;

    public Integer getLocationID() {
        return LocationID;
    }

    public void setLocationID(Integer locationID) {
        LocationID = locationID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
