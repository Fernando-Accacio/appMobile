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
import com.example.app.controller.BarbeiroAdapter;
import com.example.app.controller.MainMenu;
import com.example.app.model.Barbeiro;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClientesBarbeiroActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BarbeiroAdapter adapter;
    private List<Barbeiro> listaBarbeiros = new ArrayList<>();
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes_barbeiro);

        inicializarComponentes();
        configurarToolbar();
        configurarRecyclerView();
        carregarBarbeiros();
    }

    private void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recyclerViewBarbeiros);
    }

    private void configurarToolbar() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.openDrawer, R.string.closeDrawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, DashboardClientActivity.class));
                finish();
            }
            else if (id == R.id.nav_profile) {
            }
            else if (id == R.id.nav_logout) {
                realizarLogout();
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
                                        startActivity(new Intent(ClientesBarbeiroActivity.this, MainMenu.class));
                                        finish();
                                        Toast.makeText(ClientesBarbeiroActivity.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ClientesBarbeiroActivity.this, "Falha ao excluir conta. Tente novamente.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ClientesBarbeiroActivity.this, "Falha ao excluir dados na Firestore. Tente novamente.", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Erro ao excluir dados na Firestore", e);
                    });
        } else {
            Toast.makeText(this, "Nenhum usuário logado", Toast.LENGTH_SHORT).show();
        }
    }



    private void configurarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BarbeiroAdapter(listaBarbeiros);
        recyclerView.setAdapter(adapter);
    }

    private void carregarBarbeiros() {
        db = FirebaseFirestore.getInstance();

        db.collection("barbeiro")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Nenhum barbeiro cadastrado", Toast.LENGTH_SHORT).show();
                    } else {
                        listaBarbeiros.clear();

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            String barbeiroId = doc.getId();
                            String nome = doc.getString("nome");
                            String email = doc.getString("email");
                            String endereco = doc.getString("endereco");

                            List<String> diasDisponiveis = (List<String>) doc.get("diasDisponiveis");
                            if (diasDisponiveis == null) {
                                diasDisponiveis = new ArrayList<>();
                            }

                            List<String> servicos = (List<String>) doc.get("servicos");
                            if (servicos == null) {
                                servicos = new ArrayList<>();
                            }

                            Barbeiro barbeiro = new Barbeiro(barbeiroId, nome, email, endereco, diasDisponiveis, servicos);
                            listaBarbeiros.add(barbeiro);
                        }

                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar barbeiros", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Erro ao carregar barbeiros", e);
                });
    }

    private void realizarLogout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, MainMenu.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarBarbeiros();
    }
}