package com.example.fittrack;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoadFitnessActivitiesTest {
    private FirebaseAuth mAuth;
    private ConnectivityManager.NetworkCallback networkCallback;
    @Before
    public void setUp() throws Exception {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @Test
    public void loadFitnessActivitiesTestIn5Seconds() {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");
        ActivityScenario.launch(HomeActivity.class);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withId(R.id.activitiesRecyclerView))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.activitiesRecyclerView))
                .check(ViewAssertions.matches(hasMinimumChildCount(1)));
    }
}
