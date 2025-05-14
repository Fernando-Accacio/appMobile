package com.example.app.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.example.app.controller.MainMenu;
import com.example.app.R;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuração para barra de status transparente com foco no layout correto
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Diz ao WindowManager para desenhar na área da barra de status
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

            // Android 11+ (API 30+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
                WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
                controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
            // Android 6.0+ (API 23+)
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }

        setContentView(R.layout.activity_main);

        // Adiciona padding ao conteúdo baseado no tamanho da barra de status para versões anteriores
        final View content = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            // Para Android mais recente que suporta WindowInsets
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            // Não precisamos adicionar o padding porque estamos usando fitsSystemWindows no layout
            return insets;
        });

        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.animate().alpha(0f).setDuration(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                imageView.animate().alpha(1f).setDuration(1000);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
                boolean termsAccepted = sharedPref.getBoolean("terms_accepted", false);
                Intent intent;
                if (!termsAccepted) {
                    intent = new Intent(MainActivity.this, TermsConditionsActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, MainMenu.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}