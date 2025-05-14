package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.app.R;
import com.example.app.controller.MainMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BarberDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_CREATE_STORE = 1;
    private static final String TAG = "BarberDashboard";

    private TextView txtNome, tvUserEmail, txtEnderecoLoja, txtServicos, txtDiasFuncionamento;
    private Button btnLoja;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_dashboard);

        try {
            inicializarComponentes();
            configurarFirebase();
            configurarNavigationDrawer();
            carregarDadosUsuario();
            verificarDadosBarbearia();
        } catch (Exception e) {
            Log.e(TAG, "Erro no onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao carregar o painel", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        txtNome = findViewById(R.id.txtNome);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        txtEnderecoLoja = findViewById(R.id.txtEnderecoLoja);
        txtServicos = findViewById(R.id.txtServicos);
        txtDiasFuncionamento = findViewById(R.id.txtDiasFuncionamento);

        btnLoja = findViewById(R.id.btnLoja);
        btnLoja.setOnClickListener(v -> gerenciarBarbearia());
    }

    private void configurarFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void configurarNavigationDrawer() {
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.openDrawer, R.string.closeDrawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void gerenciarBarbearia() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("barbeiro").document(user.getUid())
                    .get()
                    .addOnSuccessListener(document -> {
                        Intent intent = new Intent(this, CreateStoreActivity.class);
                        if (document.exists()) {
                            intent.putExtra("editing", true);
                        }
                        startActivityForResult(intent, REQUEST_CODE_CREATE_STORE);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erro ao verificar loja existente", e);
                    });
        }
    }

    private void carregarDadosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "Não informado");

            db.collection("usuarios").document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                txtNome.setText(document.getString("nome"));
                            }
                        } else {
                            Log.e(TAG, "Erro ao carregar dados do usuário", task.getException());
                        }
                    });
        }
    }

    private void verificarDadosBarbearia() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("barbeiro").document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            runOnUiThread(() -> {
                                if (document != null && document.exists()) {
                                    exibirDadosBarbearia(document);
                                    btnLoja.setText("ALTERAR LOJA");
                                } else {
                                    btnLoja.setText("CRIAR LOJA");
                                    Toast.makeText(this, "Complete seu cadastro profissional", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }
    }

    private void exibirDadosBarbearia(DocumentSnapshot document) {
        try {
            txtEnderecoLoja.setText(document.getString("endereco"));

            List<String> servicos = (List<String>) document.get("servicos");
            txtServicos.setText(servicos != null && !servicos.isEmpty() ?
                    TextUtils.join(", ", servicos) : "Nenhum serviço cadastrado");

            List<String> diasDisponiveis = (List<String>) document.get("diasDisponiveis");
            txtDiasFuncionamento.setText(diasDisponiveis != null && !diasDisponiveis.isEmpty() ?
                    TextUtils.join(", ", diasDisponiveis) : "Nenhum dia cadastrado");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao exibir dados", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_STORE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
            verificarDadosBarbearia();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarDadosBarbearia();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        }
        else if (id == R.id.nav_clientes) {
            startActivity(new Intent(this, BarberClientsActivity.class));
            finish();
        }
        else if (id == R.id.nav_logout) {
            realizarLogout();
        }else if (id == R.id.excluir_conta) {
            excluirConta();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                                        startActivity(new Intent(BarberDashboardActivity.this, MainMenu.class));
                                        finish();
                                        Toast.makeText(BarberDashboardActivity.this, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(BarberDashboardActivity.this, "Falha ao excluir conta. Tente novamente.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(BarberDashboardActivity.this, "Falha ao excluir dados na Firestore. Tente novamente.", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Erro ao excluir dados na Firestore", e);
                    });
        } else {
            Toast.makeText(this, "Nenhum usuário logado", Toast.LENGTH_SHORT).show();
        }
    }



    private void realizarLogout() {
        mAuth.signOut();
        startActivity(new Intent(this, MainMenu.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}