//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.model;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Date;

public class AgendamentoTest extends TestCase {

    @Test
    public void testConstrutorEGetters() {
        String barbeiroId = "barbeiro123";
        String barbeiroNome = "João Barbeiro";
        String clienteId = "cliente456";
        String clienteNome = "Maria Cliente";
        String dia = "2025-04-25";
        String horario = "14:30";
        String servico = "Corte de cabelo";
        String status = "Pendente";

        Agendamento agendamento = new Agendamento(
                barbeiroId, barbeiroNome, clienteId, clienteNome,
                dia, horario, servico, status
        );

        assertEquals(barbeiroId, agendamento.getBarbeiroId());
        assertEquals(barbeiroNome, agendamento.getBarbeiroNome());
        assertEquals(clienteId, agendamento.getClienteId());
        assertEquals(clienteNome, agendamento.getClienteNome());
        assertEquals(dia, agendamento.getDia());
        assertEquals(horario, agendamento.getHorario());
        assertEquals(servico, agendamento.getServico());
        assertEquals(status, agendamento.getStatus());
    }


    @Test
    public void testSettersEGetters() {
        Agendamento agendamento = new Agendamento();

        agendamento.setId("agendamento001");
        agendamento.setBarbeiroId("barbeiro789");
        agendamento.setBarbeiroNome("Lucas");
        agendamento.setClienteId("cliente321");
        agendamento.setClienteNome("Pedro");
        agendamento.setDia("2025-04-30");
        agendamento.setHorario("10:00");
        agendamento.setServico("Barba");
        agendamento.setStatus("Confirmado");

        Date now = new Date();
        agendamento.setDataCriacao(now);

        assertEquals("agendamento001", agendamento.getId());
        assertEquals("barbeiro789", agendamento.getBarbeiroId());
        assertEquals("Lucas", agendamento.getBarbeiroNome());
        assertEquals("cliente321", agendamento.getClienteId());
        assertEquals("Pedro", agendamento.getClienteNome());
        assertEquals("2025-04-30", agendamento.getDia());
        assertEquals("10:00", agendamento.getHorario());
        assertEquals("Barba", agendamento.getServico());
        assertEquals("Confirmado", agendamento.getStatus());
        assertEquals(now, agendamento.getDataCriacao());
    }
}
