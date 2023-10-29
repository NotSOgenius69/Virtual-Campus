package com.example.virtualcampus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView reg;
    Button loginbtn;
    FirebaseAuth auth;
    ProgressBar prbar;
    EditText eml,pss;
    String logemail,logpass;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        reg=findViewById(R.id.regi);
        loginbtn=findViewById(R.id.Login);
        prbar=findViewById(R.id.progressBar2);
        eml=findViewById(R.id.loginemail);
        pss=findViewById(R.id.loginpass);
        auth=FirebaseAuth.getInstance();


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logemail=eml.getText().toString().trim();
                logpass=pss.getText().toString().trim();
                if(logemail.isEmpty()||logpass.isEmpty())
                {
                    if(logemail.isEmpty())
                    {
                        eml.setError("This field is required");
                    }
                    if(logpass.isEmpty())
                    {
                        pss.setError("This field is required");
                    }
                    return;
                }
                prbar.setVisibility(View.VISIBLE);
                SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putBoolean("flag",true);
                editor.apply();
                auth.signInWithEmailAndPassword(logemail,logpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Login.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this,MainActivity.class));
                            finish();
                        }
                        else
                        {
                            prbar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Login failed! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,SignUp.class));
            }
        });
    }
}