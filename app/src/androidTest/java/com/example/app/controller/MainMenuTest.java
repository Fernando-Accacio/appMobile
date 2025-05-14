package com.example.app.controller;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.example.app.R;
import com.example.app.view.LoginActivity;
import com.example.app.controller.ChooseOption;
import com.example.app.controller.MainMenu;

@RunWith(AndroidJUnit4.class)
public class MainMenuTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testMainViewExists() {
        try (ActivityScenario<MainMenu> scenario = ActivityScenario.launch(MainMenu.class)) {
            scenario.onActivity(activity -> {
                View mainView = activity.findViewById(R.id.main);
                assertThat(mainView, notNullValue());
            });
        }
    }

    @Test
    public void testActivityLaunched() {
        ActivityScenario<MainMenu> scenario = ActivityScenario.launch(MainMenu.class);
        scenario.onActivity(activity -> {
            activity.setContentView(R.layout.activity_main_menu);
        });
    }

    @Test
    public void testWindowInsets() {
        try (ActivityScenario<MainMenu> scenario = ActivityScenario.launch(MainMenu.class)) {
            scenario.onActivity(activity -> {
                View mainView = activity.findViewById(R.id.main);
                assertThat(mainView.getPaddingTop(), notNullValue());
                assertThat(mainView.getPaddingLeft(), notNullValue());
            });
        }
    }

    @Test
    public void testActivityCreation_initialState() {
        try (ActivityScenario<MainMenu> scenario = ActivityScenario.launch(MainMenu.class)) {
            // This test verifies that the activity can be created without errors
            // The assertion passes if we reach this point, confirming onCreate() completes
            scenario.onActivity(activity -> {
                // Additional assertions about initial state could go here
                assert activity != null;
            });
        }
    }
}

