package com.example.virtualcampus;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    DrawerLayout dlay;
    NavigationView navview;

    Toolbar toolbar;
    ImageView hamicon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dlay=findViewById(R.id.dlayout);
        navview=findViewById(R.id.naviview);
        toolbar=findViewById(R.id.tbar);
        hamicon=findViewById(R.id.hamburgericon);

      setSupportActionBar(toolbar);
       getSupportActionBar().setTitle(null);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MainActivity.this,dlay,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        dlay.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        /*Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bars, MainActivity.this.getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dlay.isDrawerVisible(GravityCompat.START)) {
                    dlay.closeDrawer(GravityCompat.START);
                } else {
                    dlay.openDrawer(GravityCompat.START);
                }
            }
        });*/
        hamicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dlay.isDrawerVisible(GravityCompat.START)) {
                    dlay.closeDrawer(GravityCompat.START);
                } else {
                    dlay.openDrawer(GravityCompat.START);
                }
            }
        });


        navview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.userprofile)
                {

                }
                else if (id==R.id.settings)
                {

                }
                else if(id==R.id.logout)
                {
                    logout();
                }

                dlay.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }
    public void logout()
    {
        logoutdialoguebox();
    }
    public void logoutdialoguebox()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putBoolean("flag",false);
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,Login.class));
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}