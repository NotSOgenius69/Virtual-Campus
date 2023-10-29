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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    DrawerLayout dlay;
    NavigationView navview;

    Toolbar toolbar;
    ImageView hamicon,newpost;
    TextView tbtitle;
    me.ibrahimsn.lib.SmoothBottomBar btnav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finding ids
        dlay=findViewById(R.id.dlayout);
        navview=findViewById(R.id.naviview);
        toolbar=findViewById(R.id.tbar);
        hamicon=findViewById(R.id.hamburgericon);
        btnav=findViewById(R.id.bottomBar);
        tbtitle=findViewById(R.id.toolbartitle);
        newpost=findViewById(R.id.Createnew);


        newpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadfrag(new CreateNewFragment(),false,"Create Post");
            }
        });
//TOOLBAR SETUP//START

      //setting toolbar and nav drawer
      setSupportActionBar(toolbar);
       getSupportActionBar().setTitle(null);

//TOOLBAR SETUP//FINISH

//NAVIGATION DRAWER SETUP//START
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MainActivity.this,dlay,toolbar,R.string.OpenDrawer,R.string.CloseDrawer);
        dlay.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        //Listener for nav drawer
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
        //Item select listener for nav menu
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

//NAVIGATION DRAWER SETUP//FINISH

//BOTTOM NAVIGATION BAR//START

        btnav.setItemActiveIndex(1);
        loadfrag(new DiscussionFragment(),true,"Discussions");
        btnav.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int indx) {
                if(indx==0)
                {
                    loadfrag(new RepositoryFragment(),false,"Repository");
                }
                else if (indx==1)
                {
                    loadfrag(new DiscussionFragment(),false,"Discussions");
                }
                else if(indx==2)
                {
                    loadfrag(new ProfileFragment(),false,"Profile");
                }

                return true;
            }
        });

//BOTTOM NAVIGATION BAR//FINISH


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

    public void loadfrag(Fragment fragment,Boolean flg,String title)
    {
        tbtitle.setText(title);
        if(!(fragment instanceof DiscussionFragment))
        {
            newpost.setVisibility(View.GONE);
        }
        else
            newpost.setVisibility(View.VISIBLE);
        FragmentManager fragman=getSupportFragmentManager();
        FragmentTransaction fragtrn=fragman.beginTransaction();
        if(flg)
            fragtrn.add(R.id.container,fragment);
        else
            fragtrn.replace(R.id.container,fragment);



        fragtrn.commit();

    }
}