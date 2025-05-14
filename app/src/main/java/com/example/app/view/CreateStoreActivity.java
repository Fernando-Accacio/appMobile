package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateStoreActivity extends AppCompatActivity {

    private static final String TAG = "CreateStoreActivity";

    protected FirebaseAuth mAuth;
    protected FirebaseFirestore db;
    private ImageView backArrow;
    protected CheckBox chkCorte;
    protected CheckBox chkBarba;
    protected CheckBox chkSobrancelha;
    protected CheckBox chkDia1;
    protected CheckBox chkDia2;
    protected CheckBox chkDia3;
    protected CheckBox chkDia4;
    protected CheckBox chkDia5;
    protected CheckBox chkDia6;
    protected CheckBox chkDia7;
    protected MaterialButton btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            inicializarViews();

            backArrow.setOnClickListener(v -> {
                setResult(RESULT_CANCELED);
                finish();
            });

            btnSalvar.setOnClickListener(v -> salvarRespostas());

        } catch (Exception e) {
            Log.e(TAG, "Erro no onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao inicializar", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void inicializarViews() {
        backArrow = findViewById(R.id.backArrow);
        chkCorte = findViewById(R.id.chkCorte);
        chkBarba = findViewById(R.id.chkBarba);
        chkSobrancelha = findViewById(R.id.chkSobrancelha);
        chkDia1 = findViewById(R.id.chkDia1);
        chkDia2 = findViewById(R.id.chkDia2);
        chkDia3 = findViewById(R.id.chkDia3);
        chkDia4 = findViewById(R.id.chkDia4);
        chkDia5 = findViewById(R.id.chkDia5);
        chkDia6 = findViewById(R.id.chkDia6);
        chkDia7 = findViewById(R.id.chkDia7);
        btnSalvar = findViewById(R.id.btnSalvar);
    }

    protected void salvarRespostas() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Você precisa estar logado", Toast.LENGTH_LONG).show();
            return;
        }

        List<String> servicosEscolhidos = new ArrayList<>();
        if (chkCorte.isChecked()) servicosEscolhidos.add("Corte de Cabelo");
        if (chkBarba.isChecked()) servicosEscolhidos.add("Barba");
        if (chkSobrancelha.isChecked()) servicosEscolhidos.add("Sobrancelha");

        List<String> diasDisponiveis = new ArrayList<>();
        if (chkDia1.isChecked()) diasDisponiveis.add("Segunda-feira");
        if (chkDia2.isChecked()) diasDisponiveis.add("Terça-feira");
        if (chkDia3.isChecked()) diasDisponiveis.add("Quarta-feira");
        if (chkDia4.isChecked()) diasDisponiveis.add("Quinta-feira");
        if (chkDia5.isChecked()) diasDisponiveis.add("Sexta-feira");
        if (chkDia6.isChecked()) diasDisponiveis.add("Sábado");
        if (chkDia7.isChecked()) diasDisponiveis.add("Domingo");

        if (servicosEscolhidos.isEmpty() || diasDisponiveis.isEmpty()) {
            Toast.makeText(this, "Selecione ao menos um serviço e um dia", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> barbeiroData = new HashMap<>();
                        barbeiroData.put("userId", userId);
                        barbeiroData.put("nome", documentSnapshot.getString("nome"));
                        barbeiroData.put("email", documentSnapshot.getString("email"));
                        barbeiroData.put("endereco", documentSnapshot.getString("endereco"));
                        barbeiroData.put("servicos", servicosEscolhidos);
                        barbeiroData.put("diasDisponiveis", diasDisponiveis);

                        db.collection("barbeiro").document(userId)
                                .set(barbeiroData)
                                .addOnSuccessListener(aVoid -> {
                                    Intent intent = new Intent(CreateStoreActivity.this, ConfirmationActivity.class);
                                    startActivity(intent);
                                    finish();
                                })

                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Erro ao salvar: " + e.getMessage(), e);
                                    Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao buscar usuário: " + e.getMessage(), e);
                    Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_LONG).show();
                });
    }
}