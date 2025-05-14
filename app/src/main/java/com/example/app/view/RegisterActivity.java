package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.controller.ChooseOption;
import com.example.app.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha, edtEndereco;
    private Button btnRegistrar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_register);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, ChooseOption.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtEndereco = findViewById(R.id.edtEndereco);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        boolean isBarbeiro = getIntent().getBooleanExtra("isBarbeiro", false);

        if (isBarbeiro) {
            edtEndereco.setVisibility(View.VISIBLE);
        } else {
            edtEndereco.setVisibility(View.GONE);
        }

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = edtNome.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String senha = edtSenha.getText().toString().trim();
                String phoneNumber = "";
                String endereco = edtEndereco.getText().toString().trim();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
                mAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();

                                    String tipoUsuario = isBarbeiro ? "barbeiro" : "cliente";

                                    User userObject = new User(nome, email, senhaCriptografada, phoneNumber, isBarbeiro ? endereco : null, tipoUsuario);

                                    db.collection("usuarios").document(uid).set(userObject)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(RegisterActivity.this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(RegisterActivity.this, "Erro ao registrar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Erro ao registrar usuário: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
