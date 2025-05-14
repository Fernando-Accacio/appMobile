package com.example.app.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.controller.MainMenu;

public class TermsConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        CheckBox cbAcceptTerms = findViewById(R.id.cbAcceptTerms);
        Button btnContinue = findViewById(R.id.btnContinue);

        // Botão só fica habilitado quando os termos forem aceitos
        btnContinue.setEnabled(false);

        cbAcceptTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnContinue.setEnabled(isChecked);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Salva no SharedPreferences que o usuário já aceitou os termos
                SharedPreferences sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("terms_accepted", true);
                editor.apply();

                // Redireciona para o menu principal
                Intent intent = new Intent(TermsConditionsActivity.this, MainMenu.class);
                startActivity(intent);
                finish(); // Finaliza esta activity para que o usuário não possa voltar
            }
        });
    }
}