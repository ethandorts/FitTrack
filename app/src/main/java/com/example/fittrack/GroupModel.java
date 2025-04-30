package com.example.fittrack;

import android.widget.ImageView;

import java.util.ArrayList;

public class GroupModel {
    private String GroupID;
    private String Name;
    private String shortDescription;
    private String Description;
    private String Activity;
    private String Location;
    private ArrayList<String> Runners;

    public GroupModel(String groupID, String groupName, String shortDescription, String groupDescription, String groupLocation, ArrayList<String> members, String activity) {
        GroupID = groupID;
        Name = groupName;
        this.shortDescription = shortDescription;
        Description = groupDescription;
        Location = groupLocation;
        this.Runners = members;
        this.Activity = activity;
    }

    public GroupModel() {

    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        Activity = activity;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public ArrayList<String> getRunners() {
        return Runners;
    }

    public void setRunners(ArrayList<String> runners) {
        Runners = runners;
    }
}
