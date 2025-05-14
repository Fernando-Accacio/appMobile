package com.example.app.view;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.contrib.DrawerMatchers.isOpen;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;


import com.example.app.R;
import com.example.app.controller.MainMenu;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ClientesBarbeiroActivityTest {

    private ActivityScenario<ClientesBarbeiroActivity> scenario;

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.INTERNET
    );

    @Before
    public void setUp() {
        scenario = ActivityScenario.launch(ClientesBarbeiroActivity.class);
    }

    @Test
    public void testDrawerLayout() {
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
    }

    @Test
    public void testToolbarTitle() {
        onView(withId(R.id.toolbar))
                .check(matches(hasDescendant(withText("Barbeiros Disponíveis"))));
    }

    @Test
    public void testToolbarSetup() {
        onView(withId(R.id.toolbar))
                .check(matches(hasDescendant(withText("Barbeiros Disponíveis"))));
    }
}
