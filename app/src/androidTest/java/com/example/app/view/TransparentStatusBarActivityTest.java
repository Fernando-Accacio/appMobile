package com.example.app.view;

import android.graphics.Insets;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.app.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class TransparentStatusBarActivityTest {

    @Test
    public void testOnCreate() {
        ActivityScenario<TransparentStatusBarActivity> scenario =
                ActivityScenario.launch(TransparentStatusBarActivity.class);

        scenario.onActivity(activity -> {
            assertNotNull(activity);
            assertNotNull(activity.getWindow());
            assertNotNull(activity.findViewById(android.R.id.content));
        });
    }

    @Test
    public void testSetupTransparentStatusBar() {
        ActivityScenario<TransparentStatusBarActivity> scenario =
                ActivityScenario.launch(TransparentStatusBarActivity.class);

        scenario.onActivity(activity -> {
            int statusBarColor = activity.getWindow().getStatusBarColor();
            int transparent = activity.getResources().getColor(android.R.color.transparent);
            assertEquals(transparent, statusBarColor);
        });
    }

    @Test
    public void testSetLightStatusBar() {
        ActivityScenario<TransparentStatusBarActivity> scenario =
                ActivityScenario.launch(TransparentStatusBarActivity.class);

        scenario.onActivity(activity -> {
            activity.setLightStatusBar(true);
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            assertTrue((flags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0);

            activity.setLightStatusBar(false);
            flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            assertFalse((flags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0);
        });
    }

    @Test
    public void testApplyTopInsetPadding() {
        ActivityScenario<TransparentStatusBarActivity> scenario =
                ActivityScenario.launch(TransparentStatusBarActivity.class);

        scenario.onActivity(activity -> {
            View view = new View(activity);
            int originalPadding = view.getPaddingTop();

            activity.applyTopInsetPadding(view);

            view.post(() -> {
                Boolean tag = (Boolean) view.getTag();
                assertNotNull("Tag should not be null", tag); // Certifique-se de que a tag não é null
                assertTrue("Padding should be applied", tag); // Verifique se a tag foi configurada corretamente
            });
        });
    }
}
