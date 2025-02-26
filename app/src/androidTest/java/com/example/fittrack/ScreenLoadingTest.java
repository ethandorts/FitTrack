package com.example.fittrack;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
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

    public void loginUser() throws InterruptedException {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");
            Thread.sleep(500);
        }
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
