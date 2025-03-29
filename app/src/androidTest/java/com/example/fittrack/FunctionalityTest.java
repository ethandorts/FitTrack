package com.example.fittrack;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;


import static org.hamcrest.CoreMatchers.is;

import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FunctionalityTest {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Test
    public void addTimeGoalSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        ActivityScenario.launch(CreateGoalActivity.class);

        onView(withId(R.id.spinnerGoalType)).perform(click());
        onView(withText("Time Goal"))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.etDistanceTarget)).perform(typeText("5"), closeSoftKeyboard());
        onView(withId(R.id.etTimeTarget)).perform(typeText("00:23:00"), closeSoftKeyboard());
        onView(withId(R.id.etCompletionDate)).perform(click());

        int year = 2025;
        int month = 5;
        int day = 15;

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .inRoot(isDialog())
                .perform(PickerActions.setDate(year, month, day));

        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.btnCreateTimeGoal)).perform(click());

        ActivityScenario.launch(GoalSettingActivity.class);
        onView(withText("Time Goal")).check(matches(isDisplayed()));
        onView(withText("Achieve a time of 23:00 for a distance of 5 KM by Thursday, May 15, 2025")).check(matches(isDisplayed()));
    }

    @Test
    public void addDistanceGoalSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        ActivityScenario.launch(CreateGoalActivity.class);

        onView(withId(R.id.spinnerGoalType)).perform(click());
        onView(withText("Distance Goal"))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.editEnterDistanceGoal)).perform(typeText("5"), closeSoftKeyboard());
        onView(withId(R.id.editDistanceCompletionDate)).perform(click());

        int year = 2025;
        int month = 5;
        int day = 15;

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .inRoot(isDialog())
                .perform(PickerActions.setDate(year, month, day));

        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.btnCreateGoalDistance)).perform(click());

        ActivityScenario.launch(GoalSettingActivity.class);
        onView(withText("Time Goal")).check(matches(isDisplayed()));
        onView(withText("Achieve a time of 23:00 for a distance of 5 KM by Thursday, May 15, 2025")).check(matches(isDisplayed()));
        mAuth.signOut();
    }

    @Test
    public void addCalorieGoalSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        ActivityScenario.launch(CreateGoalActivity.class);

        onView(withId(R.id.spinnerGoalType)).perform(click());
        onView(withText("Calorie Goal"))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.editenterCalorieGoal)).perform(typeText("2000"), closeSoftKeyboard());
        onView(withId(R.id.editCalorieCompletionDate)).perform(click());

        int year = 2025;
        int month = 6;
        int day = 20;

        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .inRoot(isDialog())
                .perform(PickerActions.setDate(year, month, day));

        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.btnCreateCaloriesGoal)).perform(click());

        ActivityScenario.launch(GoalSettingActivity.class);
        onView(withText("Calorie Goal")).check(matches(isDisplayed()));
        onView(withText("Consume 2000 calories by the end of Friday, June 20, 2025")).check(matches(isDisplayed()));
        mAuth.signOut();
    }

    @Test
    public void createFitnessGroupSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        ActivityScenario<CreateRunningGroupActivity> scenario = ActivityScenario.launch(CreateRunningGroupActivity.class);

        onView(withId(R.id.editGroupName))
                .perform(typeText("Test Runners"), closeSoftKeyboard());

        onView(withId(R.id.editGroupFitnessActivity)).perform(click());
        onView(withText("Running"))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.editGroupLocation))
                .perform(typeText("New York City"), closeSoftKeyboard());

        onView(withId(R.id.editGroupMotto))
                .perform(typeText("Run Fast, Live Healthy"), closeSoftKeyboard());

        onView(withId(R.id.editGroupDetailedDescription))
                .perform(typeText("Join us every morning at 6 AM to run and have fun!"), closeSoftKeyboard());

        onView(withId(R.id.btnCreateFitnessGroup)).perform(click());
        mAuth.signOut();
    }


    @Test
    public void createGroupPostSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GroupActivity.class);
        intent.putExtra("GroupID", "Sg8JLYf9lpE1akjQRHBv");
        intent.putExtra("GroupName", "Test Group");
        intent.putExtra("GroupSize", 10);
        intent.putExtra("GroupActivity", "Running");

        ActivityScenario.launch(intent);

        onView(withText("Posts")).perform(click());
        onView(withId(R.id.editMessage)).perform(typeText("Any good running routes?"), closeSoftKeyboard());
        onView(withId(R.id.btnImgSend)).perform(click());
        Thread.sleep(2000);

        onView(withId(R.id.postsRecyclerView))
                .check(matches(hasDescendant(withText("Any good running routes?"))));
        mAuth.signOut();
        Thread.sleep(5000);
    }

    @Test
    public void createGroupMeetupSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), GroupActivity.class);
        intent.putExtra("GroupID", "Sg8JLYf9lpE1akjQRHBv");
        intent.putExtra("GroupName", "Test Group");
        intent.putExtra("GroupSize", 10);
        intent.putExtra("GroupActivity", "Running");

        ActivityScenario.launch(intent);

        onView(withText("Meetup Requests")).perform(click());
        onView(withId(R.id.btnCreateMeetup)).perform(click());
        onView(withId(R.id.editCreateMeetupTitle))
                .perform(typeText("Test Meetup"), closeSoftKeyboard());
        onView(withId(R.id.editCreateMeetupLocation))
                .perform(typeText("Test Location"), closeSoftKeyboard());
        onView(withId(R.id.editCreateMeetupDescription))
                .perform(typeText("Test Description"), closeSoftKeyboard());

        onView(withId(R.id.editCreateMeetupDate)).perform(click());
        java.util.Calendar futureDate = java.util.Calendar.getInstance();
        futureDate.add(java.util.Calendar.DAY_OF_MONTH, 5);
        onView(withClassName(org.hamcrest.Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(
                        futureDate.get(java.util.Calendar.YEAR),
                        futureDate.get(java.util.Calendar.MONTH) + 1,
                        futureDate.get(java.util.Calendar.DAY_OF_MONTH)
                ));
        onView(withText("OK")).perform(click());


        onView(withId(R.id.editCreateMeetupTime)).perform(click());
        onView(withClassName(org.hamcrest.Matchers.equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(10, 30));
        onView(withText("OK")).perform(click());

        onView(withId(R.id.btnCreateMeetup2)).perform(click());

        onView(withId(R.id.meetupsRecyclerView))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Test Meetup"))));

        onView(withText("Test Meetup")).check(matches(isDisplayed()));
        mAuth.signOut();
    }

    @Test
    public void sendDirectMessageSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MessagingChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", "Aileen Doherty");
        bundle.putString("UserID", "75IWND3yGZTdi4f05jkcApf4N9p2");
        intent.putExtras(bundle);

        ActivityScenario.launch(intent);

        String message = "Hello, I have sent a test message!";
        onView(withId(R.id.editPost)).perform(typeText(message), closeSoftKeyboard());

        onView(withId(R.id.btnSendPost)).perform(click());
        onView(withText("Hello, I have sent a test message!"));
        onView(withId(R.id.editPost)).check(matches(withText("")));
        mAuth.signOut();
        Thread.sleep(3000);
    }

    @Test
    public void addCalendarEventSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        ActivityScenario.launch(HomeActivity.class);
        onView(withId(R.id.settings_bottom)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.recyclerFitTrackMenu))
                .perform(actionOnItem(
                        hasDescendant(withText("My Fitness Planner")),
                        click()));
        onView(withId(R.id.btnAddEvent)).perform(click());

        onView(withId(R.id.enterEventName)).perform(typeText("5KM Run"), closeSoftKeyboard());
        onView(withId(R.id.enterActivityType)).perform(click());
        onView(withText("Running"))
                .inRoot(isPlatformPopup())
                .perform(click());
        onView(withId(R.id.enterDescription)).perform(typeText("Tempo Run"), closeSoftKeyboard());
        onView(withId(R.id.btnSaveEvent)).perform(click());
        Thread.sleep(1000);


        ActivityScenario.launch(Calendar.class);
        onView(withId(R.id.recyclerViewCalendarEvent))
                .check(matches(hasDescendant(withText("5KM Run"))));
        mAuth.signOut();
    }

    @Test
    public void loadFitnessActivitiesTestIn5Seconds() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

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

        ActivityScenario.launch(SettingsActivity.class);
        onView(withId(R.id.btnLogOut)).perform(click());
        mAuth.signOut();
    }
    @Test
    public void loadFitnessActivitiesOfflineTestIn5Seconds() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("aileeneed@gmail.com", "Aileen1975");
        Thread.sleep(2000);
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

    @Test
    public void loginSuccessfullyTest() throws InterruptedException {
        Intents.init();
        ActivityScenario.launch(LoginActivity.class);
        onView(withId(R.id.edit_text_firstname)).perform(replaceText("brendy@gmail.com"));
        onView(withId(R.id.edit_text_password)).perform(replaceText("Brendy1976"));
        onView(withId(R.id.btnLogin)).perform(click());

        Thread.sleep(500);
        Intents.intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
        mAuth.signOut();
    }


    @Test
    public void viewPersonalBestsSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        ActivityScenario.launch(PersonalRecordsActivity.class);

        onView(withText("1 KM")).check(matches(isDisplayed()));
        onView(withText("5 KM")).check(matches(isDisplayed()));
        onView(withText("10 KM")).check(matches(isDisplayed()));
        onView(withText("Half Marathon")).check(matches(isDisplayed()));
        onView(withText("Marathon")).check(matches(isDisplayed()));
        mAuth.signOut();
    }

    @Test
    public void viewFitnessBadgesSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");

        Thread.sleep(3000);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MessagingChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", "Brendan Doherty");
        bundle.putString("UserID", "1Mbd9AI2eZc8wORYcsV7jxNHJLV2");
        intent.putExtras(bundle);

        ActivityScenario.launch(intent);


        ActivityScenario.launch(MyBadgesActivity.class);

        onView(withText("Ran 25 KM in January 2025")).check(matches(isDisplayed()));
        onView(withText("Ran 50 KM in January 2025")).check(matches(isDisplayed()));
        mAuth.signOut();
    }

    @Test
    public void modifyProfileDetailsSuccessfully() throws InterruptedException {

    }

    @Test
    public void joinFitnessGroupSuccessfully() throws InterruptedException {

    }

    @Test
    public void receiveAIGeneratedFitnessAdviceSuccessfully() throws InterruptedException {

    }

    @Test
    public void createManualFitnessActivityTestSuccessfully() throws InterruptedException {

    }
}

