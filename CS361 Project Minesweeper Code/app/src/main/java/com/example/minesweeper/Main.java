package com.example.minesweeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Main extends AppCompatActivity {

    private ImageView imageView;
    private Button play, menuInMain, exit;
    private TextView app_name;
    private int back_press;

    @SuppressLint({"DefaultLocale", "ClickableViewAccessibility", "RtlHardcoded", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (width/2, height/2);

        imageView = findViewById(R.id.bomb_logo);
        imageView.setLayoutParams(params);

        params = new LinearLayout.LayoutParams
                (width/2, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);

        app_name = findViewById(R.id.app_name);
        app_name.setTextSize(getResources().getInteger(R.integer.font_size_28));

        play = findViewById(R.id.play);
        menuInMain = findViewById(R.id.menuInMain);
        exit = findViewById(R.id.exit);
        play.setLayoutParams(params);
        play.setTextSize(getResources().getInteger(R.integer.font_size_18));
        menuInMain.setLayoutParams(params);
        menuInMain.setTextSize(getResources().getInteger(R.integer.font_size_18));
        exit.setLayoutParams(params);
        exit.setTextSize(getResources().getInteger(R.integer.font_size_18));

        play.setOnClickListener(v -> {
            Intent intent = new Intent(this, GamePage.class);
            startActivity(intent);
        });

        menuInMain.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuPage.class);
            String extra = "0";
            intent.putExtra("extra", extra);
            startActivity(intent);
        });

        exit.setOnClickListener(v -> finishAffinity());
    }

    @Override
    public void onBackPressed() {
        back_press++;
        Toast.makeText(this, getString(R.string.press_back_main),
                Toast.LENGTH_SHORT).show();
        if(back_press == 1){
            new CountDownTimer(3000, 1000){

                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    back_press = 0;
                }

            }.start();

        } else if(back_press == 2){
            back_press = 0;
            super.onBackPressed();
        }
    }

    //Configuration Changed
    @SuppressLint("RtlHardcoded")
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (width/2, height/2);

        imageView.setLayoutParams(params);

        params = new LinearLayout.LayoutParams
                (width/2, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);

        app_name.setTextSize(getResources().getInteger(R.integer.font_size_28));

        play.setLayoutParams(params);
        play.setTextSize(getResources().getInteger(R.integer.font_size_18));
        menuInMain.setLayoutParams(params);
        menuInMain.setTextSize(getResources().getInteger(R.integer.font_size_18));
        exit.setLayoutParams(params);
        exit.setTextSize(getResources().getInteger(R.integer.font_size_18));
    }
}