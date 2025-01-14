package com.example.fittrack;

import android.widget.ImageView;

import java.util.ArrayList;

public class GroupModel {
    private String GroupID;
    private String GroupName;
    private String shortDescription;
    private String GroupDescription;
    private String Activity;
    private String GroupLocation;
    private ArrayList<String> MembersNumber;
    private ImageView GroupIcon;

    public GroupModel(String groupID, String groupName, String shortDescription, String groupDescription, String groupLocation, ImageView groupIcon, ArrayList<String> members, String activity) {
        GroupID = groupID;
        GroupName = groupName;
        this.shortDescription = shortDescription;
        GroupDescription = groupDescription;
        GroupLocation = groupLocation;
        GroupIcon = groupIcon;
        this.MembersNumber = members;
        this.Activity = activity;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getGroupDescription() {
        return GroupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        GroupDescription = groupDescription;
    }

    public String getGroupLocation() {
        return GroupLocation;
    }

    public void setGroupLocation(String groupLocation) {
        GroupLocation = groupLocation;
    }

    public ImageView getGroupIcon() {
        return GroupIcon;
    }

    public void setGroupIcon(ImageView groupIcon) {
        GroupIcon = groupIcon;
    }

    public ArrayList<String> getMembersNumber() {
        return MembersNumber;
    }

    public void setMembersNumber(ArrayList<String> membersNumber) {
        MembersNumber = membersNumber;
    }

    public String getActivity() {
        return Activity;
    }

    public void setActivity(String activity) {
        Activity = activity;
    }
}
