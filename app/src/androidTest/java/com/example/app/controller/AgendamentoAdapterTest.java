//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.controller;

import static com.google.common.base.Verify.verify;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

public class AgendamentoAdapterTest {

    private AgendamentoAdapter adapter;

    @Before
    public void setUp() {
        adapter = new AgendamentoAdapter(Collections.emptyList());
    }

    @Test
    public void testGetPrioridadeStatus_pendente() {
        int prioridade = adapter.getPrioridadeStatus("pendente");
        assertEquals(1, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_confirmado() {
        int prioridade = adapter.getPrioridadeStatus("confirmado");
        assertEquals(2, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_cancelado() {
        int prioridade = adapter.getPrioridadeStatus("cancelado");
        assertEquals(3, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_defaultCase() {
        int prioridade = adapter.getPrioridadeStatus("outro");
        assertEquals(4, prioridade);
    }

    @Test
    public void testGetPrioridadeStatus_nullStatus() {
        int prioridade = adapter.getPrioridadeStatus(null);
        assertEquals(3, prioridade);
    }

    @Test
    public void testOrdenarAgendamentos_porPrioridade() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("cancelado");
        Agendamento a2 = new Agendamento();
        a2.setStatus("pendente");
        Agendamento a3 = new Agendamento();
        a3.setStatus("confirmado");

        List<Agendamento> ordenada = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3));

        assertEquals("pendente", ordenada.get(0).getStatus());
        assertEquals("confirmado", ordenada.get(1).getStatus());
        assertEquals("cancelado", ordenada.get(2).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_mesmoStatus() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("confirmado");

        Agendamento a2 = new Agendamento();
        a2.setStatus("confirmado");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2));

        assertEquals("confirmado", resultado.get(0).getStatus());
        assertEquals("confirmado", resultado.get(1).getStatus());
        assertSame(a1, resultado.get(0));
        assertSame(a2, resultado.get(1));
    }

    @Test
    public void testOrdenarAgendamentos_diferentesStatus() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("confirmado");

        Agendamento a2 = new Agendamento();
        a2.setStatus("pendente");

        Agendamento a3 = new Agendamento();
        a3.setStatus("cancelado");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3));

        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("confirmado", resultado.get(1).getStatus());
        assertEquals("cancelado", resultado.get(2).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_comVariosItens() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("cancelado");

        Agendamento a2 = new Agendamento();
        a2.setStatus("pendente");

        Agendamento a3 = new Agendamento();
        a3.setStatus("confirmado");

        Agendamento a4 = new Agendamento();
        a4.setStatus("pendente");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3, a4));

        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("pendente", resultado.get(1).getStatus());
        assertEquals("confirmado", resultado.get(2).getStatus());
        assertEquals("cancelado", resultado.get(3).getStatus());
    }


    @Test
    public void testGetItemCount() {
        Agendamento a1 = new Agendamento();
        Agendamento a2 = new Agendamento();

        AgendamentoAdapter novaAdapter = new AgendamentoAdapter(Arrays.asList(a1, a2));
        assertEquals(2, novaAdapter.getItemCount());
    }

    @Test
    public void testOrdenarAgendamentos_listaVazia() {
        List<Agendamento> resultado = adapter.ordenarAgendamentos(Collections.emptyList());
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testOrdenarAgendamentos_listaComUmItem() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("pendente");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Collections.singletonList(a1));
        assertEquals(1, resultado.size());
        assertEquals("pendente", resultado.get(0).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_ordemJaCorreta() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("pendente");
        Agendamento a2 = new Agendamento();
        a2.setStatus("confirmado");
        Agendamento a3 = new Agendamento();
        a3.setStatus("cancelado");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a1, a2, a3));

        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("confirmado", resultado.get(1).getStatus());
        assertEquals("cancelado", resultado.get(2).getStatus());
    }

    @Test
    public void testOrdenarAgendamentos_comStatusDesconhecido() {
        Agendamento a1 = new Agendamento();
        a1.setStatus("pendente");
        Agendamento a2 = new Agendamento();
        a2.setStatus("xyz");

        List<Agendamento> resultado = adapter.ordenarAgendamentos(Arrays.asList(a2, a1));
        assertEquals("pendente", resultado.get(0).getStatus());
        assertEquals("xyz", resultado.get(1).getStatus());
    }

    @Test
    public void testOnCreateViewHolder() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ViewGroup parent = new LinearLayout(context);

        AgendamentoAdapter.AgendamentoViewHolder holder = adapter.onCreateViewHolder(parent, 0);

        assertNotNull(holder);

        assertNotNull(holder.itemView);
    }

    @Test
    public void testOnBindViewHolder_corTextoStatusPendente() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("pendente");

        Context context = ApplicationProvider.getApplicationContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));
        adapter.onBindViewHolder(viewHolder, 0);

        int expectedColor = context.getResources().getColor(android.R.color.holo_orange_dark);
        int actualColor = viewHolder.txtStatus.getCurrentTextColor();

        assertEquals(expectedColor, actualColor);
        assertEquals(View.VISIBLE, viewHolder.layoutBotoes.getVisibility());
        assertEquals("pendente", viewHolder.txtStatus.getText().toString().toLowerCase());
    }


    @Test
    public void testOnBindViewHolder_camposPreenchidosCorretamente() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("confirmado");
        agendamento.setBarbeiroNome("Carlos");
        agendamento.setServico("Barba");
        agendamento.setDia("10/05/2025");
        agendamento.setHorario("16:30");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        assertEquals("Carlos", viewHolder.txtBarbeiroNome.getText().toString());
        assertEquals("Barba", viewHolder.txtServico.getText().toString());
        assertEquals("10/05/2025 - 16:30", viewHolder.txtDiaHorario.getText().toString());
        assertEquals("confirmado", viewHolder.txtStatus.getText().toString());
    }



    @Test
    public void testOnBindViewHolder_pendente() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("pendente");
        agendamento.setBarbeiroNome("João");
        agendamento.setServico("Corte de Cabelo");
        agendamento.setDia("05/05/2025");
        agendamento.setHorario("14:00");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null);

        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        viewHolder.txtStatus.setText(agendamento.getStatus());

        assertNotNull(viewHolder.txtStatus);
        assertEquals("pendente", viewHolder.txtStatus.getText().toString());
    }


    @Test
    public void testOnBindViewHolder_statusConfirmado() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("confirmado");
        agendamento.setBarbeiroNome("Carlos");
        agendamento.setServico("Barba");
        agendamento.setDia("10/05/2025");
        agendamento.setHorario("16:30");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        int expectedColor = context.getResources().getColor(android.R.color.holo_green_dark);
        int actualColor = viewHolder.txtStatus.getCurrentTextColor();

        assertEquals(expectedColor, actualColor);
        assertEquals(View.VISIBLE, viewHolder.layoutBotoes.getVisibility());
    }

    @Test
    public void testOnBindViewHolder_statusCancelado() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("cancelado");
        agendamento.setBarbeiroNome("Pedro");
        agendamento.setServico("Corte Simples");
        agendamento.setDia("15/05/2025");
        agendamento.setHorario("10:00");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        int expectedColor = context.getResources().getColor(android.R.color.darker_gray);
        int actualColor = viewHolder.txtStatus.getCurrentTextColor();

        assertEquals(expectedColor, actualColor);
        assertEquals(View.GONE, viewHolder.layoutBotoes.getVisibility());
    }

    @Test
    public void testOnBindViewHolder_statusDesconhecido() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("em análise");
        agendamento.setBarbeiroNome("Ricardo");
        agendamento.setServico("Barba e Cabelo");
        agendamento.setDia("20/05/2025");
        agendamento.setHorario("09:30");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        int expectedColor = context.getResources().getColor(android.R.color.darker_gray);
        int actualColor = viewHolder.txtStatus.getCurrentTextColor();

        assertEquals(expectedColor, actualColor);
        assertEquals("em análise", viewHolder.txtStatus.getText().toString());
    }

    @Test
    public void testOnBindViewHolder_dataCriacaoNula() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("pendente");
        agendamento.setBarbeiroNome("Marcos");
        agendamento.setServico("Corte de Cabelo");
        agendamento.setDia("25/05/2025");
        agendamento.setHorario("11:30");
        agendamento.setDataCriacao(null);

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        assertEquals("Criado em: N/A", viewHolder.txtDataCriacao.getText().toString());
    }

    @Test
    public void testOnBindViewHolder_statusMaiusculoMinusculo() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("PENDENTE");
        agendamento.setBarbeiroNome("Lucas");
        agendamento.setServico("Corte e Barba");
        agendamento.setDia("02/06/2025");
        agendamento.setHorario("15:30");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        int expectedColor = context.getResources().getColor(android.R.color.holo_orange_dark);
        int actualColor = viewHolder.txtStatus.getCurrentTextColor();

        assertEquals(expectedColor, actualColor);
        assertEquals(View.VISIBLE, viewHolder.layoutBotoes.getVisibility());
    }

    @Test
    public void testOnBindViewHolder_statusPrimeiraLetraMaiuscula() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("Confirmado");
        agendamento.setBarbeiroNome("Rafael");
        agendamento.setServico("Corte Moderno");
        agendamento.setDia("05/06/2025");
        agendamento.setHorario("16:45");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        int expectedColor = context.getResources().getColor(android.R.color.holo_green_dark);
        int actualColor = viewHolder.txtStatus.getCurrentTextColor();

        assertEquals(expectedColor, actualColor);
        assertEquals(View.VISIBLE, viewHolder.layoutBotoes.getVisibility());
    }

    @Test
    public void testOnBindViewHolder_formatacoesTextuais() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("pendente");
        agendamento.setBarbeiroNome("Bruno Silva");
        agendamento.setServico("Barba Completa");
        agendamento.setDia("10/06/2025");
        agendamento.setHorario("17:30");

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        assertEquals("Bruno Silva", viewHolder.txtBarbeiroNome.getText().toString());
        assertEquals("Barba Completa", viewHolder.txtServico.getText().toString());
        assertEquals("10/06/2025 - 17:30", viewHolder.txtDiaHorario.getText().toString());
        assertEquals("pendente", viewHolder.txtStatus.getText().toString());
    }

    @Test
    public void testOnBindViewHolder_dataCriacaoValida() {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("pendente");
        agendamento.setBarbeiroNome("André");
        agendamento.setServico("Barba");
        agendamento.setDia("30/05/2025");
        agendamento.setHorario("14:00");

        Calendar cal = Calendar.getInstance();
        cal.set(2025, 3, 15, 10, 30);
        Date dataCriacao = cal.getTime();
        agendamento.setDataCriacao(dataCriacao);

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_agendamento, null, false);
        AgendamentoAdapter.AgendamentoViewHolder viewHolder = new AgendamentoAdapter.AgendamentoViewHolder(view);

        AgendamentoAdapter adapter = new AgendamentoAdapter(Collections.singletonList(agendamento));

        adapter.onBindViewHolder(viewHolder, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dataFormatadaEsperada = "Criado em: " + sdf.format(dataCriacao);
        assertEquals(dataFormatadaEsperada, viewHolder.txtDataCriacao.getText().toString());
    }
}
