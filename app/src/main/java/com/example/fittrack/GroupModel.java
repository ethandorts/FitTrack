package com.example.fittrack;

import android.widget.ImageView;

public class GroupModel {
    private String GroupID;
    private String GroupName;
    private String GroupDescription;
    private ImageView GroupIcon;

    public GroupModel(String groupID, String groupName, String groupDescription, ImageView groupIcon) {
        GroupID = groupID;
        GroupName = groupName;
        GroupDescription = groupDescription;
        GroupIcon = groupIcon;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getGroupDescription() {
        return GroupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        GroupDescription = groupDescription;
    }

    public ImageView getGroupIcon() {
        return GroupIcon;
    }

    public void setGroupIcon(ImageView groupIcon) {
        GroupIcon = groupIcon;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }
}
