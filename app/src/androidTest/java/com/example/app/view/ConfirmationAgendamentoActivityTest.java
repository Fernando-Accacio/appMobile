//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.app.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ConfirmationAgendamentoActivityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testAgendamentoDetails() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvServico)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDataHora)).check(matches(isDisplayed()));

            onView(withId(R.id.tvServico)).check(matches(withText("Serviço: Corte de Cabelo")));
            onView(withId(R.id.tvDataHora)).check(matches(withText("Data/Hora: 2025-06-15 10:30")));
        }
    }

    @Test
    public void testAgendamentoDetailsDisplay() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvServico)).check(matches(withText("Serviço: Corte de Cabelo")));

            onView(withId(R.id.tvDataHora)).check(matches(withText("Data/Hora: 2025-06-15 10:30")));
        }
    }

    @Test
    public void testHeaderTitleDisplay() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvDashboardTitle))
                    .check(matches(isDisplayed()))
                    .check(matches(withText("Agendamento Confirmado!")));
        }
    }

    @Test
    public void testCheckIconDisplay() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(allOf(
                    withParent(withParent(withId(android.R.id.content))),
                    withId(R.id.backArrow)
            )).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testDifferentServiceValue() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Bruno");
        intent.putExtra("SERVICO", "Barba");
        intent.putExtra("DATA_HORA", "2025-06-20 14:45");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.tvServico)).check(matches(withText("Serviço: Barba")));
            onView(withId(R.id.tvDataHora)).check(matches(withText("Data/Hora: 2025-06-20 14:45")));
        }
    }

    @Test
    public void testBackButtonNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                activity.onBackPressed();
            });

            intended(hasComponent(MainActivity.class.getName()));
        }
    }

    @Test
    public void testLayoutStructure() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.backArrow)).check(matches(isDisplayed()));

            onView(withId(R.id.tvDashboardTitle)).check(matches(isDisplayed()));

            onView(withText("Detalhes do Agendamento:")).check(matches(isDisplayed()));
            onView(withId(R.id.tvServico)).check(matches(isDisplayed()));
            onView(withId(R.id.tvDataHora)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBarbeiroNomeIsNotDisplayedDirectly() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmationAgendamentoActivity.class);
        intent.putExtra("BARBEIRO_NOME", "Maria");
        intent.putExtra("SERVICO", "Corte de Cabelo");
        intent.putExtra("DATA_HORA", "2025-06-15 10:30");

        try (ActivityScenario<ConfirmationAgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            try {
                onView(withText("Maria")).check(doesNotExist());
                assertTrue(true);
            } catch (Exception e) {
                assertTrue("O nome do barbeiro não deveria estar visível diretamente na tela", false);
            }
        }
    }
}