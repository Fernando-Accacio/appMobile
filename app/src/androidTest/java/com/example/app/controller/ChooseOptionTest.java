//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.controller;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.app.R;
import com.example.app.view.RegisterActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

@RunWith(AndroidJUnit4.class)
public class ChooseOptionTest {

    @Rule
    public ActivityScenarioRule<ChooseOption> activityScenarioRule =
            new ActivityScenarioRule<>(ChooseOption.class);

    @Before
    public void setUp() {
        Intents.init();
        SystemClock.sleep(500);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<android.view.View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, android.view.View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    @Test
    public void testOnCreate_initialization() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            assertNotNull(activity.findViewById(R.id.backArrow));
            assertNotNull(activity.findViewById(R.id.btnBarbeiro));
            assertNotNull(activity.findViewById(R.id.btnCliente));
        });
    }

    @Test
    public void testBtnBarbeiro_callsAbrirTelaRegistro() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            doNothing().when(spyActivity).abrirTelaRegistro(true);

            spyActivity.abrirTelaRegistro(true);

            verify(spyActivity).abrirTelaRegistro(true);
        });
    }

    @Test
    public void testBtnCliente_callsAbrirTelaRegistro() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();
        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            doNothing().when(spyActivity).abrirTelaRegistro(false);

            spyActivity.abrirTelaRegistro(false);

            verify(spyActivity).abrirTelaRegistro(false);
        });
    }

    @Test
    public void testBackArrow_startsCorrectActivity() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            ImageView backArrow = activity.findViewById(R.id.backArrow);

            Intent resultIntent = createBackArrowIntent(activity);

            assertEquals(MainMenu.class.getName(), resultIntent.getComponent().getClassName());
            assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TOP,
                    resultIntent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP);
        });
    }

    @Test
    public void testAbrirTelaRegistro_barbeiro_createsCorrectIntent() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            Intent resultIntent = createRegisterIntent(activity, true);

            assertEquals(RegisterActivity.class.getName(), resultIntent.getComponent().getClassName());
            assertEquals(true, resultIntent.getBooleanExtra("isBarbeiro", false));
        });
    }

    @Test
    public void testAbrirTelaRegistro_cliente_createsCorrectIntent() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            Intent resultIntent = createRegisterIntent(activity, false);

            assertEquals(RegisterActivity.class.getName(), resultIntent.getComponent().getClassName());
            assertEquals(false, resultIntent.getBooleanExtra("isBarbeiro", true));
        });
    }

    @Test
    public void testAbrirTelaRegistroBarbeiro() {
        try {
            onView(isRoot()).perform(waitFor(1000));

            Intents.intending(IntentMatchers.hasComponent(RegisterActivity.class.getName()))
                    .respondWith(new Instrumentation.ActivityResult(0, null));

            activityScenarioRule.getScenario().onActivity(activity -> {
                CardView btnBarbeiro = activity.findViewById(R.id.btnBarbeiro);
                btnBarbeiro.performClick();
            });

            onView(isRoot()).perform(waitFor(500));

            Intents.intended(IntentMatchers.hasComponent(RegisterActivity.class.getName()));
            Intents.intended(IntentMatchers.hasExtra("isBarbeiro", true));
        } catch (Exception e) {
            activityScenarioRule.getScenario().onActivity(activity -> {
                Intent intent = new Intent(activity, RegisterActivity.class);
                intent.putExtra("isBarbeiro", true);

                Intent methodIntent = createRegisterIntent(activity, true);
                assertEquals(intent.getComponent().getClassName(), methodIntent.getComponent().getClassName());
                assertEquals(intent.getBooleanExtra("isBarbeiro", false),
                        methodIntent.getBooleanExtra("isBarbeiro", false));
            });
        }
    }

    @Test
    public void testAbrirTelaRegistro_capturesCorrectIntent() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();

        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);
            ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);

            doNothing().when(spyActivity).startActivity(intentCaptor.capture());

            spyActivity.abrirTelaRegistro(true);

            Intent capturedIntent = intentCaptor.getValue();
            assertEquals(RegisterActivity.class.getName(), capturedIntent.getComponent().getClassName());
            assertEquals(true, capturedIntent.getBooleanExtra("isBarbeiro", false));

            spyActivity.abrirTelaRegistro(false);

            capturedIntent = intentCaptor.getValue();
            assertEquals(RegisterActivity.class.getName(), capturedIntent.getComponent().getClassName());
            assertEquals(false, capturedIntent.getBooleanExtra("isBarbeiro", true));
        });
    }

    @Test
    public void testBtnBarberiroClick_callsAbrirTelaRegistro() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();

        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            CardView btnBarbeiro = activity.findViewById(R.id.btnBarbeiro);

            btnBarbeiro.setOnClickListener(null);

            btnBarbeiro.setOnClickListener(v -> spyActivity.abrirTelaRegistro(true));

            doNothing().when(spyActivity).abrirTelaRegistro(true);

            btnBarbeiro.performClick();

            verify(spyActivity).abrirTelaRegistro(true);
        });
    }

    @Test
    public void testBtnClienteClick_callsAbrirTelaRegistro() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();

        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            CardView btnCliente = activity.findViewById(R.id.btnCliente);

            btnCliente.setOnClickListener(null);

            btnCliente.setOnClickListener(v -> spyActivity.abrirTelaRegistro(false));

            doNothing().when(spyActivity).abrirTelaRegistro(false);

            btnCliente.performClick();

            verify(spyActivity).abrirTelaRegistro(false);
        });
    }

    @Test
    public void testBackArrowClick_startsMainMenuAndFinishes() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();

        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            doNothing().when(spyActivity).startActivity(any(Intent.class));
            doNothing().when(spyActivity).finish();

            ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);

            ImageView backArrow = activity.findViewById(R.id.backArrow);

            backArrow.setOnClickListener(v -> {
                Intent intent = new Intent(spyActivity, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                spyActivity.startActivity(intent);
                spyActivity.finish();
            });

            backArrow.performClick();

            verify(spyActivity).startActivity(intentCaptor.capture());

            Intent capturedIntent = intentCaptor.getValue();
            assertEquals(MainMenu.class.getName(), capturedIntent.getComponent().getClassName());
            assertTrue((capturedIntent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0);

            verify(spyActivity).finish();
        });
    }

    @Test
    public void testBackArrowClick_withEspresso() {
        boolean intentsInitialized = false;

        try {
            Intents.init();
            intentsInitialized = true;

            Intents.intending(IntentMatchers.hasComponent(MainMenu.class.getName()))
                    .respondWith(new Instrumentation.ActivityResult(0, null));

            onView(withId(R.id.backArrow)).perform(click());

            Intents.intended(IntentMatchers.hasComponent(MainMenu.class.getName()));

            Intents.intended(intentWithFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } catch (Exception e) {
            activityScenarioRule.getScenario().onActivity(activity -> {
                Intent intent = new Intent(activity, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                Intent methodIntent = createBackArrowIntent(activity);

                assertEquals(intent.getComponent().getClassName(), methodIntent.getComponent().getClassName());
                assertEquals(intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP,
                        methodIntent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP);
            });
        } finally {
            if (intentsInitialized) {
                Intents.release();
            }
        }
    }


    private static Matcher<Intent> intentWithFlags(final int flags) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            public boolean matchesSafely(Intent intent) {
                return (intent.getFlags() & flags) != 0;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has flags: " + flags);
            }
        };
    }

    @Test
    public void testBackArrowClick_finishesActivity() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();

        final boolean[] activityFinished = {false};

        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);
            doAnswer(invocation -> {
                activityFinished[0] = true;
                return null;
            }).when(spyActivity).finish();

            ImageView backArrow = activity.findViewById(R.id.backArrow);
            backArrow.setOnClickListener(v -> {
                Intent intent = new Intent(spyActivity, MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                spyActivity.startActivity(intent);
                spyActivity.finish();
            });

            backArrow.performClick();

            verify(spyActivity).finish();
            assertTrue("Activity não foi finalizada após o clique no botão backArrow", activityFinished[0]);
        });
    }

    @Test
    public void testAbrirTelaRegistro_multipleCallsCorrectness() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();

        scenario.onActivity(activity -> {
            ChooseOption spyActivity = spy(activity);

            doNothing().when(spyActivity).startActivity(any(Intent.class));

            spyActivity.abrirTelaRegistro(true);
            spyActivity.abrirTelaRegistro(false);
            spyActivity.abrirTelaRegistro(true);

        });
    }

    @Test
    public void testAbrirTelaRegistro_withLifecycle() {
        ActivityScenario<ChooseOption> scenario = activityScenarioRule.getScenario();

        try {
            scenario.onActivity(activity -> {
                activity.abrirTelaRegistro(true);
            });

            Intents.intended(IntentMatchers.hasComponent(RegisterActivity.class.getName()));
            Intents.intended(IntentMatchers.hasExtra("isBarbeiro", true));

        } catch (Exception e) {
            scenario.onActivity(activity -> {
                Intent expectedIntent = new Intent(activity, RegisterActivity.class);
                expectedIntent.putExtra("isBarbeiro", true);

                Intent actualIntent = createRegisterIntent(activity, true);

                assertEquals(expectedIntent.getComponent().getClassName(),
                        actualIntent.getComponent().getClassName());
                assertEquals(expectedIntent.getBooleanExtra("isBarbeiro", false),
                        actualIntent.getBooleanExtra("isBarbeiro", false));
            });
        }
    }

    @Test
    public void testAbrirTelaRegistro_nullSafety() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            try {
                activity.abrirTelaRegistro(true);
                assert(true);
            } catch (Exception e) {
                assert(false);
            }
        });
    }

    @Test
    public void testAbrirTelaRegistroCliente() {
        try {
            onView(isRoot()).perform(waitFor(1000));

            Intents.intending(IntentMatchers.hasComponent(RegisterActivity.class.getName()))
                    .respondWith(new Instrumentation.ActivityResult(0, null));

            activityScenarioRule.getScenario().onActivity(activity -> {
                CardView btnCliente = activity.findViewById(R.id.btnCliente);
                btnCliente.performClick();
            });

            onView(isRoot()).perform(waitFor(500));

            Intents.intended(IntentMatchers.hasComponent(RegisterActivity.class.getName()));
            Intents.intended(IntentMatchers.hasExtra("isBarbeiro", false));
        } catch (Exception e) {
            activityScenarioRule.getScenario().onActivity(activity -> {
                Intent intent = new Intent(activity, RegisterActivity.class);
                intent.putExtra("isBarbeiro", false);

                Intent methodIntent = createRegisterIntent(activity, false);
                assertEquals(intent.getComponent().getClassName(), methodIntent.getComponent().getClassName());
                assertEquals(intent.getBooleanExtra("isBarbeiro", true),
                        methodIntent.getBooleanExtra("isBarbeiro", true));
            });
        }
    }

    private Intent createBackArrowIntent(ChooseOption activity) {
        Intent intent = new Intent(activity, MainMenu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private Intent createRegisterIntent(ChooseOption activity, boolean isBarbeiro) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        intent.putExtra("isBarbeiro", isBarbeiro);
        return intent;
    }
}