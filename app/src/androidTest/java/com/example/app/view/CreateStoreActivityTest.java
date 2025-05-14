//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.app.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class CreateStoreActivityTest {

    private ActivityScenario<CreateStoreActivity> scenario;

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(CreateStoreActivity.class);
    }

    @Test
    public void testActivityLaunchedAndUIElementsAreVisible() {
        onView(withId(R.id.chkCorte)).check(matches(isDisplayed()));
        onView(withId(R.id.chkBarba)).check(matches(isDisplayed()));
        onView(withId(R.id.chkSobrancelha)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSalvar)).check(matches(isDisplayed()));
    }

    @Test
    public void testSalvarButtonClick() {
        onView(withId(R.id.btnSalvar)).perform(click());
    }

    @Test
    public void testCheckAndUncheckCheckboxes() {
        onView(withId(R.id.chkCorte)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.chkCorte)).perform(click()).check(matches(isNotChecked()));
    }

    @Test
    public void testMultipleCheckboxSelections() {
        onView(withId(R.id.chkCorte)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.chkBarba)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.chkSobrancelha)).check(matches(isNotChecked()));
    }

    @Test
    public void testDiasDaSemanaCheckBoxesVisible() {
        onView(withId(R.id.chkDia1)).check(matches(isDisplayed()));
        onView(withId(R.id.chkDia2)).check(matches(isDisplayed()));
        onView(withId(R.id.chkDia3)).check(matches(isDisplayed()));
        onView(withId(R.id.chkDia4)).check(matches(isDisplayed()));
        onView(withId(R.id.chkDia5)).check(matches(isDisplayed()));
        onView(withId(R.id.chkDia6)).check(matches(isDisplayed()));
        onView(withId(R.id.chkDia7)).check(matches(isDisplayed()));
    }

    @Test
    public void testCheckboxesInitialStateIsNotChecked() {
        onView(withId(R.id.chkCorte)).check(matches(isNotChecked()));
        onView(withId(R.id.chkBarba)).check(matches(isNotChecked()));
        onView(withId(R.id.chkSobrancelha)).check(matches(isNotChecked()));
    }

    @Test
    public void testClickBackArrowIfExists() {
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
        onView(withId(R.id.backArrow)).perform(click());
    }

    @Test
    public void testSalvarButtonWithSomeCheckboxesChecked() {
        onView(withId(R.id.chkCorte)).perform(click());
        onView(withId(R.id.chkBarba)).perform(click());

        onView(withId(R.id.chkDia1)).perform(click());
        onView(withId(R.id.chkDia5)).perform(click());

        onView(withId(R.id.btnSalvar)).perform(click());

        onView(withId(R.id.chkCorte)).check(matches(isChecked()));
        onView(withId(R.id.chkBarba)).check(matches(isChecked()));
        onView(withId(R.id.chkDia1)).check(matches(isChecked()));
        onView(withId(R.id.chkDia5)).check(matches(isChecked()));
    }

    @Test
    public void testSalvarButtonWithoutAnyCheckboxChecked() {
        onView(withId(R.id.btnSalvar)).perform(click());

        onView(withId(R.id.chkCorte)).check(matches(isNotChecked()));
        onView(withId(R.id.chkBarba)).check(matches(isNotChecked()));
        onView(withId(R.id.chkSobrancelha)).check(matches(isNotChecked()));
    }
    @Test
    public void testToggleAllDiasDaSemanaCheckboxes() {
        int[] diasIds = {
                R.id.chkDia1, R.id.chkDia2, R.id.chkDia3,
                R.id.chkDia4, R.id.chkDia5, R.id.chkDia6, R.id.chkDia7
        };

        for (int id : diasIds) {
            onView(withId(id)).perform(click()).check(matches(isChecked()));
        }

        for (int id : diasIds) {
            onView(withId(id)).perform(click()).check(matches(isNotChecked()));
        }
    }

    @Test
    public void testSalvarWithAllCheckboxesChecked() {
        onView(withId(R.id.chkCorte)).perform(click());
        onView(withId(R.id.chkBarba)).perform(click());
        onView(withId(R.id.chkSobrancelha)).perform(click());

        onView(withId(R.id.chkDia1)).perform(click());
        onView(withId(R.id.chkDia2)).perform(click());
        onView(withId(R.id.chkDia3)).perform(click());
        onView(withId(R.id.chkDia4)).perform(click());
        onView(withId(R.id.chkDia5)).perform(click());
        onView(withId(R.id.chkDia6)).perform(click());
        onView(withId(R.id.chkDia7)).perform(click());

        onView(withId(R.id.btnSalvar)).perform(click());
    }

}
