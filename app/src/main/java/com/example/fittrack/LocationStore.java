package com.example.fittrack;

import android.app.Application;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationStore extends Application {
    List<Location> locations = new ArrayList<>();

    public void addLocation(Location location) {
        locations.add(location);
    }

    public List<Location> getLocations() {
        return locations;
    }
}
