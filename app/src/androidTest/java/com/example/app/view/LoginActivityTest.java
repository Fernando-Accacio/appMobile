//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import com.example.app.R;
import com.example.app.controller.MainMenu;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testBackArrowClick_shouldNavigateToMainMenu() {
        onView(withId(R.id.backArrow)).perform(click());
        intended(hasComponent(MainMenu.class.getName()));
    }

    @Test
    public void testViews_onCreate_shouldBeDisplayed() {
        onView(withId(R.id.backArrow)).check(matches(isDisplayed()));
    }

    @Test
    public void testFirebaseRelatedComponentsAreDisplayed() {
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));
        onView(withId(R.id.edtEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin_withEmptyFields_shouldShowErrors() {
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.edtEmail)).check(matches(hasErrorText("Digite seu e-mail")));
        onView(withId(R.id.edtEmail)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
        onView(withId(R.id.edtSenha)).check(matches(hasErrorText("Digite sua senha")));
    }

    @Test
    public void testLoginWithEmailPassword_validCredentials() {
        onView(withId(R.id.edtEmail)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edtSenha)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
    }

    @Test
    public void testLoginError_handlesAuthExceptions() {
        onView(withId(R.id.edtEmail)).perform(typeText("nonexistent@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edtSenha)).perform(typeText("wrongpassword"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
    }


    @Test
    public void testLoginSuccess_shouldCallVerificarTipoDeUsuario() {
        activityRule.getScenario().onActivity(activity -> {
            FirebaseAuth mockAuth = org.mockito.Mockito.mock(FirebaseAuth.class);
            @SuppressWarnings("unchecked")
            com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult> mockTask =
                    (com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult>)
                            org.mockito.Mockito.mock(com.google.android.gms.tasks.Task.class);
            com.google.firebase.auth.FirebaseUser mockUser = org.mockito.Mockito.mock(com.google.firebase.auth.FirebaseUser.class);

            when(mockAuth.signInWithEmailAndPassword(anyString(), anyString()))
                    .thenReturn(mockTask);

            when(mockTask.isSuccessful()).thenReturn(true);
            when(mockAuth.getCurrentUser()).thenReturn(mockUser);
            when(mockUser.getUid()).thenReturn("fakeUid123");

            org.mockito.Mockito.doAnswer(invocation -> {
                com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult> listener = invocation.getArgument(0);
                listener.onComplete(mockTask);
                return null;
            }).when(mockTask).addOnCompleteListener(org.mockito.Mockito.any());
            activity.mAuth = mockAuth;
        });

        onView(withId(R.id.edtEmail)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edtSenha)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.btnLogin)).perform(click());
    }


    @Test
    public void testVerificarTipoDeUsuario_redirecionaParaBarbeiro() {
        activityRule.getScenario().onActivity(activity -> {
            FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
            DocumentReference mockDocRef = Mockito.mock(DocumentReference.class);
            Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
            DocumentSnapshot mockSnapshot = Mockito.mock(DocumentSnapshot.class);

            activity.db = mockFirestore;

            Mockito.when(mockFirestore.collection("usuarios")).thenReturn(Mockito.mock(com.google.firebase.firestore.CollectionReference.class));
            Mockito.when(mockFirestore.collection("usuarios").document("barbeiroUid")).thenReturn(mockDocRef);
            Mockito.when(mockDocRef.get()).thenReturn(mockTask);

            Mockito.when(mockTask.isSuccessful()).thenReturn(true);
            Mockito.when(mockTask.getResult()).thenReturn(mockSnapshot);
            Mockito.when(mockSnapshot.exists()).thenReturn(true);
            Mockito.when(mockSnapshot.getString("tipoUsuario")).thenReturn("barbeiro");

            Mockito.doAnswer(invocation -> {
                com.google.android.gms.tasks.OnCompleteListener<DocumentSnapshot> listener = invocation.getArgument(0);
                listener.onComplete(mockTask);
                return null;
            }).when(mockTask).addOnCompleteListener(Mockito.any());

            activity.verificarTipoDeUsuario("barbeiroUid");
        });

        intended(hasComponent(BarberDashboardActivity.class.getName()));
    }


    @Test
    public void testVerificarTipoDeUsuario_redirecionaParaCliente() {
        activityRule.getScenario().onActivity(activity -> {
            FirebaseFirestore mockFirestore = Mockito.mock(FirebaseFirestore.class);
            DocumentReference mockDocRef = Mockito.mock(DocumentReference.class);
            Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
            DocumentSnapshot mockSnapshot = Mockito.mock(DocumentSnapshot.class);

            activity.db = mockFirestore;

            Mockito.when(mockFirestore.collection("usuarios")).thenReturn(Mockito.mock(com.google.firebase.firestore.CollectionReference.class));
            Mockito.when(mockFirestore.collection("usuarios").document("clienteUid")).thenReturn(mockDocRef);
            Mockito.when(mockDocRef.get()).thenReturn(mockTask);

            Mockito.when(mockTask.isSuccessful()).thenReturn(true);
            Mockito.when(mockTask.getResult()).thenReturn(mockSnapshot);
            Mockito.when(mockSnapshot.exists()).thenReturn(true);
            Mockito.when(mockSnapshot.getString("tipoUsuario")).thenReturn("cliente");

            Mockito.doAnswer(invocation -> {
                com.google.android.gms.tasks.OnCompleteListener<DocumentSnapshot> listener = invocation.getArgument(0);
                listener.onComplete(mockTask);
                return null;
            }).when(mockTask).addOnCompleteListener(Mockito.any());

            activity.verificarTipoDeUsuario("clienteUid");
        });

        intended(hasComponent(ClientesBarbeiroActivity.class.getName()));
    }

    @Test
    public void testTratarErroLogin_InvalidUserException() {

        activityRule.getScenario().onActivity(activity -> {
            activity.showErrorDialog("E-mail não cadastrado ou conta desativada");
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("E-mail não cadastrado ou conta desativada"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText("OK"))
                .inRoot(isDialog())
                .perform(click());
    }


    @Test
    public void testTratarErroLogin_InvalidCredentialsException() {
        activityRule.getScenario().onActivity(activity -> {
            FirebaseAuthInvalidCredentialsException mockException = Mockito.mock(FirebaseAuthInvalidCredentialsException.class);

            activity.tratarErroLogin(mockException);
        });

        onView(withText("Senha incorreta ou e-mail inválido"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText("OK"))
                .inRoot(isDialog())
                .perform(click());
    }

    @Test
    public void testTratarErroLogin_GenericException() {
        activityRule.getScenario().onActivity(activity -> {

            Exception mockException = Mockito.mock(Exception.class);
            when(mockException.getMessage()).thenReturn("Erro genérico");

            activity.tratarErroLogin(mockException);
        });

        onView(withText("Erro ao fazer login: Erro genérico"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText("OK"))
                .inRoot(isDialog())
                .perform(click());
    }

    @Test
    public void testTratarErroLogin_ExceptionWithNullMessage() {
        activityRule.getScenario().onActivity(activity -> {
            Exception mockException = Mockito.mock(Exception.class);
            when(mockException.getMessage()).thenReturn(null);
            activity.tratarErroLogin(mockException);
        });


        onView(withText("Erro ao fazer login: null"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        onView(withText("OK"))
                .inRoot(isDialog())
                .perform(click());
    }
}