package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.controller.AgendamentoAdapter;
import com.example.app.controller.MainMenu;
import com.example.app.model.Agendamento;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DashboardClientActivity extends AppCompatActivity {

    private RecyclerView recyclerViewAgendamentos;
    private AgendamentoAdapter agendamentoAdapter;
    private FirebaseFirestore db;
    private String clienteId;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_client);

        recyclerViewAgendamentos = findViewById(R.id.recyclerViewAgendamentos);
        recyclerViewAgendamentos.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        clienteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        carregarAgendamentos();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Início selecionado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardClientActivity.this, DashboardClientActivity.class));
                finish();
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(DashboardClientActivity.this, ClientesBarbeiroActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DashboardClientActivity.this, MainMenu.class));
                finish();
            }else if (id == R.id.excluir_conta) {
            excluirConta();
        }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void excluirConta() {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza de que deseja excluir sua conta?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    deletarConta();
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deletarConta() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = user.getUid();

            db.collection("usuarios").document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        user.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(DashboardClientActivity.this, MainMenu.class));
                                        finish();
                                        Toast.makeText(DashboardClientActivity.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(DashboardClientActivity.this, "Falha ao excluir conta. Tente novamente.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DashboardClientActivity.this, "Falha ao excluir dados na Firestore. Tente novamente.", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Erro ao excluir dados na Firestore", e);
                    });
        } else {
            Toast.makeText(this, "Nenhum usuário logado", Toast.LENGTH_SHORT).show();
        }
    }


    private void carregarAgendamentos() {
        db.collection("agendamentos")
                .whereEqualTo("clienteId", clienteId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Agendamento> agendamentos = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Agendamento agendamento = document.toObject(Agendamento.class);
                            agendamentos.add(agendamento);
                        }

                        ordenarAgendamentosPorStatus(agendamentos);

                        agendamentoAdapter = new AgendamentoAdapter(agendamentos);
                        recyclerViewAgendamentos.setAdapter(agendamentoAdapter);
                    } else {
                        Toast.makeText(DashboardClientActivity.this, "Erro ao carregar agendamentos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ordenarAgendamentosPorStatus(List<Agendamento> agendamentos) {
        agendamentos.sort(new Comparator<Agendamento>() {
            @Override
            public int compare(Agendamento o1, Agendamento o2) {
                String status1 = o1.getStatus();
                String status2 = o2.getStatus();

                if (status1.equals("confirmado") && status2.equals("pendente")) {
                    return -1;
                } else if (status1.equals("confirmado") && status2.equals("cancelado")) {
                    return -1;
                } else if (status1.equals("pendente") && status2.equals("confirmado")) {
                    return 1;
                } else if (status1.equals("pendente") && status2.equals("cancelado")) {
                    return -1;
                } else if (status1.equals("cancelado") && status2.equals("confirmado")) {
                    return 1;
                } else if (status1.equals("cancelado") && status2.equals("pendente")) {
                    return 1;
                }
                return 0;
            }
        });
    }
}
