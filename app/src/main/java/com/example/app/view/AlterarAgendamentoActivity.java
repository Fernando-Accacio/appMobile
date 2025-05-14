package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.model.Agendamento;
import com.example.app.model.Barbeiro;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlterarAgendamentoActivity extends AppCompatActivity {

    private LinearLayout llServicos;
    private Spinner spinnerDias;
    private TextView tvBarbeiroNome;
    private Button btnConfirmar;
    private FirebaseFirestore db;
    private String agendamentoId;
    private Agendamento agendamentoAtual;
    private final String TAG = "AlterarAgendamento";
    private Button btnHorarioAtualmenteSelecionado = null;
    private String horarioSelecionado;
    private boolean inicializacaoEmAndamento = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_agendamento);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(AlterarAgendamentoActivity.this, DashboardClientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        llServicos = findViewById(R.id.llServicos);
        spinnerDias = findViewById(R.id.spinnerDias);
        tvBarbeiroNome = findViewById(R.id.tvBarbeiroNome);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        db = FirebaseFirestore.getInstance();

        setTitle("Alterar Agendamento");

        spinnerDias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!inicializacaoEmAndamento) {
                    if (btnHorarioAtualmenteSelecionado != null) {
                        btnHorarioAtualmenteSelecionado.setBackgroundResource(R.drawable.botao_select);
                        btnHorarioAtualmenteSelecionado.setTextColor(getResources().getColor(android.R.color.black));
                        btnHorarioAtualmenteSelecionado = null;
                    }
                    horarioSelecionado = null;
                    configurarHorariosDisponiveis();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (getIntent().hasExtra("id")) {
            agendamentoId = getIntent().getStringExtra("id");
            Log.d(TAG, "Agendamento ID recebido: " + agendamentoId);
            carregarAgendamento(agendamentoId);
        } else {
            Toast.makeText(this, "Erro: ID do agendamento não fornecido.", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnConfirmar.setOnClickListener(v -> salvarAlteracoes());
    }

    private void carregarAgendamento(String agendamentoId) {
        db.collection("agendamentos").whereEqualTo("id", agendamentoId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (var documentSnapshot : queryDocumentSnapshots) {
                            agendamentoAtual = documentSnapshot.toObject(Agendamento.class);

                            if (agendamentoAtual != null) {
                                Log.d(TAG, "Agendamento encontrado: " + agendamentoAtual.toString());
                                String barbeiroId = agendamentoAtual.getBarbeiroId();

                                if (barbeiroId != null && !barbeiroId.isEmpty()) {
                                    Log.d(TAG, "BarbeiroId encontrado: " + barbeiroId);
                                    carregarBarbeiro(barbeiroId);
                                } else {
                                    Log.e(TAG, "BarbeiroId é nulo ou vazio");
                                    Toast.makeText(AlterarAgendamentoActivity.this,
                                            "Erro: Barbeiro não encontrado", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            } else {
                                Log.e(TAG, "Agendamento não encontrado ou inválido.");
                            }
                        }
                    } else {
                        Toast.makeText(AlterarAgendamentoActivity.this, "Agendamento não encontrado.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Agendamento não encontrado no Firestore.");
                    }
                });
    }

    private void carregarBarbeiro(String barbeiroId) {
        if (barbeiroId == null || barbeiroId.isEmpty()) {
            Log.d(TAG, "Barbeiro ID inválido: " + barbeiroId);
            Toast.makeText(AlterarAgendamentoActivity.this, "Erro: Barbeiro ID inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("barbeiro").document(barbeiroId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Barbeiro barbeiro = documentSnapshot.toObject(Barbeiro.class);

                        if (barbeiro != null) {
                            Log.d(TAG, "Barbeiro encontrado: " + barbeiro.getNome());
                            tvBarbeiroNome.setText(barbeiro.getNome());
                            configurarDiasDisponiveis(barbeiro.getDiasDisponiveis());
                            configurarServicos(barbeiro.getServicos(), agendamentoAtual.getServico());
                            configurarHorariosDisponiveis();
                            inicializacaoEmAndamento = false;
                        }
                    } else {
                        Toast.makeText(AlterarAgendamentoActivity.this, "Barbeiro não encontrado.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Barbeiro não encontrado no Firestore.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AlterarAgendamentoActivity.this, "Erro ao carregar barbeiro", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Erro ao carregar barbeiro: " + e.getMessage());
                });
    }

    private void configurarDiasDisponiveis(List<String> diasDisponiveis) {
        if (diasDisponiveis != null && !diasDisponiveis.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, diasDisponiveis);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDias.setAdapter(adapter);

            if (agendamentoAtual != null && agendamentoAtual.getDia() != null) {
                int position = diasDisponiveis.indexOf(agendamentoAtual.getDia());
                if (position >= 0) {
                    spinnerDias.setSelection(position);
                }
            }
        } else {
            List<String> diasPadrao = Arrays.asList("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_spinner_item, diasPadrao);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerDias.setAdapter(adapter);

            if (agendamentoAtual != null && agendamentoAtual.getDia() != null) {
                int position = diasPadrao.indexOf(agendamentoAtual.getDia());
                if (position >= 0) {
                    spinnerDias.setSelection(position);
                }
            }
        }
    }

    private void configurarServicos(List<String> servicosBarbeiro, String servicosAgendamento) {
        llServicos.removeAllViews();

        if (servicosBarbeiro == null || servicosBarbeiro.isEmpty()) {
            Log.d(TAG, "Barbeiro não possui serviços cadastrados");
            Toast.makeText(this, "Barbeiro não possui serviços cadastrados", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<String> servicosSelecionados = new HashSet<>();
        if (servicosAgendamento != null && !servicosAgendamento.isEmpty()) {
            String[] servicos = servicosAgendamento.split(", ");
            servicosSelecionados.addAll(Arrays.asList(servicos));
        }

        for (String servico : servicosBarbeiro) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(servico);
            checkBox.setTextSize(16);
            checkBox.setChecked(servicosSelecionados.contains(servico));
            llServicos.addView(checkBox);
        }
    }

    private void configurarHorariosDisponiveis() {
        String[] horarios = new String[]{"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"};

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHorarios);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        if (agendamentoAtual == null || agendamentoAtual.getBarbeiroId() == null) {
            Toast.makeText(this, "Erro: Barbeiro não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String diaSelecionado = spinnerDias.getSelectedItem().toString();

        db.collection("agendamentos")
                .whereEqualTo("barbeiroId", agendamentoAtual.getBarbeiroId())
                .whereEqualTo("dia", diaSelecionado)
                .whereIn("status", Arrays.asList("pendente", "confirmado"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> horariosReservados = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Agendamento agendamento = document.toObject(Agendamento.class);
                        if (agendamento != null && agendamento.getHorario() != null
                                && !agendamento.getId().equals(agendamentoId)) {
                            horariosReservados.add(agendamento.getHorario());
                        }
                    }

                    HorariosAdapter adapter = new HorariosAdapter(horarios, horariosReservados);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AlterarAgendamentoActivity.this, "Erro ao carregar horários", Toast.LENGTH_SHORT).show();
                });
    }

    private class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder> {
        private final String[] horarios;
        private final List<String> horariosReservados;

        public HorariosAdapter(String[] horarios, List<String> horariosReservados) {
            this.horarios = horarios;
            this.horariosReservados = horariosReservados;
        }

        @NonNull
        @Override
        public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Button btnHorario = new Button(parent.getContext());

            int width = parent.getMeasuredWidth() / 3 - 16;
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(width,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 8, 8, 8);
            btnHorario.setLayoutParams(params);

            return new HorarioViewHolder(btnHorario);
        }

        @Override
        public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
            String horario = horarios[position];
            holder.btnHorario.setText(horario);

            if (horariosReservados.contains(horario)) {
                holder.btnHorario.setEnabled(false);
                holder.btnHorario.setBackgroundColor(getResources().getColor(android.R.color.background_light));
                holder.btnHorario.setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.btnHorario.setBackgroundResource(R.drawable.botao_select);
                holder.btnHorario.setTextColor(getResources().getColor(android.R.color.black));

                holder.btnHorario.setOnClickListener(v -> {
                    if (btnHorarioAtualmenteSelecionado != null) {
                        btnHorarioAtualmenteSelecionado.setBackgroundResource(R.drawable.botao_select);
                        btnHorarioAtualmenteSelecionado.setTextColor(getResources().getColor(android.R.color.black));
                    }

                    v.setBackgroundResource(R.drawable.btn);
                    ((Button) v).setTextColor(getResources().getColor(android.R.color.white));

                    btnHorarioAtualmenteSelecionado = (Button) v;
                    horarioSelecionado = horario;
                });
            }

            if (agendamentoAtual != null &&
                    spinnerDias.getSelectedItem().toString().equals(agendamentoAtual.getDia()) &&
                    horario.equals(agendamentoAtual.getHorario())) {
                holder.btnHorario.setBackgroundResource(R.drawable.btn);
                holder.btnHorario.setTextColor(getResources().getColor(android.R.color.white));
                btnHorarioAtualmenteSelecionado = holder.btnHorario;
                horarioSelecionado = horario;
            }
        }

        @Override
        public int getItemCount() {
            return horarios.length;
        }

        class HorarioViewHolder extends RecyclerView.ViewHolder {
            final Button btnHorario;

            HorarioViewHolder(Button button) {
                super(button);
                this.btnHorario = button;
            }
        }
    }

    private void salvarAlteracoes() {
        if (agendamentoAtual == null) {
            Toast.makeText(this, "Erro: Agendamento não carregado corretamente", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> servicosSelecionados = new ArrayList<>();
        for (int i = 0; i < llServicos.getChildCount(); i++) {
            View view = llServicos.getChildAt(i);
            if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                if (checkBox.isChecked()) {
                    servicosSelecionados.add(checkBox.getText().toString());
                }
            }
        }

        if (servicosSelecionados.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione pelo menos um serviço", Toast.LENGTH_SHORT).show();
            return;
        }

        String diaSelecionado = spinnerDias.getSelectedItem().toString();

        if (horarioSelecionado == null || horarioSelecionado.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um horário", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean houveAlteracoes = !diaSelecionado.equals(agendamentoAtual.getDia()) ||
                !horarioSelecionado.equals(agendamentoAtual.getHorario()) ||
                !String.join(", ", servicosSelecionados).equals(agendamentoAtual.getServico());

        String novoStatus = houveAlteracoes ? "pendente" : agendamentoAtual.getStatus();

        db.collection("agendamentos").whereEqualTo("id", agendamentoId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (var documentSnapshot : queryDocumentSnapshots) {
                            db.collection("agendamentos").document(documentSnapshot.getId())
                                    .update(
                                            "dia", diaSelecionado,
                                            "horario", horarioSelecionado,
                                            "servico", String.join(", ", servicosSelecionados),
                                            "status", novoStatus,
                                            "dataAlteracao", FieldValue.serverTimestamp()
                                    )
                                    .addOnSuccessListener(aVoid -> {
                                        String mensagem = houveAlteracoes ?
                                                "Agendamento alterado e aguardando confirmação do barbeiro" :
                                                "Agendamento atualizado";

                                        Toast.makeText(AlterarAgendamentoActivity.this,
                                                mensagem, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AlterarAgendamentoActivity.this, DashboardClientActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AlterarAgendamentoActivity.this,
                                                "Erro ao atualizar agendamento", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Erro ao atualizar agendamento: " + e.getMessage());
                                    });
                            break;
                        }
                    } else {
                        Toast.makeText(AlterarAgendamentoActivity.this,
                                "Agendamento não encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AlterarAgendamentoActivity.this,
                            "Erro ao buscar agendamento", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao buscar agendamento: " + e.getMessage());
                });
    }
}