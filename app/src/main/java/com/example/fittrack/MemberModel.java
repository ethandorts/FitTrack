package com.example.fittrack;

import android.net.Uri;
import android.widget.ImageView;

public class MemberModel {
    private String userName;
    private boolean isAdmin;
    public MemberModel(String userName, boolean isAdmin) {
        this.userName = userName;
        this.isAdmin = isAdmin;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
