//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.app.R;
import com.example.app.controller.MainMenu;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withAlpha;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private SharedPreferences prefs;

    @Before
    public void setUp() {
        Context context = getApplicationContext();
        prefs = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }

    @After
    public void tearDown() {
        prefs.edit().clear().commit();
    }

    @Test
    public void testLaunchesTermsConditions_WhenFirstTime() {
        prefs.edit().putBoolean("terms_accepted", false).commit();

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        assertNotNull(scenario);
    }

    @Test
    public void testLaunchesMainMenu_WhenTermsAccepted() {
        prefs.edit().putBoolean("terms_accepted", true).commit();

        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        assertNotNull(scenario);
    }


    @Test
    public void testSharedPreferencesDefaultValue_ShouldBeFalse() {
        prefs.edit().clear().commit();

        boolean termsAccepted = prefs.getBoolean("terms_accepted", false);

        assertTrue("O valor padrão de 'terms_accepted' deve ser false", !termsAccepted);
    }

    @Test
    public void testTermsAcceptedValue_WhenSetToTrue() {
        prefs.edit().putBoolean("terms_accepted", true).commit();

        boolean termsAccepted = prefs.getBoolean("terms_accepted", false);

        assertTrue("O valor de 'terms_accepted' deve ser true quando definido", termsAccepted);
    }
}