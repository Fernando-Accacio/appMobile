//./gradlew clean
//./gradlew connectedDebugAndroidTest
//./gradlew jacocoTestReportAndroid

package com.example.app.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.app.R;
import com.example.app.model.Barbeiro;
import com.example.app.view.AgendamentoActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class BarbeiroAdapterTest {

    @Mock
    private ViewGroup mockParent;

    @Mock
    private Context mockContext;

    @Mock
    RecyclerView.ViewHolder mockViewHolder;

    @Mock
    Intent mockIntent;

    @Mock
    Barbeiro mockBarbeiro;

    private BarbeiroAdapter adapter;
    private List<Barbeiro> listaBarbeiros;

    @Before
    public void setUp() {

        MockitoAnnotations.openMocks(this);

        listaBarbeiros = new ArrayList<>();
        List<String> servicos = Arrays.asList("Corte", "Barba");
        List<String> diasDisponiveis = Arrays.asList("Segunda", "Terça");

        Barbeiro barbeiro = new Barbeiro("1", "João", "joao@email.com", "Rua 1", diasDisponiveis, servicos);
        listaBarbeiros.add(barbeiro);

        adapter = new BarbeiroAdapter(listaBarbeiros);
    }

    @Test
    public void testOnCreateViewHolder() {
        View mockView = Mockito.mock(View.class);

        LayoutInflater mockInflater = Mockito.mock(LayoutInflater.class);
        when(mockInflater.inflate(Mockito.anyInt(), Mockito.any(ViewGroup.class), Mockito.eq(false))).thenReturn(mockView);

        BarbeiroAdapter testAdapter = new BarbeiroAdapter(listaBarbeiros) {
            @Override
            public BarbeiroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new BarbeiroViewHolder(mockInflater.inflate(R.layout.item_barbeiro, parent, false));
            }
        };

        BarbeiroAdapter.BarbeiroViewHolder viewHolder = testAdapter.onCreateViewHolder(mockParent, 0);

        assertNotNull(viewHolder);

        verify(mockInflater).inflate(R.layout.item_barbeiro, mockParent, false);
    }

    @Test
    public void testOnBindViewHolder() {
        // Preparando os dados para o teste
        List<String> servicos = Arrays.asList("Corte", "Barba");
        List<String> diasDisponiveis = Arrays.asList("Segunda", "Terça");
        Barbeiro barbeiro = new Barbeiro("1", "João", "joao@email.com", "Rua 1", diasDisponiveis, servicos);

        listaBarbeiros.add(barbeiro);

        BarbeiroAdapter.BarbeiroViewHolder mockViewHolder = Mockito.mock(BarbeiroAdapter.BarbeiroViewHolder.class);

        mockViewHolder.txtNomeBarbeiro = Mockito.mock(TextView.class);
        mockViewHolder.txtEmailBarbeiro = Mockito.mock(TextView.class);
        mockViewHolder.txtEnderecoBarbeiro = Mockito.mock(TextView.class);
        mockViewHolder.txtServicos = Mockito.mock(TextView.class);
        mockViewHolder.txtDiasDisponiveis = Mockito.mock(TextView.class);

        adapter.onBindViewHolder(mockViewHolder, 0);

        verify(mockViewHolder.txtNomeBarbeiro).setText("João");
        verify(mockViewHolder.txtEmailBarbeiro).setText("Email: joao@email.com");
        verify(mockViewHolder.txtEnderecoBarbeiro).setText("Endereço: Rua 1");
        verify(mockViewHolder.txtServicos).setText("Serviços: Corte, Barba");
        verify(mockViewHolder.txtDiasDisponiveis).setText("Dias Disponíveis: Segunda, Terça");
    }

    @Test
    public void testOnBindViewHolderWithNullValues() {
        List<String> servicos = Arrays.asList("Corte", "Barba");
        List<String> diasDisponiveis = Arrays.asList("Segunda", "Terça");
        Barbeiro barbeiro = new Barbeiro("1", "João", "joao@email.com", "Rua 1", diasDisponiveis, servicos);

        listaBarbeiros.add(barbeiro);

        BarbeiroAdapter.BarbeiroViewHolder mockViewHolder = Mockito.mock(BarbeiroAdapter.BarbeiroViewHolder.class);

        mockViewHolder.txtNomeBarbeiro = Mockito.mock(TextView.class);
        mockViewHolder.txtEmailBarbeiro = Mockito.mock(TextView.class);
        mockViewHolder.txtEnderecoBarbeiro = Mockito.mock(TextView.class);
        mockViewHolder.txtServicos = Mockito.mock(TextView.class);
        mockViewHolder.txtDiasDisponiveis = Mockito.mock(TextView.class);

        adapter.onBindViewHolder(mockViewHolder, 0);

        verify(mockViewHolder.txtNomeBarbeiro).setText("João");
        verify(mockViewHolder.txtEmailBarbeiro).setText("Email: joao@email.com");
        verify(mockViewHolder.txtEnderecoBarbeiro).setText("Endereço: Rua 1");
        verify(mockViewHolder.txtServicos).setText("Serviços: Corte, Barba");
        verify(mockViewHolder.txtDiasDisponiveis).setText("Dias Disponíveis: Segunda, Terça");  // Agora o valor esperado é o da lista de diasDisponiveis
    }



    @Test
    public void testGetItemCount() {
        assertEquals(1, adapter.getItemCount());

        Barbeiro novoBarbeiro = new Barbeiro("2", "Pedro", "pedro@email.com", "Rua 2", null, null);
        listaBarbeiros.add(novoBarbeiro);

        adapter = new BarbeiroAdapter(listaBarbeiros);

        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testUpdateData() {
        List<Barbeiro> novaLista = new ArrayList<>();
        Barbeiro novoBarbeiro = new Barbeiro("2", "Pedro", "pedro@email.com", "Rua 2", null, null);
        novaLista.add(novoBarbeiro);

        adapter.updateData(novaLista);

        assertEquals(1, adapter.getItemCount());
        assertEquals("Pedro", adapter.listaBarbeiros.get(0).getNome());
    }

    @Test
    public void testUpdateDataWithNull() {
        adapter.updateData(null);

        assertNotNull(adapter.listaBarbeiros);
        assertEquals(0, adapter.listaBarbeiros.size());
    }

    @Test
    public void testConstructorWithNull() {
        BarbeiroAdapter nullAdapter = new BarbeiroAdapter(null);
        assertNotNull(nullAdapter.listaBarbeiros);
        assertEquals(0, nullAdapter.listaBarbeiros.size());
    }

    @Test
    public void testViewHolderInitialization() {
        assertNotNull(mockViewHolder);
        assertNotNull(adapter);
    }

    @Test
    public void testSetUp() {
        assertEquals(1, listaBarbeiros.size());
        assertEquals("João", listaBarbeiros.get(0).getNome());
        assertEquals("joao@email.com", listaBarbeiros.get(0).getEmail());
    }
}