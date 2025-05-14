//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.model;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class BarbeiroTest extends TestCase {

    public void testConstrutorComParametros() {
        List<String> dias = Arrays.asList("Segunda", "Quarta");
        List<String> servicos = Arrays.asList("Corte", "Barba");

        Barbeiro barbeiro = new Barbeiro("1", "João", "joao@email.com", "Rua A", dias, servicos);

        assertEquals("1", barbeiro.getId());
        assertEquals("João", barbeiro.getNome());
        assertEquals("joao@email.com", barbeiro.getEmail());
        assertEquals("Rua A", barbeiro.getEndereco());
        assertEquals(dias, barbeiro.getDiasDisponiveis());
        assertEquals(servicos, barbeiro.getServicos());
    }

    public void testSettersAndGetters() {
        Barbeiro barbeiro = new Barbeiro();

        List<String> dias = Arrays.asList("Terça", "Quinta");
        List<String> servicos = Arrays.asList("Corte", "Barba", "Sobrancelha");

        barbeiro.setId("2");
        barbeiro.setNome("Carlos");
        barbeiro.setEmail("carlos@email.com");
        barbeiro.setEndereco("Rua B");
        barbeiro.setDiasDisponiveis(dias);
        barbeiro.setServicos(servicos);

        assertEquals("2", barbeiro.getId());
        assertEquals("Carlos", barbeiro.getNome());
        assertEquals("carlos@email.com", barbeiro.getEmail());
        assertEquals("Rua B", barbeiro.getEndereco());
        assertEquals(dias, barbeiro.getDiasDisponiveis());
        assertEquals(servicos, barbeiro.getServicos());
    }

    public void testGetId() {
        Barbeiro barbeiro = new Barbeiro();
        barbeiro.setId("123");
        assertEquals("123", barbeiro.getId());
    }
}
