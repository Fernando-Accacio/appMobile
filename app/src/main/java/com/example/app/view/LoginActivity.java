package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.controller.ChooseOption;
import com.example.app.controller.MainMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    public EditText edtEmail;
    public EditText edtSenha;
    public Button btnLogin;
    public TextView txtRegistrar;
    public FirebaseFirestore db;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegistrar = findViewById(R.id.txtRegistrar);
    }

    private void setupClickListeners() {

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();

            if (validarCampos(email, senha)) {
                loginWithEmailPassword(email, senha);
            }
        });

        txtRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ChooseOption.class);
            startActivity(intent);
        });
    }

    private boolean validarCampos(String email, String senha) {
        if (email.isEmpty()) {
            edtEmail.setError("Digite seu e-mail");
            edtEmail.requestFocus();
            return false;
        }

        if (senha.isEmpty()) {
            edtSenha.setError("Digite sua senha");
            edtSenha.requestFocus();
            return false;
        }

        return true;
    }

    private void loginWithEmailPassword(String email, String senha) {
        showLoading("Autenticando...");

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    dismissLoading();

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            verificarTipoDeUsuario(user.getUid());
                        }
                    } else {
                        tratarErroLogin(task.getException());
                    }
                });
    }

    void verificarTipoDeUsuario(String userId) {
        showLoading("Verificando perfil...");

        DocumentReference docRef = db.collection("usuarios").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            dismissLoading();

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    redirectByUserType(document.getString("tipoUsuario"));
                } else {
                    showErrorDialog("Usuário não encontrado no sistema");
                }
            } else {
                showErrorDialog("Erro ao verificar perfil: " + task.getException().getMessage());
            }
        });
    }

    private void redirectByUserType(String tipoUsuario) {
        Intent intent;

        if ("barbeiro".equals(tipoUsuario)) {
            intent = new Intent(this, BarberDashboardActivity.class);
        } else if ("cliente".equals(tipoUsuario)) {
            intent = new Intent(this, ClientesBarbeiroActivity.class);
        } else {
            showErrorDialog("Tipo de usuário não reconhecido");
            return;
        }

        startActivity(intent);
        finish();
    }

    public void tratarErroLogin(Exception exception) {
        try {
            throw exception;
        } catch (FirebaseAuthInvalidUserException e) {
            showErrorDialog("E-mail não cadastrado ou conta desativada");
        } catch (FirebaseAuthInvalidCredentialsException e) {
            showErrorDialog("Senha incorreta ou e-mail inválido");
        } catch (Exception e) {
            showErrorDialog("Erro ao fazer login: " + e.getMessage());
        }
    }

    private void showLoading(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private void dismissLoading() {
    }

    void showErrorDialog(String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle("Erro")
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }
}