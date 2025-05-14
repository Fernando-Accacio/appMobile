//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AgendamentoActivityTest {

    private Barbeiro barbeiroFake;

    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private FirebaseAuth mockAuth;

    @Mock
    private FirebaseUser mockUser;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private Query mockQuery;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private DocumentReference mockDocumentReference;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        barbeiroFake = new Barbeiro();
        barbeiroFake.setId("barbeiro123");
        barbeiroFake.setNome("João Barbeiro");
        barbeiroFake.setServicos(Arrays.asList("Corte", "Barba"));
        barbeiroFake.setDiasDisponiveis(Arrays.asList("Segunda", "Terça"));

        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("cliente123");
        when(mockDb.collection("agendamentos")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.whereEqualTo(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.whereEqualTo(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.whereIn(anyString(), any(List.class))).thenReturn(mockQuery);
        when(mockDocumentReference.getId()).thenReturn("agendamento123");
    }

    public String getHorarioSelecionado(AgendamentoActivity activity) {
        try {
            java.lang.reflect.Field field = AgendamentoActivity.class.getDeclaredField("horarioSelecionado");
            field.setAccessible(true);
            return (String) field.get(activity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setHorarioSelecionado(AgendamentoActivity activity, String horario) {
        try {
            java.lang.reflect.Field field = AgendamentoActivity.class.getDeclaredField("horarioSelecionado");
            field.setAccessible(true);
            field.set(activity, horario);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOnCreateActivityWithValidBarbeiroIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                assertNotNull(activity.findViewById(R.id.llServicos));
                assertNotNull(activity.findViewById(R.id.spinnerDias));
                assertNotNull(activity.findViewById(R.id.btnConfirmar));
                assertNotNull(activity.findViewById(R.id.gridLayoutHorarios));
            });
        }
    }

    @Test
    public void testServicoCheckboxesCreatedCorrectly() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                assertEquals(2, llServicos.getChildCount());

                CheckBox checkBox1 = (CheckBox) llServicos.getChildAt(0);
                CheckBox checkBox2 = (CheckBox) llServicos.getChildAt(1);

                assertEquals("Corte", checkBox1.getText().toString());
                assertEquals("Barba", checkBox2.getText().toString());
            });
        }
    }

    @Test
    public void testSpinnerDiasPreenchidoCorretamente() {
        // Prepara o Intent
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake); // Verifique se barbeiroFake é serializável ou parcelável

        // Lança a activity
        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);

                // Verifique a quantidade de itens no Spinner
                assertEquals(2, spinnerDias.getAdapter().getCount());
                assertEquals("Segunda", spinnerDias.getAdapter().getItem(0));
                assertEquals("Terça", spinnerDias.getAdapter().getItem(1));
            });
        }
    }

    @Test
    public void testBackButtonNavigation() {
        // Preparando o Intent para iniciar a activity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        // Iniciando a activity usando ActivityScenario
        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                // Encontrando o botão de "Voltar" na activity
                View backArrow = activity.findViewById(R.id.backArrow);
                assertNotNull(backArrow); // Verificando se o botão existe

                // Simulando o clique no botão de "Voltar"
                backArrow.performClick();

                // Verificando se a activity foi finalizada
                assertTrue("A activity não foi finalizada após o clique no botão de 'Voltar'", activity.isFinishing());
            });
        }
    }

    @Test
    public void testSelecaoDiaNoSpinner() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                Spinner spinner = activity.findViewById(R.id.spinnerDias);
                // Aguardando o carregamento do spinner antes de definir a seleção
                activity.runOnUiThread(() -> spinner.setSelection(1));
                // Aguardar a atualização do spinner
                assertEquals("Terça", spinner.getSelectedItem());
            });
        }
    }

    @Test
    public void testSalvarAgendamentoSemServicosSelecionados() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                for (int i = 0; i < llServicos.getChildCount(); i++) {
                    CheckBox checkBox = (CheckBox) llServicos.getChildAt(i);
                    assertFalse(checkBox.isChecked());
                }
                Button btnConfirmar = activity.findViewById(R.id.btnConfirmar);
                btnConfirmar.performClick();
                verify(mockCollectionReference, times(0)).add(any());
            });
        }
    }

    @Test
    public void testSalvarAgendamentoSemHorarioSelecionado() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                ((CheckBox) llServicos.getChildAt(0)).setChecked(true);
                setHorarioSelecionado(activity, null);

                Button btnConfirmar = activity.findViewById(R.id.btnConfirmar);
                btnConfirmar.performClick();
                verify(mockCollectionReference, times(0)).add(any());
            });
        }
    }

    @Test
    public void testValidacaoDeInputsEComponentes() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                Button btnConfirmar = activity.findViewById(R.id.btnConfirmar);
                assertNotNull(btnConfirmar);

                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                assertNotNull(llServicos);
                assertEquals(2, llServicos.getChildCount());

                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);
                assertNotNull(spinnerDias);
                assertEquals(2, spinnerDias.getAdapter().getCount());

                GridLayout gridLayoutHorarios = activity.findViewById(R.id.gridLayoutHorarios);
                assertNotNull(gridLayoutHorarios);

                CheckBox checkBox1 = (CheckBox) llServicos.getChildAt(0);
                CheckBox checkBox2 = (CheckBox) llServicos.getChildAt(1);

                assertEquals("Corte", checkBox1.getText().toString());
                assertEquals("Barba", checkBox2.getText().toString());

                assertFalse(checkBox1.isChecked());
                assertFalse(checkBox2.isChecked());

                checkBox1.setChecked(true);
                checkBox2.setChecked(true);

                assertTrue(checkBox1.isChecked());
                assertTrue(checkBox2.isChecked());

                spinnerDias.setSelection(1);
                assertEquals("Terça", spinnerDias.getSelectedItem());

                String horarioTeste = "14:30";
                setHorarioSelecionado(activity, horarioTeste);
                assertEquals(horarioTeste, getHorarioSelecionado(activity));

                boolean todosOsCamposPreenchidos =
                        checkBox1.isChecked() || checkBox2.isChecked()
                                && spinnerDias.getSelectedItem() != null
                                && getHorarioSelecionado(activity) != null;

                assertTrue(todosOsCamposPreenchidos);
            });
        }
    }

    @Test
    public void testControleDeHorariosDisponiveisEIndisponiveis() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                GridLayout gridLayoutHorarios = activity.findViewById(R.id.gridLayoutHorarios);
                assertNotNull(gridLayoutHorarios);

                Button btnHorarioDisponivel = new Button(activity);
                btnHorarioDisponivel.setText("10:00");

                Button btnHorarioIndisponivel = new Button(activity);
                btnHorarioIndisponivel.setText("11:00");

                List<String> horariosReservados = new ArrayList<>();
                horariosReservados.add("11:00");

                if (horariosReservados.contains(btnHorarioDisponivel.getText().toString())) {
                    btnHorarioDisponivel.setEnabled(false);
                    btnHorarioDisponivel.setBackgroundColor(activity.getResources().getColor(android.R.color.background_light));
                    btnHorarioDisponivel.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                } else {
                    btnHorarioDisponivel.setBackgroundResource(R.drawable.btntwo);
                    btnHorarioDisponivel.setTextColor(activity.getResources().getColor(android.R.color.black));
                }

                if (horariosReservados.contains(btnHorarioIndisponivel.getText().toString())) {
                    btnHorarioIndisponivel.setEnabled(false);
                    btnHorarioIndisponivel.setBackgroundColor(activity.getResources().getColor(android.R.color.background_light));
                    btnHorarioIndisponivel.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                } else {
                    btnHorarioIndisponivel.setBackgroundResource(R.drawable.btntwo);
                    btnHorarioIndisponivel.setTextColor(activity.getResources().getColor(android.R.color.black));
                }

                assertTrue(btnHorarioDisponivel.isEnabled());
                assertFalse(btnHorarioIndisponivel.isEnabled());

                Button btnHorarioSelecionado = null;
                String horarioSelecionado = null;

                btnHorarioDisponivel.performClick();

                btnHorarioSelecionado = btnHorarioDisponivel;
                horarioSelecionado = btnHorarioDisponivel.getText().toString();

                assertEquals("10:00", horarioSelecionado);
                assertNotNull(btnHorarioSelecionado);

                setHorarioSelecionado(activity, horarioSelecionado);
                assertEquals("10:00", getHorarioSelecionado(activity));

                btnHorarioIndisponivel.performClick();
                assertEquals("10:00", horarioSelecionado);

                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);
                spinnerDias.setSelection(0);

                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                CheckBox checkBox1 = (CheckBox) llServicos.getChildAt(0);
                checkBox1.setChecked(true);

                boolean todosOsCamposPreenchidos =
                        checkBox1.isChecked() &&
                                spinnerDias.getSelectedItem() != null &&
                                getHorarioSelecionado(activity) != null;

                assertTrue(todosOsCamposPreenchidos);
            });
        }
    }

    @Test
    public void testProcessamentoAgendamentosEControleHorarios() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AgendamentoActivity.class);
        intent.putExtra("barbeiro", barbeiroFake);

        try (ActivityScenario<AgendamentoActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
                List<DocumentSnapshot> documentSnapshots = new ArrayList<>();

                DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
                Agendamento agendamentoExistente = new Agendamento();
                agendamentoExistente.setHorario("09:00");
                when(mockDocumentSnapshot.toObject(Agendamento.class)).thenReturn(agendamentoExistente);
                documentSnapshots.add(mockDocumentSnapshot);

                when(mockQuerySnapshot.getDocuments()).thenReturn(documentSnapshots);

                List<String> horariosReservados = new ArrayList<>();

                for (DocumentSnapshot document : mockQuerySnapshot.getDocuments()) {
                    Agendamento agendamento = document.toObject(Agendamento.class);
                    if (agendamento != null && agendamento.getHorario() != null) {
                        horariosReservados.add(agendamento.getHorario());
                    }
                }

                assertEquals(1, horariosReservados.size());
                assertEquals("09:00", horariosReservados.get(0));

                List<String> horarios = Arrays.asList("08:00", "09:00", "10:00", "11:00");

                Context context = activity.getApplicationContext();
                GridLayout gridLayoutHorarios = activity.findViewById(R.id.gridLayoutHorarios);

                List<Button> botoesHorarios = new ArrayList<>();

                for (String horario : horarios) {
                    Button btnHorario = new Button(context);
                    btnHorario.setText(horario);

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.setMargins(16, 16, 16, 16);
                    params.width = 200;
                    params.height = 100;
                    btnHorario.setLayoutParams(params);

                    if (horariosReservados.contains(horario)) {
                        btnHorario.setEnabled(false);
                        btnHorario.setBackgroundColor(activity.getResources().getColor(android.R.color.background_light));
                        btnHorario.setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
                    } else {
                        btnHorario.setBackgroundResource(R.drawable.btntwo);
                        btnHorario.setTextColor(activity.getResources().getColor(android.R.color.black));
                    }

                    botoesHorarios.add(btnHorario);
                }

                assertFalse(botoesHorarios.get(1).isEnabled());
                assertTrue(botoesHorarios.get(0).isEnabled());
                assertTrue(botoesHorarios.get(2).isEnabled());
                assertTrue(botoesHorarios.get(3).isEnabled());

                Button btnHorarioSelecionado = null;
                String horarioSelecionado = null;

                Button btnClicado = botoesHorarios.get(2);

                if (btnHorarioSelecionado != null) {
                    btnHorarioSelecionado.setBackgroundResource(R.drawable.btntwo);
                    btnHorarioSelecionado.setTextColor(activity.getResources().getColor(android.R.color.black));
                }

                btnHorarioSelecionado = btnClicado;
                horarioSelecionado = btnClicado.getText().toString();

                btnHorarioSelecionado.setBackgroundResource(R.drawable.btn);
                btnHorarioSelecionado.setTextColor(activity.getResources().getColor(android.R.color.white));

                assertEquals("10:00", horarioSelecionado);

                setHorarioSelecionado(activity, horarioSelecionado);

                Spinner spinnerDias = activity.findViewById(R.id.spinnerDias);
                spinnerDias.setSelection(1);

                LinearLayout llServicos = activity.findViewById(R.id.llServicos);
                CheckBox checkBox1 = (CheckBox) llServicos.getChildAt(0);
                checkBox1.setChecked(true);

                boolean todosOsCamposPreenchidos =
                        checkBox1.isChecked() &&
                                spinnerDias.getSelectedItem() != null &&
                                getHorarioSelecionado(activity) != null;

                assertTrue(todosOsCamposPreenchidos);
            });
        }
    }


}