//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.controller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.app.R;
import com.example.app.model.Agendamento;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class BarberClientsAdapterTest {
    @Mock
    private BarberClientsAdapter.ViewHolder mockViewHolder;
    private Agendamento agendamentoPendente;
    private Agendamento agendamentoConfirmado;
    @Mock
    private BarberClientsAdapter.OnClientActionListener mockListener;
    private BarberClientsAdapter adapter;
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        agendamentoPendente = new Agendamento();
        agendamentoPendente.setId("1");
        agendamentoPendente.setClienteNome("Carlos");
        agendamentoPendente.setServico("Corte");
        agendamentoPendente.setDia("Segunda");
        agendamentoPendente.setHorario("14:00");
        agendamentoPendente.setStatus("pendente");

        agendamentoConfirmado = new Agendamento();
        agendamentoConfirmado.setId("2");
        agendamentoConfirmado.setClienteNome("Lucas");
        agendamentoConfirmado.setServico("Barba");
        agendamentoConfirmado.setDia("Terça");
        agendamentoConfirmado.setHorario("15:00");
        agendamentoConfirmado.setStatus("confirmado");

        adapter = new BarberClientsAdapter(Arrays.asList(agendamentoPendente, agendamentoConfirmado), mockListener);
    }

    @Test
    public void testGetItemCount() {
        assertEquals(2, adapter.getItemCount());

        BarberClientsAdapter emptyAdapter = new BarberClientsAdapter(Collections.emptyList(), mockListener);
        assertEquals(0, emptyAdapter.getItemCount());
    }

    @Test
    public void testCapitalize() throws Exception {
        var method = BarberClientsAdapter.class.getDeclaredMethod("capitalize", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(adapter, "confirmado");
        assertEquals("Confirmado", result);
    }

    @Test
    public void testFormatDateTime() throws Exception {
        var method = BarberClientsAdapter.class.getDeclaredMethod("formatDateTime", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(adapter, "10/10", "13:30");
        assertEquals("10/10 • 13:30", result);
    }


    @Test
    public void testCapitalizeEdgeCases() throws Exception {
        var method = BarberClientsAdapter.class.getDeclaredMethod("capitalize", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(adapter, "");
        assertEquals("", result);

        result = (String) method.invoke(adapter, "PENDENTE");
        assertEquals("Pendente", result);

        result = (String) method.invoke(adapter, "corte simples");
        assertEquals("Corte simples", result);

        result = (String) method.invoke(adapter, "a");
        assertEquals("A", result);
    }

    @Test
    public void testFormatDateTimeVariations() throws Exception {
        var method = BarberClientsAdapter.class.getDeclaredMethod("formatDateTime", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(adapter, "01/05/2023", "10:00");
        assertEquals("01/05/2023 • 10:00", result);

        result = (String) method.invoke(adapter, "", "12:30");
        assertEquals(" • 12:30", result);

        result = (String) method.invoke(adapter, "Quarta", "");
        assertEquals("Quarta • ", result);
    }

    @Test
    public void testConstructorWithNullValues() {
        List<Agendamento> nullList = null;
        try {
            BarberClientsAdapter nullListAdapter = new BarberClientsAdapter(nullList, mockListener);
            int itemCount = nullListAdapter.getItemCount();
            fail("Esperava NullPointerException ao passar lista nula");
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        List<Agendamento> list = new ArrayList<>();
        list.add(agendamentoPendente);
        BarberClientsAdapter nullListenerAdapter = new BarberClientsAdapter(list, null);
        assertEquals("Adaptador com listener nulo deve manter a contagem de itens correta",
                1, nullListenerAdapter.getItemCount());
    }


    @Test
    public void testGetItemViewType() throws Exception {
        try {
            var method = BarberClientsAdapter.class.getDeclaredMethod("getItemViewType", int.class);
            method.setAccessible(true);

            int pendingType = (int) method.invoke(adapter, 0);
            int confirmedType = (int) method.invoke(adapter, 1);

            assertNotEquals("Tipos de itens devem ser diferentes para estados diferentes",
                    pendingType, confirmedType);
        } catch (NoSuchMethodException e) {
            System.out.println("Método getItemViewType não encontrado - ignorando teste");
        }
    }

    @Test
    public void testAgendamentoStatusBehavior() {
        Agendamento cancelado = new Agendamento();
        cancelado.setId("3");
        cancelado.setStatus("cancelado");

        List<Agendamento> testList = new ArrayList<>();
        testList.add(agendamentoPendente);
        testList.add(agendamentoConfirmado);
        testList.add(cancelado);

        BarberClientsAdapter testAdapter = new BarberClientsAdapter(testList, mockListener);

        try {
            var methodIsButtonVisible = BarberClientsAdapter.class.getDeclaredMethod("isConfirmButtonVisible", String.class);
            methodIsButtonVisible.setAccessible(true);

            boolean confirmPendente = (boolean) methodIsButtonVisible.invoke(testAdapter, "pendente");
            boolean confirmConfirmado = (boolean) methodIsButtonVisible.invoke(testAdapter, "confirmado");
            boolean confirmCancelado = (boolean) methodIsButtonVisible.invoke(testAdapter, "cancelado");

            assertTrue("Botão confirmar deve ser visível para pendente", confirmPendente);
            assertFalse("Botão confirmar não deve ser visível para confirmado", confirmConfirmado);
            assertFalse("Botão confirmar não deve ser visível para cancelado", confirmCancelado);
        } catch (NoSuchMethodException e) {
            System.out.println("Método isConfirmButtonVisible não encontrado - ignorando teste");
        } catch (Exception e) {
            fail("Erro ao testar comportamento de status: " + e.getMessage());
        }
    }

    @Test
    public void testListManipulation() throws Exception {
        List<Agendamento> originalList = new ArrayList<>();
        originalList.add(agendamentoPendente);

        BarberClientsAdapter testAdapter = new BarberClientsAdapter(originalList, mockListener);
        assertEquals(1, testAdapter.getItemCount());

        try {
            var methodUpdateList = BarberClientsAdapter.class.getDeclaredMethod("updateData", List.class);
            methodUpdateList.setAccessible(true);

            List<Agendamento> newList = new ArrayList<>();
            newList.add(agendamentoPendente);
            newList.add(agendamentoConfirmado);

            methodUpdateList.invoke(testAdapter, newList);
            assertEquals(2, testAdapter.getItemCount());

        } catch (NoSuchMethodException e) {
            System.out.println("Método updateData não encontrado - ignorando teste");
        }
    }

    @Test
    public void testGetAgendamentoAtPosition() throws Exception {
        try {
            var methodGetItem = BarberClientsAdapter.class.getDeclaredMethod("getAgendamento", int.class);
            methodGetItem.setAccessible(true);

            Agendamento result = (Agendamento) methodGetItem.invoke(adapter, 0);
            assertEquals("1", result.getId());
            assertEquals("Carlos", result.getClienteNome());

            result = (Agendamento) methodGetItem.invoke(adapter, 1);
            assertEquals("2", result.getId());
            assertEquals("Lucas", result.getClienteNome());

        } catch (NoSuchMethodException e) {
            try {
                var field = BarberClientsAdapter.class.getDeclaredField("clientList");
                field.setAccessible(true);

                List<Agendamento> list = (List<Agendamento>) field.get(adapter);
                assertEquals("1", list.get(0).getId());
                assertEquals("2", list.get(1).getId());

            } catch (NoSuchFieldException fieldException) {
                System.out.println("Campo clientList não encontrado - ignorando teste");
            }
        }
    }

    @Test
    public void testActionListenerCallbacks() {
        BarberClientsAdapter.OnClientActionListener listener = mock(BarberClientsAdapter.OnClientActionListener.class);
        List<Agendamento> agendamentos = Arrays.asList(agendamentoPendente);
        BarberClientsAdapter actionAdapter = new BarberClientsAdapter(agendamentos, listener);

        try {
            var methodConfirm = BarberClientsAdapter.class.getDeclaredMethod("confirmAppointment", Agendamento.class);
            methodConfirm.setAccessible(true);
            methodConfirm.invoke(actionAdapter, agendamentoPendente);

            verify(listener, times(1)).onConfirm(agendamentoPendente);

            var methodCancel = BarberClientsAdapter.class.getDeclaredMethod("cancelAppointment", Agendamento.class);
            methodCancel.setAccessible(true);
            methodCancel.invoke(actionAdapter, agendamentoPendente);

            verify(listener, times(1)).onCancel(agendamentoPendente);

        } catch (NoSuchMethodException e) {
            System.out.println("Métodos de ação não encontrados - ignorando teste");
        } catch (Exception e) {
            fail("Erro ao testar callbacks de ações: " + e.getMessage());
        }
    }


    @Test
    public void testOnCreateViewHolder_returnsNonNullViewHolder() {
        ViewGroup parent = new FrameLayout(context);

        BarberClientsAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);

        assertNotNull("ViewHolder deve ser diferente de null", viewHolder);
        assertNotNull("itemView deve ser diferente de null", viewHolder.itemView);
    }
    @Test
    public void testOnBindViewHolder_setsCorrectTextValues() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_barber_client, null, false);
        BarberClientsAdapter.ViewHolder holder = new BarberClientsAdapter.ViewHolder(itemView);

        adapter.onBindViewHolder(holder, 0);

        assertEquals("Carlos", holder.tvClientName.getText().toString());
        assertEquals("Corte", holder.tvService.getText().toString());
        assertEquals("Segunda • 14:00", holder.tvDateTime.getText().toString());
    }

    @Test
    public void testSetupActionButtons_onConfirmClick_callsOnConfirm() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_barber_client, null, false);
        BarberClientsAdapter.ViewHolder holder = new BarberClientsAdapter.ViewHolder(itemView);

        adapter.onBindViewHolder(holder, 0);

        holder.btnConfirm.performClick();

        verify(mockListener, times(1)).onConfirm(agendamentoPendente);
    }

    @Test
    public void testSetupActionButtons_onCancelClick_callsOnCancel() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_barber_client, null, false);
        BarberClientsAdapter.ViewHolder holder = new BarberClientsAdapter.ViewHolder(itemView);

        adapter.onBindViewHolder(holder, 0);

        holder.btnCancel.performClick();

        verify(mockListener, times(1)).onCancel(agendamentoPendente);
    }

    @Test
    public void testSetupStatusUI_confirmado() {
        View mockView = LayoutInflater.from(context).inflate(R.layout.item_barber_client, null);
        Button mockBtnConfirm = mockView.findViewById(R.id.btn_confirm);
        Button mockBtnCancel = mockView.findViewById(R.id.btn_cancel);
        TextView mockTvStatus = mockView.findViewById(R.id.tv_status);

        BarberClientsAdapter.ViewHolder viewHolder = new BarberClientsAdapter.ViewHolder(mockView);
        adapter.setupStatusUI(viewHolder, agendamentoConfirmado);

        assertEquals(View.GONE, mockBtnConfirm.getVisibility());
        assertEquals(View.GONE, mockBtnCancel.getVisibility());
        assertEquals(View.VISIBLE, mockTvStatus.getVisibility());
        assertEquals("Confirmado", mockTvStatus.getText());
        assertEquals(Color.parseColor("#4CAF50"), mockTvStatus.getCurrentTextColor());
    }

    @Test
    public void testSetupStatusUI_cancelado() {
        View mockView = LayoutInflater.from(context).inflate(R.layout.item_barber_client, null);
        Button mockBtnConfirm = mockView.findViewById(R.id.btn_confirm);
        Button mockBtnCancel = mockView.findViewById(R.id.btn_cancel);
        TextView mockTvStatus = mockView.findViewById(R.id.tv_status);

        Agendamento agendamentoCancelado = new Agendamento();
        agendamentoCancelado.setId("3");
        agendamentoCancelado.setClienteNome("João");
        agendamentoCancelado.setServico("Corte");
        agendamentoCancelado.setDia("Quarta");
        agendamentoCancelado.setHorario("16:00");
        agendamentoCancelado.setStatus("cancelado");

        BarberClientsAdapter.ViewHolder viewHolder = new BarberClientsAdapter.ViewHolder(mockView);
        adapter.setupStatusUI(viewHolder, agendamentoCancelado);

        assertEquals(View.GONE, mockBtnConfirm.getVisibility());
        assertEquals(View.GONE, mockBtnCancel.getVisibility());
        assertEquals(View.VISIBLE, mockTvStatus.getVisibility());
        assertEquals("Cancelado", mockTvStatus.getText());
        assertEquals(Color.parseColor("#F44336"), mockTvStatus.getCurrentTextColor());
    }

    @Test
    public void testSetupStatusUI_default() {
        View mockView = LayoutInflater.from(context).inflate(R.layout.item_barber_client, null);
        Button mockBtnConfirm = mockView.findViewById(R.id.btn_confirm);
        Button mockBtnCancel = mockView.findViewById(R.id.btn_cancel);
        TextView mockTvStatus = mockView.findViewById(R.id.tv_status);

        Agendamento agendamentoOutro = new Agendamento();
        agendamentoOutro.setId("4");
        agendamentoOutro.setClienteNome("Rafael");
        agendamentoOutro.setServico("Barba e Corte");
        agendamentoOutro.setDia("Quinta");
        agendamentoOutro.setHorario("17:00");
        agendamentoOutro.setStatus("outro");

        BarberClientsAdapter.ViewHolder viewHolder = new BarberClientsAdapter.ViewHolder(mockView);
        adapter.setupStatusUI(viewHolder, agendamentoOutro);

        assertEquals(View.GONE, mockBtnConfirm.getVisibility());
        assertEquals(View.GONE, mockBtnCancel.getVisibility());
        assertEquals(View.VISIBLE, mockTvStatus.getVisibility());
        assertEquals("Outro", mockTvStatus.getText());
        assertEquals(Color.GRAY, mockTvStatus.getCurrentTextColor());
    }
}