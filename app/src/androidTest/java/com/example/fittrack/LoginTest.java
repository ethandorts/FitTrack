package com.example.fittrack;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    private FirebaseAuth mAuth;

    @Before
    public void setUp() throws InterruptedException {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        Intents.init();
        ActivityScenario.launch(LoginActivity.class);
    }


    @Test
    public void loginUnsuccessfullyTest() throws InterruptedException {
        onView(withId(R.id.edit_text_firstname)).perform(replaceText("brendy@gmail.com"));
        onView(withId(R.id.edit_text_password)).perform(replaceText("password123"));
        onView(withId(R.id.btnLogin)).perform(click());

        Thread.sleep(500);
        Intents.intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void loginSuccessfullyTest() throws InterruptedException {
        onView(withId(R.id.edit_text_firstname)).perform(replaceText("brendy@gmail.com"));
        onView(withId(R.id.edit_text_password)).perform(replaceText("Brendy1976"));
        onView(withId(R.id.btnLogin)).perform(click());

        Thread.sleep(500);
        Intents.intended(hasComponent(HomeActivity.class.getName()));
    }

    @After
    public void EndTesting() {
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
        Intents.release();
    }


}
