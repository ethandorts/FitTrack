package com.example.fittrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class ProfileOverview extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseDatabaseHelper DatabaseUtil = new FirebaseDatabaseHelper(db);
    private TextView txtProfileName;
    private ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_overview);

        ImageView profilePicture = findViewById(R.id.profilePageImage);
        txtProfileName = findViewById(R.id.txtProfilePageName);


        Intent intent = getIntent();
        String UserID = intent.getStringExtra("UserID");

        DatabaseUtil.retrieveProfilePicture(UserID + ".jpeg", new FirebaseDatabaseHelper.ProfilePictureCallback() {
            @Override
            public void onCallback(Uri PicturePath) {
                Glide.with(getApplicationContext())
                        .load(PicturePath)
                        .circleCrop()
                        .into(profilePicture);
            }
        });

        DatabaseUtil.retrieveChatName(UserID, new FirebaseDatabaseHelper.ChatUserCallback() {
            @Override
            public void onCallback(String Chatname) {
                txtProfileName.setText(Chatname);
            }
        });



        TabLayout tabs = findViewById(R.id.tabProfile);
        pager = findViewById(R.id.pagerProfile);
        UserProfileFragmentStateAdapter adapter = new UserProfileFragmentStateAdapter(this);
        adapter.addFragment(new IndividualUserFragment(getApplicationContext(), UserID));
        adapter.addFragment(new IndividualStatisticsFragment(UserID));
        adapter.addFragment(new IndividualBadgesFragment(UserID));
        pager.setAdapter(adapter);

        new TabLayoutMediator(tabs, pager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Activities");
                                break;
                            case 1:
                                tab.setText("Statistics");
                                break;
                            case 2:
                                tab.setText("Badges");
                                break;
                        }
                    }
                }).attach();
    }
}