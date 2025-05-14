//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.model;

import junit.framework.TestCase;

public class UserTest extends TestCase {

    public void testConstrutorComParametros() {
        User user = new User("João", "joao@email.com", "123456", "99999-0000", "Rua A", "cliente");

        assertEquals("João", user.getNome());
        assertEquals("joao@email.com", user.getEmail());
        assertEquals("123456", user.getSenha());
        assertEquals("99999-0000", user.getPhoneNumber());
        assertEquals("Rua A", user.getEndereco());
        assertEquals("cliente", user.getTipoUsuario());
    }

    public void testSettersAndGetters() {
        User user = new User("", "", "", "", "", "");

        user.setNome("Maria");
        user.setEmail("maria@email.com");
        user.setSenha("abcdef");
        user.setPhoneNumber("88888-1111");
        user.setEndereco("Rua B");
        user.setTipoUsuario("barbeiro");

        assertEquals("Maria", user.getNome());
        assertEquals("maria@email.com", user.getEmail());
        assertEquals("abcdef", user.getSenha());
        assertEquals("88888-1111", user.getPhoneNumber());
        assertEquals("Rua B", user.getEndereco());
        assertEquals("barbeiro", user.getTipoUsuario());
    }

    public void testGetNome() {
        User user = new User("Carlos", "", "", "", "", "");
        assertEquals("Carlos", user.getNome());
    }

    public void testGetEmail() {
        User user = new User("", "teste@email.com", "", "", "", "");
        assertEquals("teste@email.com", user.getEmail());
    }

    public void testGetSenha() {
        User user = new User("", "", "senha123", "", "", "");
        assertEquals("senha123", user.getSenha());
    }

    public void testGetPhoneNumber() {
        User user = new User("", "", "", "12345-6789", "", "");
        assertEquals("12345-6789", user.getPhoneNumber());
    }

    public void testGetEndereco() {
        User user = new User("", "", "", "", "Av. Brasil", "");
        assertEquals("Av. Brasil", user.getEndereco());
    }

    public void testGetTipoUsuario() {
        User user = new User("", "", "", "", "", "cliente");
        assertEquals("cliente", user.getTipoUsuario());
    }
}
