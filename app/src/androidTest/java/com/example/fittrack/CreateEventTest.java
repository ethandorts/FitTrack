package com.example.fittrack;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;


import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static java.util.EnumSet.allOf;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateEventTest {
    FirebaseAuth mAuth;
    @Before
    public void setUp() throws Exception {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @Test
    public void addEventSuccessfully() throws InterruptedException {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");
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
    }

    @Test
    public void addGoalSuccessfully() {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");
        ActivityScenario.launch(CreateGoalActivity.class);
        onView(withId(R.id.spinnerGoalType)).perform(click());
        onView(withText("Distance Goal"))
                .inRoot(isPlatformPopup())
                .perform(click());
        onView(withId(R.id.editTargetDistance)).perform(typeText("5"), closeSoftKeyboard());
        onView(withId(R.id.editTargetDistance)).perform(typeText("5"), closeSoftKeyboard());
        onView(withId(R.id.edit)).perform(click());
        int year = 2025;
        int month = 2;
        int day = 15;
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, month + 1, day));
        onView(withId(android.R.id.button1)).perform(click());
    }

    @Test
    public void createFitnessGroup() {
        mAuth.signInWithEmailAndPassword("brendy@gmail.com", "Brendy1976");
        ActivityScenario.launch(CreateRunningGroupActivity.class);
        onView(withId(R.id.editTargetDistance)).perform(typeText(""), closeSoftKeyboard());
    }

    @Test
    public void joinFitnessGroup() {

    }

    @Test
    public void createGroupPost() {
        ActivityScenario.launch(CreatePostActivity.class);

    }

    @Test
    public void createGroupMeetup() {
        onView(withText("Meetup Requests")).perform(click());

        onView(withId(R.id.btnCreateDistanceGoal)).perform(click());

        onView(withId(R.id.editTargetDistance)).perform(typeText("Weekend Run"), closeSoftKeyboard());


        onView(withId(R.id.editTargetTimeForm)).perform(click());
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2025, 3, 15));
        onView(withId(android.R.id.button1)).perform(click());


        onView(withId(R.id.editGroupLocation)).perform(click());
        onView(withClassName(equalTo(TimePicker.class.getName())))
                .perform(PickerActions.setTime(8, 30));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.editTextLocation)).perform(typeText("Derry Quay"), closeSoftKeyboard());

        onView(withId(R.id.editDistanceGoalDescription)).perform(typeText("Group running session"), closeSoftKeyboard());


        onView(withId(R.id.btnCreateDistanceGoal)).perform(click());

        onView(withId(R.id.meetupsRecyclerView))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Weekend Run"))))
                .check(matches(isDisplayed()));
    }

}
