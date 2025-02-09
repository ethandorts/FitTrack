package com.example.fittrack;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.os.SystemClock;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ScreenLoadingTest {

    @Test
    public void loadRegisterScreenWithin3Seconds() {
        loadScreen(RegisterActivity.class);
    }

    @Test
    public void loadLoginScreenWithin3Seconds() {
        loadScreen(LoginActivity.class);
    }

    @Test
    public void loadHomeActivityWithin3Seconds() {
        loadScreen(HomeActivity.class);
    }
    @Test
    public void loadSettingsActivityWithin3Seconds() {
        loadScreen(SettingsActivity.class);
    }
    @Test
    public void loadGroupActivityWithin3Seconds() {
        loadScreen(GroupActivity.class);
    }
    @Test
    public void loadAIAssistantActivityWithin3Seconds() {
        loadScreen(AIAssistantActivity.class);
    }

    @Test
    public void loadAdminGroupRequestActivityWithin3Seconds() {
        loadScreen(AdminGroupRequestsActivity.class);
    }

    @Test
    public void loadCommentsActivityWithin3Seconds() {
        loadScreen(CommentsActivity.class);
    }

    public <T extends Activity> void loadScreen(Class<T> activity) {
        ActivityScenario<T> scenario = ActivityScenario.launch(activity);
        long startTime = SystemClock.elapsedRealtime();

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
