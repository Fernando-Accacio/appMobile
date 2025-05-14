//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.core.app.PendingIntentCompat.getActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Test
    public void testCamposVisiveisParaCliente() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RegisterActivity.class);
        intent.putExtra("isBarbeiro", false);

        try (ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.edtEndereco)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        }
    }

    @Test
    public void testCamposVisiveisParaBarbeiro() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RegisterActivity.class);
        intent.putExtra("isBarbeiro", true);

        try (ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.edtEndereco)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        }
    }

    @Test
    public void testCamposObrigatorios() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RegisterActivity.class);
        intent.putExtra("isBarbeiro", false);

        try (ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnRegistrar)).perform(click());

            onView(withId(R.id.btnRegistrar)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBackArrowClick() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RegisterActivity.class);
        intent.putExtra("isBarbeiro", false);

        try (ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.backArrow)).perform(click());

            onView(withId(R.id.optionsContainer)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCampoEnderecoVisivelParaBarbeiro() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), RegisterActivity.class);
        intent.putExtra("isBarbeiro", true);

        try (ActivityScenario<RegisterActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.edtEndereco)).check(matches(isDisplayed()));
        }
    }

}
