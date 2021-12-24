package com.example.minesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class GamePage extends AppCompatActivity {

    public static final long TIMER_LENGTH = 999000L;

    private TextView time, bomb;
    private ImageButton smile;
    private Button menu, mode;
    private CountDownTimer countDownTimer;
    private Minesweeper minesweeper;
    private String filename;
    private LinearLayout root;
    private List<Cell> cells;
    private List<TextView> textViews;
    private int secondsPass, difficulty, bomb_number, grid_size_x, grid_size_y, back_press;
    private int[] best_time, game_play, game_won;
    private boolean timerStart, game_mode, shown_dialog;

    @SuppressLint({"DefaultLocale", "ClickableViewAccessibility", "RtlHardcoded"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_page);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //define size of array
        best_time = new int[3];
        game_play = new int[3];
        game_won = new int[3];
        back_press = 0;
        game_mode = true;
        shown_dialog = true;

        read_setting_file();

        LinearLayout.LayoutParams rootFrame = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        rootFrame.gravity = Gravity.CENTER_HORIZONTAL;

        //setting grid size and number of bomb
        root = findViewById(R.id.linearLayoutVertical2);
        root.setLayoutParams(rootFrame);

        //create count time
        time = findViewById(R.id.time);
        timerStart = false;
        createCountDownTimer();

        //create minesweeper
//        minesweeper = new Minesweeper(bomb_number, grid_size_x, grid_size_y);
        textViews = new ArrayList<>();
        createTable();
//        cells = minesweeper.getCells();

        bomb = findViewById(R.id.bomb);
        bomb.setText(String.format("%03d", bomb_number));
        setBombAndTimeColor();

        //if click smile, it will start new game
        //(create new game, new adapter and set default value)
        smile = findViewById(R.id.smile);
        smile.setOnClickListener(v -> {
            minesweeper = null;
            root.removeAllViews();
            textViews = new ArrayList<>();
            read_setting_file();
            createTable();
            smile.setImageResource(R.drawable.face_smile);
            timerStart = false;
            countDownTimer.cancel();
            secondsPass = 0;
            time.setText(R.string.time);
            bomb.setText(String.format("%03d", bomb_number));
        });

        //setting menu
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuPage.class);
            String value = Integer.toString(difficulty) + getText(R.string.new_line);
            writeToFile(value, this, getText(R.string.setting).toString());
            String extra = "1";
            intent.putExtra("extra", extra);
            countDownTimer.cancel();
            createCountDownTimer();
            timerStart = false;
            startActivity(intent);
        });

        //setting mode
        mode = findViewById(R.id.mode);
        mode.setOnClickListener(v -> {
            game_mode = !game_mode;
            setBombAndTimeColor();
        });

        menu.setTextSize(getResources().getInteger(R.integer.font_size_18));
        bomb.setTextSize(getResources().getInteger(R.integer.font_size_18));
        time.setTextSize(getResources().getInteger(R.integer.font_size_18));
        mode.setTextSize(getResources().getInteger(R.integer.font_size_18));


    }

    private void createCountDownTimer(){
        countDownTimer = new CountDownTimer(TIMER_LENGTH, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                secondsPass++;
                time.setText(String.format("%03d", secondsPass));
            }

            @Override
            public void onFinish() {
                minesweeper.revealAllBombs();
            }
        };
    }

    private void read_setting_file() {
        try{
            Thread thread = new Thread(() -> {
                try{
                    filename = getString(R.string.setting);
                    BufferedReader inputReader = new BufferedReader
                            (new InputStreamReader(openFileInput(filename)));
                    String inputString;
                    String last_line = null;
                    while ((inputString = inputReader.readLine()) != null) {
                        last_line = inputString;
                    }
                    assert last_line != null;
                    difficulty = Integer.parseInt(last_line);
                }catch (Exception ignored){
                    difficulty = 0;
                }

                switch(difficulty){
                    case 0: bomb_number = 10; grid_size_x = 9; grid_size_y = 9; break;
                    case 1: bomb_number = 40; grid_size_x = 16; grid_size_y = 16; break;
                    case 2: bomb_number = 99; grid_size_x = 16; grid_size_y = 30; break;
                }
            });
            thread.start();
            thread.join();
        } catch (Exception ignored){

        }
    }

    //Toast mode (Normal Mode or Flag Mode)
    private void setBombAndTimeColor() {
        if(!game_mode){
            Toast.makeText(getApplicationContext(), getText(R.string.flag_mode), Toast.LENGTH_SHORT).show();
            time.setTextColor(getResources().getColor(R.color.green));
            bomb.setTextColor(getResources().getColor(R.color.green));
        }else {
            Toast.makeText(getApplicationContext(), getText(R.string.normal_mode), Toast.LENGTH_SHORT).show();
            time.setTextColor(getResources().getColor(R.color.red));
            bomb.setTextColor(getResources().getColor(R.color.red));
        }
    }

    //create table
    @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
    private void createTable(){
        ScrollView scrollView = new ScrollView(this);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);

        LinearLayout rootForScroll = new LinearLayout(this);
        rootForScroll.setOrientation(LinearLayout.VERTICAL);
        for(int i = 0 ; i < grid_size_x ; i++){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,5);
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(layoutParams);
            linearLayout.setGravity(Gravity.CENTER);

            for(int j = 0 ; j < grid_size_y ; j++){
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(2, 2, 2, 2);
                TextView textView = new TextView(this);
                textView.setBackground(getDrawable(R.drawable.cell_purple));
                textView.setLayoutParams(lp);
                textView.setId((i * grid_size_y) + j);
                textView.setOnClickListener(v -> cellClick(textView.getId()));
                linearLayout.addView(textView);
                textViews.add(textView);
            }
            rootForScroll.addView(linearLayout);
        }
        horizontalScrollView.addView(rootForScroll);
        scrollView.addView(horizontalScrollView);
        root.addView(scrollView);
    }

    //handle Click
    @SuppressLint({"NewApi", "UseCompatLoadingForDrawables"})
    private void textViewClick(TextView textView){
        Cell cell = minesweeper.getCell(textView.getId());
        runOnUiThread(()->{
            if(cell.isReveal()){
                if (cell.getValue() == -1) {
                    textView.setText(getText(R.string.bomb_icon));
                }

                //if value of cell is 0 then set background of that cell to blank
                else if (cell.getValue() == 0) {
                    textView.setText(getText(R.string.empty));
                }

                //if value is not 0 or -1 then set text color by value of that cell
                else {
                    textView.setText(String.valueOf(cell.getValue()));
                    if (cell.getValue() == 1) {
                        textView.setTextColor(getColor(R.color.for1));
                    } else if (cell.getValue() == 2) {
                        textView.setTextColor(getColor(R.color.for2));
                    } else if (cell.getValue() == 3) {
                        textView.setTextColor(getColor(R.color.for3));
                    } else if (cell.getValue() == 4) {
                        textView.setTextColor(getColor(R.color.for4));
                    } else if (cell.getValue() == 5) {
                        textView.setTextColor(getColor(R.color.for5));
                    } else if (cell.getValue() == 6) {
                        textView.setTextColor(getColor(R.color.for6));
                    } else if (cell.getValue() == 7) {
                        textView.setTextColor(getColor(R.color.for7));
                    } else if (cell.getValue() == 8) {
                        textView.setTextColor(getColor(R.color.for8));
                    }
                }

                textView.setBackground(getDrawable(R.drawable.cell_while_purple));
                textView.setGravity(Gravity.CENTER);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                if(cell.isFlag()){
                    textView.setText(getText(R.string.flag_icon));
                }else {
                    textView.setText(getText(R.string.empty));
                }

                textView.setGravity(Gravity.CENTER);
            }
        });
    }

    //Click cell
    @SuppressLint("DefaultLocale")
    private void cellClick(int position) {
        try {
            Thread thread = new Thread(() -> {

                //generate Grid, bomb and number
                //generateGrid(bomb_number, position);
                if(minesweeper == null){
                    minesweeper = new Minesweeper(bomb_number, grid_size_x, grid_size_y);
                    minesweeper.generateGrid(bomb_number, position);
                }

                //Handle click
                cells = minesweeper.getCells();
                Cell cell = cells.get(position);
                if(!minesweeper.isGameWon() && !minesweeper.isGame_over()){
                    minesweeper.setMode(game_mode);
                    minesweeper.handleCellClick(cell);

                    //Show number of bomb
                    if (minesweeper.getBomb_number() - minesweeper.getFlag_count() >= 0) {
                        bomb.setText(String.format
                                ("%03d", minesweeper.getBomb_number()
                                        - minesweeper.getFlag_count()));
                    } else if (minesweeper.getBomb_number() - minesweeper.getFlag_count() < 0) {
                        bomb.setText(R.string.bomb);
                    }

                    if(!timerStart && secondsPass != 0){
                        countDownTimer.start();
                        timerStart = true;
                    }

                    //if game not start it will start count down
                    //and read history file to assign value
                    if (!timerStart) {
                        game_play[difficulty]++;
                        countDownTimer.start();
                        timerStart = true;
                        File dir = getFilesDir();
                        File file = new File(dir, getText(R.string.history).toString());
                        boolean file_exists = true;

                        //if file is not exists, it will write from default value
                        if(!file.exists()){
                            file_exists = false;
                            String data = getText(R.string.max_time).toString()
                                    + getText(R.string.new_line).toString() + game_play[0]
                                    + getText(R.string.new_line).toString() + game_won[0]
                                    + getText(R.string.new_line)
                                    + getText(R.string.max_time).toString()
                                    + getText(R.string.new_line).toString() + game_play[1]
                                    + getText(R.string.new_line).toString() + game_won[1]
                                    + getText(R.string.new_line)
                                    + getText(R.string.max_time).toString()
                                    + getText(R.string.new_line).toString() + game_play[2]
                                    + getText(R.string.new_line).toString() + game_won[2];
                            writeToFile(data, this, getText(R.string.history).toString());
                        }

                        //read history file to assign value
                        try{
                            filename = getString(R.string.history);
                            BufferedReader inputReader = new BufferedReader
                                    (new InputStreamReader(openFileInput(filename)));
                            String inputString;
                            List<String> values = new ArrayList<>();
                            while ((inputString = inputReader.readLine()) != null) {
                                values.add(inputString);
                            }
                            for(int i = 0 ; i < 3 ; i++){
                                best_time[i] = Integer.parseInt(values.get(3 * i));
                                game_play[i] = Integer.parseInt(values.get(1 + (3 * i)));
                                game_won[i] = Integer.parseInt(values.get(2 + (3 * i)));
                            }
                        }catch (Exception ignored){
                            for(int i = 0 ; i < 3 ; i++){
                                best_time[i] =
                                        Integer.parseInt(getText(R.string.max_time).toString());
                                game_play[i] = 0;
                                game_won[i] = 0;
                            }
                        }

                        //if file exists, it will write file from exists value
                        if(file_exists){
                            game_play[difficulty]++;
                            String data = "" + best_time[0]
                                    + getText(R.string.new_line).toString() + game_play[0]
                                    + getText(R.string.new_line).toString() + game_won[0]
                                    + getText(R.string.new_line)
                                    + getText(R.string.max_time).toString()
                                    + getText(R.string.new_line).toString() + game_play[1]
                                    + getText(R.string.new_line).toString() + game_won[1]
                                    + getText(R.string.new_line)
                                    + getText(R.string.max_time).toString()
                                    + getText(R.string.new_line).toString() + game_play[2]
                                    + getText(R.string.new_line).toString() + game_won[2];
                            writeToFile(data, this, getText(R.string.history).toString());
                        }
                    }

                    //if game is over, it will stop count down, show all bomb and set dizzy image
                    if (minesweeper.isGame_over()) {
                        countDownTimer.cancel();
                        minesweeper.revealAllBombs();
                        smile.setImageResource(R.drawable.face_dizzy);
                    }

                    //if game is won, it will stop count down, show all bomb,
                    // set laugh image and write file
                    if (minesweeper.isGameWon()) {
                        countDownTimer.cancel();
                        minesweeper.revealAllBombs();
                        smile.setImageResource(R.drawable.face_laugh);
                        if(best_time[difficulty] > secondsPass){
                            best_time[difficulty] = secondsPass;
                        }
                        game_won[difficulty]++;
                        String data = "" + best_time[0]
                                + getText(R.string.new_line).toString() + game_play[0]
                                + getText(R.string.new_line).toString() + game_won[0]
                                + getText(R.string.new_line) + best_time[1]
                                + getText(R.string.new_line).toString() + game_play[1]
                                + getText(R.string.new_line).toString() + game_won[1]
                                + getText(R.string.new_line) + best_time[2]
                                + getText(R.string.new_line).toString() + game_play[2]
                                + getText(R.string.new_line).toString() + game_won[2];

                        writeToFile(data, this, getText(R.string.history).toString());
                        runOnUiThread(this::showDialog);
                    }
                    cells = minesweeper.getCells();
                    for(Cell cell1: cells){
                        textViewClick(textViews.get(cell1.getID()));
                    }
                }else if(minesweeper.isGameWon()){
                    runOnUiThread(this::showDialog);
                }

            });
            thread.start();
            thread.join();
        } catch (InterruptedException ignored){

        }
    }

    //won dialog
    @SuppressLint("SetTextI18n")
    public void showDialog(){
        if(shown_dialog) {
            shown_dialog = false;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.show_message, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);
            setFinishOnTouchOutside(false);

            TextView time_use = dialogView.findViewById(R.id.time_use);
            TextView best_time = dialogView.findViewById(R.id.best_time);
            TextView games_played = dialogView.findViewById(R.id.games_played);
            TextView games_won = dialogView.findViewById(R.id.games_won);

            time_use.setText(time_use.getText().toString()
                    + " " + this.secondsPass + " " + getText(R.string.seconds));
            best_time.setText(best_time.getText().toString()
                    + " " + this.best_time[difficulty] + " " + getText(R.string.seconds));
            games_played.setText(games_played.getText().toString() + " " + this.game_play[difficulty]);
            games_won.setText(games_won.getText().toString() + " " + this.game_won[difficulty]);

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            Button confirm = dialogView.findViewById(R.id.confirm);
            confirm.setOnClickListener(v -> {
                alertDialog.dismiss();
                shown_dialog = true;
            });
        }
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

    //Configuration Changed
    @SuppressLint("RtlHardcoded")
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        menu.setTextSize(getResources().getInteger(R.integer.font_size_18));
        bomb.setTextSize(getResources().getInteger(R.integer.font_size_18));
        time.setTextSize(getResources().getInteger(R.integer.font_size_18));
        mode.setTextSize(getResources().getInteger(R.integer.font_size_18));
    }

    //handle Back Pressed
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

    //handle on Pause
    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        createCountDownTimer();
        timerStart = false;
    }

    //handle on Restart
    @SuppressLint("DefaultLocale")
    @Override
    protected void onRestart() {
        super.onRestart();
        if(secondsPass == 0){
            minesweeper = null;
            root.removeAllViews();
            textViews = new ArrayList<>();
            read_setting_file();
            createTable();
            smile.setImageResource(R.drawable.face_smile);
            timerStart = false;
            countDownTimer.cancel();
            secondsPass = 0;
            time.setText(R.string.time);
            bomb.setText(String.format("%03d", bomb_number));
        }
    }
}