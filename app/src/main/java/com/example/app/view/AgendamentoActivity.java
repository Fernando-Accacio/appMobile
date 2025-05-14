package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {
    private LinearLayout llServicos;
    private Spinner spinnerDias;
    private Button btnConfirmar;
    private Button btnHorarioSelecionado;
    private String barbeiroId, nomeBarbeiro, horarioSelecionado;
    private FirebaseFirestore db;
    private GridLayout gridLayoutHorarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(AgendamentoActivity.this, ClientesBarbeiroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        llServicos = findViewById(R.id.llServicos);
        spinnerDias = findViewById(R.id.spinnerDias);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        gridLayoutHorarios = findViewById(R.id.gridLayoutHorarios);
        db = FirebaseFirestore.getInstance();

        Barbeiro barbeiro = (Barbeiro) getIntent().getSerializableExtra("barbeiro");

        if (barbeiro == null) {
            Toast.makeText(this, "Erro ao carregar dados do barbeiro.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        barbeiroId = barbeiro.getId();
        nomeBarbeiro = barbeiro.getNome();
        String[] servicos = barbeiro.getServicos().toArray(new String[0]);
        String[] diasDisponiveis = barbeiro.getDiasDisponiveis().toArray(new String[0]);

        if (servicos == null || servicos.length == 0) {
            servicos = new String[]{"Nenhum serviço disponível"};
        }
        if (diasDisponiveis == null || diasDisponiveis.length == 0) {
            diasDisponiveis = new String[]{"Nenhum dia disponível"};
        }

        for (String servico : servicos) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(servico);
            llServicos.addView(checkBox);
        }

        spinnerDias.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, diasDisponiveis));

        // *** Mudança Importante ***
        // Só chama criarBotoesDeHorario() quando mudar o spinner
        spinnerDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                criarBotoesDeHorario();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnConfirmar.setOnClickListener(v -> salvarAgendamento());
    }

    private void criarBotoesDeHorario() {
        String[] horarios = new String[] {
                "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00", "17:30"
        };

        gridLayoutHorarios.removeAllViews();
        gridLayoutHorarios.setColumnCount(3);

        String diaSelecionado = spinnerDias.getSelectedItem().toString();
        btnHorarioSelecionado = null;
        horarioSelecionado = null;

        db.collection("agendamentos")
                .whereEqualTo("barbeiroId", barbeiroId)
                .whereEqualTo("dia", diaSelecionado)
                .whereIn("status", List.of("pendente", "confirmado"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> horariosReservados = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Agendamento agendamento = document.toObject(Agendamento.class);
                        if (agendamento != null && agendamento.getHorario() != null) {
                            horariosReservados.add(agendamento.getHorario());
                        }
                    }

                    criarBotoesDisponiveis(horarios, horariosReservados);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AgendamentoActivity.this, "Erro ao buscar horários: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    criarBotoesDisponiveis(horarios, new ArrayList<>());
                });
    }

    private void criarBotoesDisponiveis(String[] horarios, List<String> horariosReservados) {
        for (String horario : horarios) {
            Button btnHorario = new Button(this);
            btnHorario.setText(horario);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);

            btnHorario.setLayoutParams(params);

            if (horariosReservados.contains(horario)) {
                btnHorario.setEnabled(false);
                btnHorario.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btnHorario.setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                btnHorario.setBackgroundResource(R.drawable.botao_select);
                btnHorario.setTextColor(getResources().getColor(android.R.color.black));

                btnHorario.setOnClickListener(v -> {
                    if (btnHorarioSelecionado != null) {
                        btnHorarioSelecionado.setBackgroundResource(R.drawable.botao_select);
                        btnHorarioSelecionado.setTextColor(getResources().getColor(android.R.color.black));
                    }

                    btnHorarioSelecionado = btnHorario;
                    horarioSelecionado = horario;

                    btnHorarioSelecionado.setBackgroundResource(R.drawable.btn);
                    btnHorarioSelecionado.setTextColor(getResources().getColor(android.R.color.white));

                    Toast.makeText(AgendamentoActivity.this, "Horário selecionado: " + horarioSelecionado, Toast.LENGTH_SHORT).show();
                });
            }

            gridLayoutHorarios.addView(btnHorario);
        }
    }

    private void salvarAgendamento() {
        List<String> servicosSelecionados = new ArrayList<>();
        for (int i = 0; i < llServicos.getChildCount(); i++) {
            CheckBox checkBox = (CheckBox) llServicos.getChildAt(i);
            if (checkBox.isChecked()) {
                servicosSelecionados.add(checkBox.getText().toString());
            }
        }

        if (servicosSelecionados.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione pelo menos um serviço.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horarioSelecionado == null || horarioSelecionado.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um horário.", Toast.LENGTH_SHORT).show();
            return;
        }

        String diaSelecionado = spinnerDias.getSelectedItem().toString();
        String clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String nomeCliente = "Nome do Cliente";

        Agendamento agendamento = new Agendamento(
                barbeiroId,
                nomeBarbeiro,
                clienteId,
                nomeCliente,
                diaSelecionado,
                horarioSelecionado,
                String.join(", ", servicosSelecionados),
                "pendente"
        );

        db.collection("agendamentos")
                .add(agendamento)
                .addOnSuccessListener(documentReference -> {
                    agendamento.setId(documentReference.getId());
                    documentReference.update("id", documentReference.getId());

                    Intent intent = new Intent(AgendamentoActivity.this, ConfirmationAgendamentoActivity.class);
                    intent.putExtra("BARBEIRO_NOME", nomeBarbeiro);
                    intent.putExtra("SERVICO", String.join(", ", servicosSelecionados));
                    intent.putExtra("DATA_HORA", diaSelecionado + " - " + horarioSelecionado);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao agendar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
