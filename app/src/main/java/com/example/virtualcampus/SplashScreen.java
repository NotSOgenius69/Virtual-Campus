package com.example.virtualcampus;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    ImageView im;
    TextView tv;
    LottieAnimationView lv;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        im=findViewById(R.id.imageView);
        tv=findViewById(R.id.textView);
        lv=findViewById(R.id.lottieAnimationView);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
                if(pref.getBoolean("flag",false))
                {
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(SplashScreen.this,Login.class));
                    finish();
                }
            }
        },6000);

        Display display=getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        int screenHeight=size.y;
        AnimationSet s=new AnimationSet(false);
        Animation scale= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.scale);
        Animation dscale= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.descale);
        s.addAnimation(scale);
        s.addAnimation(dscale);
        s.setFillAfter(true);
        im.startAnimation(s);
        AnimationSet s2=new AnimationSet(false);
        Animation fd= AnimationUtils.loadAnimation(SplashScreen.this,R.anim.txtfade);
        Animation move=AnimationUtils.loadAnimation(SplashScreen.this,R.anim.txtmove);
        s2.addAnimation(fd);
        s2.addAnimation(move);
        s2.setFillAfter(true);
        tv.startAnimation(s2);


        Animation ltscale=AnimationUtils.loadAnimation(SplashScreen.this,R.anim.lottiescale);
        lv.startAnimation(ltscale);
    }
}