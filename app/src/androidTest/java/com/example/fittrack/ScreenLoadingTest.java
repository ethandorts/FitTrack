package com.example.fittrack;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScreenLoadingTest {
    private FirebaseAuth mAuth;

    @Before
    public void setUp() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @Test
    public void loadRegisterScreenWithin3Seconds() {
        loadScreen(RegisterActivity.class);
    }

    @Test
    public void loadLoginScreenWithin3Seconds() {
        loadScreen(LoginActivity.class);
    }

    @Test
    public void loadHomeActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(HomeActivity.class);
    }
    @Test
    public void loadSettingsActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(SettingsActivity.class);
    }
    @Test
    public void loadGroupActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(GroupActivity.class);
    }
    @Test
    public void loadAIAssistantActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(AIAssistantActivity.class);
    }

    @Test
    public void loadAdminGroupRequestActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(AdminGroupRequestsActivity.class);
    }

    @Test
    public void loadCommentsActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(CommentsActivity.class);
    }

    @Test
    public void loadMainActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(MainActivity.class);
    }

    @Test
    public void loadNutritionTrackingActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(NutritionTracking.class);
    }

    @Test
    public void loadNutritionTrackingOverviewActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(NutritionTrackingOverview.class);
    }

    @Test
    public void loadOverviewFitnessStatsActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(OverviewFitnessStats.class);
    }

    @Test
    public void loadMyBadgesActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(MyBadgesActivity.class);
    }

    @Test
    public void loadCreateGoalActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(CreateGoalActivity.class);
    }

    @Test
    public void loadCreateManualActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(CreateManualActivity.class);
    }

    @Test
    public void loadCreatePostActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(CreatePostActivity.class);
    }

    @Test
    public void loadCreateRunningGroupActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(CreateRunningGroupActivity.class);
    }

    @Test
    public void loadEditMeetupActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(EditMeetupActivity.class);
    }

    @Test
    public void loadGoalSettingActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(GoalSettingActivity.class);
    }

    @Test
    public void loadFindRunningGroupsActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(FindRunningGroupsActivity.class);
    }

    @Test
    public void loadGroupOverviewActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(GroupOverviewActivity.class);
    }

    @Test
    public void loadDeletePostActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(DeletePostActivity.class);
    }

    @Test
    public void loadSelectExerciseTypeActivityWithin3Seconds() throws InterruptedException {
        loginUser();
        loadScreen(SelectExerciseTypeActivity.class);
    }

    public void loginUser() throws InterruptedException {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");
            Thread.sleep(500);
        }
    }

    public <T extends Activity> void loadScreen(Class<T> activity) {
        Intent intent = new Intent(androidx.test.core.app.ApplicationProvider.getApplicationContext(), activity);

        if (activity.equals(GroupActivity.class) || activity.equals(GroupOverviewActivity.class)) {
            intent.putExtra("GroupID", "Sg8JLYf9lpE1akjQRHBv");
            intent.putExtra("GroupName", "Test Group");
            intent.putExtra("GroupSize", 10);
            intent.putExtra("GroupActivity", "Running");
        }

        if (activity.equals(AdminGroupRequestsActivity.class)) {
            intent.putExtra("GroupID", "Sg8JLYf9lpE1akjQRHBv");
        }

        if (activity.equals(CommentsActivity.class) || activity.equals(OverviewFitnessStats.class)) {
            intent.putExtra("ActivityID", "2BxZb9qLQqEQtYlA");
        }

        long startTime = SystemClock.elapsedRealtime();
        ActivityScenario<T> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(act -> {
            act.getWindow().getDecorView().post(() -> {
                long endTime = SystemClock.elapsedRealtime();
                long loadTime = endTime - startTime;
                System.out.println("LoadTime: " + loadTime);
                assertTrue(activity.getSimpleName() + " took too long to load (" + loadTime + " ms)", loadTime <= 3000);
            });
        });
    }
}
