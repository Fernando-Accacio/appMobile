//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.model;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class BarbeiroRespostaTest extends TestCase {

    public void testConstrutorComParametros() {
        List<String> servicos = Arrays.asList("Corte", "Barba");
        List<String> dias = Arrays.asList("Segunda", "Sexta");

        BarbeiroResposta resposta = new BarbeiroResposta(servicos, dias);

        assertEquals(servicos, resposta.getServicos());
        assertEquals(dias, resposta.getDiasDisponiveis());
    }

    public void testSettersAndGetters() {
        BarbeiroResposta resposta = new BarbeiroResposta();

        List<String> servicos = Arrays.asList("Sobrancelha", "Corte");
        List<String> dias = Arrays.asList("Terça", "Quinta");

        resposta.setServicos(servicos);
        resposta.setDiasDisponiveis(dias);

        assertEquals(servicos, resposta.getServicos());
        assertEquals(dias, resposta.getDiasDisponiveis());
    }

    public void testGetServicos() {
        BarbeiroResposta resposta = new BarbeiroResposta();
        List<String> servicos = Arrays.asList("Corte");

        resposta.setServicos(servicos);

        assertEquals(servicos, resposta.getServicos());
    }

    public void testGetDiasDisponiveis() {
        BarbeiroResposta resposta = new BarbeiroResposta();
        List<String> dias = Arrays.asList("Quarta");

        resposta.setDiasDisponiveis(dias);

        assertEquals(dias, resposta.getDiasDisponiveis());
    }
}
