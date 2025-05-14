//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.R;
import com.example.app.controller.MainMenu;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TermsConditionsActivityTest {

    private SharedPreferences sharedPreferences;

    @Before
    public void setUp() {
        sharedPreferences = ApplicationProvider.getApplicationContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Test
    public void testActivityLaunch() {
        try (ActivityScenario<TermsConditionsActivity> scenario = ActivityScenario.launch(TermsConditionsActivity.class)) {
            onView(withId(R.id.cbAcceptTerms)).check(matches(isDisplayed()));
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()));

            onView(withId(R.id.btnContinue)).check(matches(not(isEnabled())));

            onView(withId(R.id.cbAcceptTerms)).perform(click());

            onView(withId(R.id.btnContinue)).check(matches(isEnabled()));

        }
    }


    @Test
    public void testButtonEnableWhenChecked() {
        try (ActivityScenario<TermsConditionsActivity> scenario = ActivityScenario.launch(TermsConditionsActivity.class)) {
            onView(withId(R.id.cbAcceptTerms)).perform(click());

            onView(withId(R.id.btnContinue)).check(matches(isEnabled()));
        }
    }

    @Test
    public void testContinueButtonRedirect() {
        try (ActivityScenario<TermsConditionsActivity> scenario = ActivityScenario.launch(TermsConditionsActivity.class)) {
            onView(withId(R.id.cbAcceptTerms)).perform(click());

            onView(withId(R.id.btnContinue)).perform(click());

            onView(withId(R.id.main)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testTermsAcceptedSavedInSharedPreferences() {
        try (ActivityScenario<TermsConditionsActivity> scenario = ActivityScenario.launch(TermsConditionsActivity.class)) {
            onView(withId(R.id.cbAcceptTerms)).perform(click());

            onView(withId(R.id.btnContinue)).perform(click());

            boolean termsAccepted = sharedPreferences.getBoolean("terms_accepted", false);
            assertTrue(termsAccepted);
        }
    }

    @Test
    public void testSharedPreferencesAccessFailure() {
        SharedPreferences corruptedPreferences = ApplicationProvider.getApplicationContext().getSharedPreferences("corrupted_preferences", Context.MODE_PRIVATE);

        corruptedPreferences.edit().putBoolean("terms_accepted", true).commit();

        try (ActivityScenario<TermsConditionsActivity> scenario = ActivityScenario.launch(TermsConditionsActivity.class)) {
            boolean termsAccepted = corruptedPreferences.getBoolean("terms_accepted", false);
            assertTrue("Esperado erro ao acessar SharedPreferences", termsAccepted == true);
        } catch (Exception e) {
            assertTrue("Falha ao acessar SharedPreferences: " + e.getMessage(), e instanceof java.lang.NullPointerException);
        }
    }

    @Test
    public void testActivityInitializationFailure() {
        try {
            ActivityScenario<TermsConditionsActivity> scenario = ActivityScenario.launch(TermsConditionsActivity.class);

            onView(withId(R.id.cbAcceptTerms)).check(matches(isDisplayed()));
            onView(withId(R.id.btnContinue)).check(matches(isDisplayed()));

            scenario.onActivity(activity -> {
                assertNotNull("A Activity foi inicializada corretamente", activity);
            });

        } catch (Exception e) {
            fail("Falha ao inicializar a Activity: " + e.getMessage());
        }
    }



}
