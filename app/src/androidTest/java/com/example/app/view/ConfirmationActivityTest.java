//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ConfirmationActivityTest {

    @Test
    public void testConfirmationTextsAreDisplayed() {
        try (ActivityScenario<ConfirmationActivity> scenario = ActivityScenario.launch(ConfirmationActivity.class)) {
            onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
            onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBackArrowClickDoesNotCrash() {
        ActivityScenario<ConfirmationActivity> scenario = ActivityScenario.launch(ConfirmationActivity.class);
        onView(withId(R.id.backArrow)).perform(click());

    }

    @Test
    public void testElementsAreVisibleOnCreate() {
        ActivityScenario<ConfirmationActivity> scenario = ActivityScenario.launch(ConfirmationActivity.class);

        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
        onView(withId(R.id.checkIcon)).check(matches(isDisplayed()));
        onView(withId(R.id.textSavedMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.textReadyForBooking)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackArrowClickFinishesActivity() {
        ActivityScenario<ConfirmationActivity> scenario = ActivityScenario.launch(ConfirmationActivity.class);

        onView(withId(R.id.backArrow)).perform(click());

        scenario.moveToState(Lifecycle.State.DESTROYED);
    }




}
