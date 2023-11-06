package com.example.languagetranslator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static final long  DELAY = 30000;

    TextView textView5,liza,fahim,arup;
    Animation top_anim,bottom_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView5=findViewById(R.id.textView5);
        liza=findViewById(R.id.lija);
        fahim=findViewById(R.id.fahim);
        arup=findViewById(R.id.arup);

        Animation fadeInAnimation = new AlphaAnimation(0f, 1f);
        fadeInAnimation.setDuration(2000);
        fadeInAnimation.setFillAfter(true);

        textView5.startAnimation(fadeInAnimation);

        Animation animLiza = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        animLiza.setDuration(1000);
        animLiza.setFillAfter(true);
        arup.startAnimation(animLiza);

        animLiza.setDuration(1000);
        animLiza.setFillAfter(true);
        liza.startAnimation(animLiza);

        animLiza.setDuration(1000);
        animLiza.setFillAfter(true);
        fahim.startAnimation(animLiza);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity or any other desired activity.
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
               finishAffinity(); // Close the splash activity to prevent going back to it.
            }
        }, DELAY);
    }
}