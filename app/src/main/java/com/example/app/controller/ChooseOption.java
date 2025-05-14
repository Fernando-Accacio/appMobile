package com.example.app.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.ImageView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.app.R;
import com.example.app.view.RegisterActivity;

public class ChooseOption extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_choose);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseOption.this, MainMenu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CardView btnBarbeiro = findViewById(R.id.btnBarbeiro);
        CardView btnCliente = findViewById(R.id.btnCliente);

        btnBarbeiro.setOnClickListener(v -> abrirTelaRegistro(true));
        btnCliente.setOnClickListener(v -> abrirTelaRegistro(false));
    }

    public void abrirTelaRegistro(boolean isBarbeiro) {
        Intent intent = new Intent(ChooseOption.this, RegisterActivity.class);
        intent.putExtra("isBarbeiro", isBarbeiro);
        startActivity(intent);
    }
}