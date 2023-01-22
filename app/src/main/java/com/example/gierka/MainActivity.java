package com.example.gierka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    //elementy, rozmiar, pozycja
    private TextView scoreLabel, startLabel;
    private ImageView cow, trawa, sianko, siekierka;

    private int screenWidth;
    private int frameHeight;
    private int cowSize;
    private Button pauseBtn;

    private float cowY;
    private float trawaX, trawaY;
    private float siankoX, siankoY;
    private float siekierkaX, siekierkaY;

    //predkosc
    private int cowSpeed, trawaSpeed, siankoSpeed, siekierkaSpeed;

    //punkty
    private int score;

    //czas
    private Timer timer = new Timer();
    private final Handler handler = new Handler();

    //statusy
    private boolean pause_flg = false;
    private boolean action_flg = false;
    private boolean start_flg = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        cow = findViewById(R.id.cow);
        trawa = findViewById(R.id.trawa);
        sianko = findViewById(R.id.sianko);
        siekierka = findViewById(R.id.siekierka);

        //rozmiarek
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        int screenHeight = size.y;

        //predkosci
        cowSpeed = Math.round(screenHeight / 60.0f);
        trawaSpeed = Math.round(screenWidth / 60.0f);
        siankoSpeed = Math.round(screenWidth / 36.0f);
        siekierkaSpeed = Math.round(screenWidth / 45.0f);

        //pozycje
        trawa.setX(-80.0f);
        trawa.setY(-80.0f);
        sianko.setX(-80.0f);
        sianko.setY(-80.0f);
        siekierka.setX(-80.0f);
        siekierka.setY(-80.0f);

        //wynik
        scoreLabel.setText(getString(R.string.score, score));
        //pauza
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
    }

    public void changePos() {

        hitCheck();

        //trawa
        trawaX -= trawaSpeed;
        if (trawaX < 0) {
            trawaX = screenWidth + 20;
            trawaY = (float) Math.floor(Math.random() * (frameHeight - trawa.getHeight()));
        }
        trawa.setX(trawaX);
        trawa.setY(trawaY);

        //siekierka
        siekierkaX -= siekierkaSpeed;
        if (siekierkaX < 0) {
            siekierkaX = screenWidth + 10;
            siekierkaY = (float) Math.floor(Math.random() * (frameHeight - siekierka.getHeight()));
        }
        siekierka.setX(siekierkaX);
        siekierka.setY(siekierkaY);

        //sianko
        siankoX -= siankoSpeed;
        if (siankoX < 0) {
            siankoX = screenWidth + 5000;
            siankoY = (float) Math.floor(Math.random() * (frameHeight - sianko.getHeight()));
        }
        sianko.setX(siankoX);
        sianko.setY(siankoY);

        //cow
        if (action_flg) {
            // Touching
            cowY -= cowSpeed;
        } else {
            // Releasing
            cowY += cowSpeed;
        }

        if (cowY < 0) cowY = 0;
        if (cowY > frameHeight - cowSize) cowY = frameHeight - cowSize;

        cow.setY(cowY);

        //wynik2
        scoreLabel.setText(getString(R.string.score, score));
    }

    public void hitCheck() {

        //trawa
        float trawaCenterX = trawaX + trawa.getWidth() / 2.0f;
        float trawaCenterY = trawaY + trawa.getHeight() / 2.0f;

        if (0 <= trawaCenterX && trawaCenterX <= cowSize &&
                cowY <= trawaCenterY && trawaCenterY <= cowY + cowSize) {
            trawaX = -100.0f;
            score += 10;
        }

        //sianko
        float siankoCenterX = siankoX + sianko.getWidth() / 2.0f;
        float siankoCenterY = siankoY + sianko.getHeight() / 2.0f;

        if (0 <= siankoCenterX && siankoCenterX <= cowSize &&
                cowY <= siankoCenterY && siankoCenterY <= cowY + cowSize) {
            siankoX = -100.0f;
            score += 30;
        }

        //siekierka
        float siekierkaCenterX = siekierkaX + siekierka.getWidth() / 2.0f;
        float siekierkaCenterY = siekierkaY + siekierka.getHeight() / 2.0f;

        if (0 <= siekierkaCenterX && siekierkaCenterX <= cowSize &&
                cowY <= siekierkaCenterY && siekierkaCenterY <= cowY + cowSize) {

            //przegrana
            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            //przejdz ResultActivity
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!start_flg) {
            start_flg = true;

            FrameLayout frameLayout = findViewById(R.id.frame);
            frameHeight = frameLayout.getHeight();

            //cow
            cowY = cow.getY();
            cowSize = cow.getHeight();

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> changePos());
                }
            }, 0, 20);

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }
        return super.onTouchEvent(event);
    }

    public void pausePushed(View view) {

        if (pause_flg == false) {

            pause_flg = true;

            timer.cancel();
            timer = null;

            //pauza/start
            pauseBtn.setText("START");


        } else {

            pause_flg = false;

            //start/pauza
            pauseBtn.setText("PAUSE");

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);

        }

    }
}
