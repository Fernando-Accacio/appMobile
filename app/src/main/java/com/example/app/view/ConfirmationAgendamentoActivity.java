package com.example.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;

public class ConfirmationAgendamentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_agendamento);

        Log.d("CONFIRMATION", "Activity iniciada");

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("CONFIRMATION", "Nenhum dado recebido");
            finish();
            return;
        }

        String barbeiroNome = extras.getString("BARBEIRO_NOME");
        String servico = extras.getString("SERVICO");
        String dataHora = extras.getString("DATA_HORA");

        if (barbeiroNome == null || servico == null || dataHora == null) {
            Log.e("CONFIRMATION", "Dados incompletos");
            finish();
            return;
        }

        TextView tvServico = findViewById(R.id.tvServico);
        TextView tvDataHora = findViewById(R.id.tvDataHora);

        tvServico.setText("Serviço: " + servico);
        tvDataHora.setText("Data/Hora: " + dataHora);

        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardClientActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        Toast.makeText(this, "Agendamento confirmado com sucesso!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}