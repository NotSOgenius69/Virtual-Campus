package com.example.virtualcampus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    Button signupbtn;
    EditText frstname,lstname,country,institute,email,pass;

    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    String fname,lname,cname,iname,eml,pss;
    ProgressBar pbar;
    String uid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        frstname=findViewById(R.id.FirstName);
        lstname=findViewById(R.id.LastName);
        country=findViewById(R.id.CountryName);
        institute=findViewById(R.id.InstituteName);
        email=findViewById(R.id.Email);
        pass=findViewById(R.id.Pass);
        signupbtn=findViewById(R.id.Signup);
        pbar=findViewById(R.id.progressBar);

        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname=frstname.getText().toString().trim();
                lname=lstname.getText().toString().trim();
                cname=country.getText().toString().trim();
                iname=institute.getText().toString().trim();
                eml=email.getText().toString().trim();
                pss=pass.getText().toString().trim();
                if(fname.isEmpty()||lname.isEmpty()||cname.isEmpty()||iname.isEmpty()||eml.isEmpty()||pss.isEmpty()||pss.length()<6)
                {
                    if(fname.isEmpty())
                    {
                        frstname.setError("This field is required");
                    }
                    if(lname.isEmpty())
                    {
                        lstname.setError("This field is required");
                    }
                    if(cname.isEmpty())
                    {
                        country.setError("This field is required");
                    }
                    if(iname.isEmpty())
                    {
                        institute.setError("This field is required");
                    }
                    if(eml.isEmpty())
                    {
                        email.setError("This field is required");
                    }
                    if(pss.isEmpty())
                    {
                        pass.setError("This field is required");
                    }
                    if(pss.length()<6)
                    {
                        pass.setError("Password must be at least 6 characters");
                    }
                    return;
                }


                    pbar.setVisibility(View.VISIBLE);
                    fauth.createUserWithEmailAndPassword(eml,pss).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                uid=fauth.getCurrentUser().getUid();
                                 Map<String,Object> userinfo=new HashMap<>();
                                 userinfo.put("Name",fname+" "+lname);
                                 userinfo.put("Country",cname);
                                 userinfo.put("Institution",iname);
                                 userinfo.put("Email",eml);
                                 userinfo.put("Password",pss);
                                 userinfo.put("postNo","0");
                                DocumentReference docref=fstore.collection("users").document(uid);
                                 docref.set(userinfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void unused) {
                                         Log.d("Tag","SUCCESS");
                                     }
                                 });
                                Toast.makeText(SignUp.this, "Signed Up", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUp.this,MainActivity.class));
                                finish();

                            }
                            else
                            {
                                pbar.setVisibility(View.GONE);
                                Toast.makeText(SignUp.this, "Registration Failed. "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



            }
        });


    }
}