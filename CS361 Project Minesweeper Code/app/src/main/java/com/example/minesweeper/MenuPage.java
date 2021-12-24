package com.example.minesweeper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MenuPage extends AppCompatActivity {

    private int difficulty_status, back_press;
    private Button difficulty, stat, reset_stat, back, another_back,
            beginner_button, intermediate_button, advanced_button,
            confirm_stat, yes, no;
    private TextView beginner_textView, intermediate_textView, advanced_textView,
            best_time1, best_time2, best_time3,
            game_play1, game_play2, game_play3,
            game_won1, game_won2, game_won3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_page);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width/2, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10, 10);

        //if click difficulty button, it will show popup window that show 3 button of difficulty
        difficulty = findViewById(R.id.difficulty);
        difficulty.setLayoutParams(layoutParams);
        difficulty.setTextSize(getResources().getInteger(R.integer.font_size_18));
        difficulty.setOnClickListener(this::selectDifficulty);

        //if click statistics button, it will all statistics
        // (show best time, game played, game won per difficulty)
        stat = findViewById(R.id.stat);
        stat.setLayoutParams(layoutParams);
        stat.setTextSize(getResources().getInteger(R.integer.font_size_18));
        stat.setOnClickListener(view -> showStat());

        //if click reset stat button, it will show 2 button; yes to reset and no to not reset
        reset_stat = findViewById(R.id.reset_stat);
        reset_stat.setLayoutParams(layoutParams);
        reset_stat.setTextSize(getResources().getInteger(R.integer.font_size_18));
        reset_stat.setOnClickListener(this::reset_stat);

        //if click back, it will go back to game
        String extra = getIntent().getStringExtra("extra");
        if(extra.equals("0")){
            back = findViewById(R.id.back);
            back.setLayoutParams(layoutParams);
            back.setText(getText(R.string.back_to_main_page));
            back.setTextSize(getResources().getInteger(R.integer.font_size_18));
            back.setOnClickListener(v -> {
                Intent intent = new Intent(this, Main.class);
                startActivity(intent);
            });
        }else if(extra.equals("1")){
            back = findViewById(R.id.back);
            back.setLayoutParams(layoutParams);
            back.setText(getText(R.string.back_to_game));
            back.setTextSize(getResources().getInteger(R.integer.font_size_18));
            back.setOnClickListener(v -> {
                Intent intent = new Intent(this, GamePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            });
            another_back = findViewById(R.id.another_back);
            another_back.setLayoutParams(layoutParams);
            another_back.setText(R.string.back_to_main_page);
            another_back.setTextSize(getResources().getInteger(R.integer.font_size_18));
            another_back.setVisibility(View.VISIBLE);
            another_back.setOnClickListener(v -> {
                Intent intent = new Intent(this, Main.class);
                startActivity(intent);
            });
        }
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    private void selectDifficulty(View view) {

        String filename = getString(R.string.setting);
        String inputString;
        List<String> values = new ArrayList<>();

        try {
            //read setting file and assign value
            BufferedReader inputReader = new BufferedReader
                    (new InputStreamReader(openFileInput(filename)));
            while ((inputString = inputReader.readLine()) != null) {
                values.add(inputString);
            }

            difficulty_status = Integer.parseInt(values.get(values.size() - 1));

            //create popup
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View dialog_view = inflater.inflate
                    (R.layout.difficulty, null);
            dialog_view.setBackground(getDrawable(R.drawable.popup_border));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialog_view);
            builder.setCancelable(false);
            setFinishOnTouchOutside(false);

            beginner_button = dialog_view.findViewById(R.id.beginner);
            intermediate_button = dialog_view.findViewById(R.id.intermediate);
            advanced_button = dialog_view.findViewById(R.id.advanced);
            beginner_button.setTextSize(getResources().getInteger(R.integer.font_size_18));
            intermediate_button.setTextSize(getResources().getInteger(R.integer.font_size_18));
            advanced_button.setTextSize(getResources().getInteger(R.integer.font_size_18));
            dialog_view.setBackground(getDrawable(R.drawable.popup_border));

            //switch case button in difficulty.
            //if game is on beginner mode, beginner button will be green button
            switch (difficulty_status) {
                case 0:
                    beginner_button.setBackgroundColor(getResources().getColor(R.color.green));
                    intermediate_button.setBackgroundColor(getResources().getColor(R.color.gray));
                    advanced_button.setBackgroundColor(getResources().getColor(R.color.gray));
                    break;
                case 1:
                    beginner_button.setBackgroundColor(getResources().getColor(R.color.gray));
                    intermediate_button.setBackgroundColor(getResources().getColor(R.color.green));
                    advanced_button.setBackgroundColor(getResources().getColor(R.color.gray));
                    break;
                case 2:
                    beginner_button.setBackgroundColor(getResources().getColor(R.color.gray));
                    intermediate_button.setBackgroundColor(getResources().getColor(R.color.gray));
                    advanced_button.setBackgroundColor(getResources().getColor(R.color.green));
                    break;
            }

            Dialog dialog = builder.create();
            dialog.show();

            //if click beginner button,
            //it will change beginner button to green button, and other button to gray button.
            //and write file
            beginner_button.setOnClickListener(v -> {
                difficulty_status = 0;
                beginner_button.setBackgroundColor(getResources().getColor(R.color.green));
                intermediate_button.setBackgroundColor(getResources().getColor(R.color.gray));
                advanced_button.setBackgroundColor(getResources().getColor(R.color.gray));
                String data = Integer.toString(difficulty_status) + getText(R.string.new_line);
                writeToFile(data, this, filename);
                dialog.dismiss();
            });

            //if click intermediate button,
            //it will change intermediate button to green button, and other button to gray button.
            //and write file
            intermediate_button.setOnClickListener(v -> {
                difficulty_status = 1;
                beginner_button.setBackgroundColor(getResources().getColor(R.color.gray));
                intermediate_button.setBackgroundColor(getResources().getColor(R.color.green));
                advanced_button.setBackgroundColor(getResources().getColor(R.color.gray));
                String data = Integer.toString(difficulty_status) + getText(R.string.new_line);
                writeToFile(data, this, filename);
                dialog.dismiss();
            });

            //if click advanced button,
            //it will change advanced button to green button, and other button to gray button.
            //and write file
            advanced_button.setOnClickListener(v -> {
                difficulty_status = 2;
                beginner_button.setBackgroundColor(getResources().getColor(R.color.gray));
                intermediate_button.setBackgroundColor(getResources().getColor(R.color.gray));
                advanced_button.setBackgroundColor(getResources().getColor(R.color.green));
                String data = Integer.toString(difficulty_status) + getText(R.string.new_line);
                writeToFile(data, this, filename);
                dialog.dismiss();
            });

        } catch (Exception ignored){

        }
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables", "SetTextI18n"})
    private void showStat() {
        String filename = getString(R.string.history);
        String inputString;
        List<String> values = new ArrayList<>();

        try {
            File dir = getFilesDir();
            File file = new File(dir, filename);
            if(!file.exists()){
                String data = getText(R.string.max_time).toString()
                        + getText(R.string.new_line).toString() + getText(R.string.zero)
                        + getText(R.string.new_line).toString() + getText(R.string.zero)
                        + getText(R.string.new_line) + getText(R.string.max_time).toString()
                        + getText(R.string.new_line).toString() + getText(R.string.zero)
                        + getText(R.string.new_line).toString() + getText(R.string.zero)
                        + getText(R.string.new_line) + getText(R.string.max_time).toString()
                        + getText(R.string.new_line).toString() + getText(R.string.zero)
                        + getText(R.string.new_line).toString() + getText(R.string.zero);
                writeToFile(data, this, getText(R.string.history).toString());
            }
            //read history file and assign value
            BufferedReader inputReader = new BufferedReader
                    (new InputStreamReader(openFileInput(filename)));
            while ((inputString = inputReader.readLine()) != null) {
                values.add(inputString);
            }

            difficulty_status = Integer.parseInt(values.get(values.size() - 1));

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View dialog_view = inflater.inflate
                    (R.layout.stat, null);
            dialog_view.setBackground(getDrawable(R.drawable.popup_border));
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialog_view);
            builder.setCancelable(false);
            setFinishOnTouchOutside(false);

            beginner_textView = dialog_view.findViewById(R.id.beginner);
            intermediate_textView = dialog_view.findViewById(R.id.intermediate);
            advanced_textView = dialog_view.findViewById(R.id.advanced);
            beginner_textView.setTextSize(getResources().getInteger(R.integer.font_size_18));
            intermediate_textView.setTextSize(getResources().getInteger(R.integer.font_size_18));
            advanced_textView.setTextSize(getResources().getInteger(R.integer.font_size_18));

            //set TextView from value that read at begin of method showStat
            best_time1 = dialog_view.findViewById(R.id.best_time1);
            game_play1 = dialog_view.findViewById(R.id.games_played1);
            game_won1 = dialog_view.findViewById(R.id.games_won1);
            best_time1.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_play1.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_won1.setTextSize(getResources().getInteger(R.integer.font_size_18));

            if(values.get(0).contentEquals(getText(R.string.max_time))){
                best_time1.setText(best_time1.getText().toString() + " "
                        + getText(R.string.default_time).toString()
                        + " " + getText(R.string.seconds));
            }else{
                best_time1.setText(best_time1.getText().toString() + " " + values.get(0)
                        + " " + getText(R.string.seconds));
            }
            game_play1.setText(game_play1.getText().toString() + " " + values.get(1));
            game_won1.setText(game_won1.getText().toString() + " " + values.get(2));

            best_time2 = dialog_view.findViewById(R.id.best_time2);
            game_play2 = dialog_view.findViewById(R.id.games_played2);
            game_won2 = dialog_view.findViewById(R.id.games_won2);
            best_time2.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_play2.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_won2.setTextSize(getResources().getInteger(R.integer.font_size_18));

            if(values.get(3).contentEquals(getText(R.string.max_time))){
                best_time2.setText(best_time2.getText().toString() + " "
                        + getText(R.string.default_time).toString()
                        + " " + getText(R.string.seconds));
            }else{
                best_time2.setText(best_time2.getText().toString() + " " + values.get(3)
                        + " " + getText(R.string.seconds));
            }
            game_play2.setText(game_play2.getText().toString() + " " + values.get(4));
            game_won2.setText(game_won2.getText().toString() + " " + values.get(5));

            best_time3 = dialog_view.findViewById(R.id.best_time3);
            game_play3 = dialog_view.findViewById(R.id.games_played3);
            game_won3 = dialog_view.findViewById(R.id.games_won3);
            best_time3.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_play3.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_won3.setTextSize(getResources().getInteger(R.integer.font_size_18));

            if(values.get(6).contentEquals(getText(R.string.max_time))){
                best_time3.setText(best_time3.getText().toString() + " "
                        + getText(R.string.default_time).toString()
                        + " " + getText(R.string.seconds));
            }else{
                best_time3.setText(best_time3.getText().toString() + " " + values.get(6)
                        + " " + getText(R.string.seconds));
            }
            game_play3.setText(game_play3.getText().toString() + " " + values.get(7));
            game_won3.setText(game_won3.getText().toString() + " " + values.get(8));

            Dialog dialog = builder.create();
            dialog.show();

            confirm_stat = dialog_view.findViewById(R.id.confirm_stat);
            confirm_stat.setTextSize(getResources().getInteger(R.integer.font_size_18));
            confirm_stat.setOnClickListener(v -> dialog.dismiss());

        } catch (Exception ignored){

        }
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables", "SetTextI18n"})
    private void reset_stat(View view){
        //create alert dialog
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View dialog_view = inflater.inflate
                (R.layout.reset_stat, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialog_view);
        builder.setCancelable(false);
        setFinishOnTouchOutside(false);

        //setting yes button to green button and no button to red button
        yes = dialog_view.findViewById(R.id.yes);
        no = dialog_view.findViewById(R.id.no);
        yes.setBackgroundColor(getResources().getColor(R.color.green));
        no.setBackgroundColor(getResources().getColor(R.color.red));
        yes.setTextSize(getResources().getInteger(R.integer.font_size_18));
        no.setTextSize(getResources().getInteger(R.integer.font_size_18));

        //show alert dialog
        Dialog dialog = builder.create();
        dialog.show();

        //if click yes button, it will reset stat by reset value to default and write file
        yes.setOnClickListener(v -> {
            String data = getText(R.string.max_time).toString()
                    + getText(R.string.new_line).toString() + 0
                    + getText(R.string.new_line).toString() + 0
                    + getText(R.string.new_line) + getText(R.string.max_time).toString()
                    + getText(R.string.new_line).toString() + 0
                    + getText(R.string.new_line).toString() + 0
                    + getText(R.string.new_line) + getText(R.string.max_time).toString()
                    + getText(R.string.new_line).toString() + 0
                    + getText(R.string.new_line).toString() + 0;
            writeToFile(data, this, getText(R.string.history).toString());
            dialog.dismiss();
        });

        //if click no button, it will close alert dialog
        no.setOnClickListener(v -> dialog.dismiss());
    }

    //write file
    private void writeToFile(String data, Context context, String filename) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter
                    (context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException ignored) {

        }
    }

    @Override
    public void onBackPressed() {
        back_press++;
        Toast.makeText(this, getString(R.string.press_back),
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try{
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width/2, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10, 10);

            difficulty.setLayoutParams(layoutParams);
            difficulty.setTextSize(getResources().getInteger(R.integer.font_size_18));
            stat.setLayoutParams(layoutParams);
            stat.setTextSize(getResources().getInteger(R.integer.font_size_18));
            reset_stat.setLayoutParams(layoutParams);
            reset_stat.setTextSize(getResources().getInteger(R.integer.font_size_18));
            back.setLayoutParams(layoutParams);
            back.setTextSize(getResources().getInteger(R.integer.font_size_18));
            another_back.setLayoutParams(layoutParams);
            another_back.setTextSize(getResources().getInteger(R.integer.font_size_18));

            beginner_button.setTextSize(getResources().getInteger(R.integer.font_size_18));
            intermediate_button.setTextSize(getResources().getInteger(R.integer.font_size_18));
            advanced_button.setTextSize(getResources().getInteger(R.integer.font_size_18));
            confirm_stat.setTextSize(getResources().getInteger(R.integer.font_size_18));
            yes.setTextSize(getResources().getInteger(R.integer.font_size_18));
            no.setTextSize(getResources().getInteger(R.integer.font_size_18));
            beginner_textView.setTextSize(getResources().getInteger(R.integer.font_size_18));
            intermediate_textView.setTextSize(getResources().getInteger(R.integer.font_size_18));
            advanced_textView.setTextSize(getResources().getInteger(R.integer.font_size_18));
            best_time1.setTextSize(getResources().getInteger(R.integer.font_size_18));
            best_time2.setTextSize(getResources().getInteger(R.integer.font_size_18));
            best_time3.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_play1.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_play2.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_play3.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_won1.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_won2.setTextSize(getResources().getInteger(R.integer.font_size_18));
            game_won3.setTextSize(getResources().getInteger(R.integer.font_size_18));
        } catch (NullPointerException ignored){

        }

    }
}