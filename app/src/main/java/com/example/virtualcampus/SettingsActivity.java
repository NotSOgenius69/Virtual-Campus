package com.example.virtualcampus;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    ArrayList<String>listitem=new ArrayList<>();
    Toolbar tb;
    TextView tbtitle;
    ImageView createnew,hamicon,back,cancel;
    ListView listView;
    EditText currpass,newpass,repass;
    Button updatebtn;
    String currpassstr,newpassstr,repassstr;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//ID FINDS
        tb=findViewById(R.id.tbar);
        tbtitle=tb.findViewById(R.id.toolbartitle);
        back=tb.findViewById(R.id.backbtn2);
        hamicon=tb.findViewById(R.id.hamburgericon);
        createnew=tb.findViewById(R.id.Createnew);
        listView=findViewById(R.id.list);


//TOOLBAR SETUP
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(null);
        tbtitle.setText("Settings");
        createnew.setVisibility(View.GONE);
        hamicon.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);

//LISTENER FOR BACK BUTTON
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,MainActivity.class));
            }
        });

        listitem.add("Change Password");
        ArrayAdapter<String>adapter=new ArrayAdapter<>(SettingsActivity.this, me.ibrahimsn.lib.R.layout.support_simple_spinner_dropdown_item,listitem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    changepassword();
                }
            }
        });
    }
    public void changepassword()
    {
        Dialog dialogue=new Dialog(SettingsActivity.this);
        dialogue.setContentView(R.layout.passchange);
        dialogue.show();
        Window window=dialogue.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        currpass=dialogue.findViewById(R.id.oldpassword);
        newpass=dialogue.findViewById(R.id.newpassword);
        repass=dialogue.findViewById(R.id.reenterpassword);
        updatebtn=dialogue.findViewById(R.id.updatebtn);
        cancel=dialogue.findViewById(R.id.cancel);



        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currpassstr=currpass.getText().toString().trim();
                newpassstr=newpass.getText().toString().trim();
                repassstr=repass.getText().toString().trim();
                if((currpassstr.isEmpty())||(TextUtils.isEmpty(newpassstr))||(TextUtils.isEmpty(repassstr))) {
                    if (currpassstr.isEmpty()) {
                        currpass.setError("Field can't be empty");
                    }
                    if (TextUtils.isEmpty(newpassstr)) {
                        newpass.setError("Field can't be empty");
                    }
                    if (TextUtils.isEmpty(repassstr)) {
                        repass.setError("Field can't be empty");
                    }
                }
                else if(newpassstr.length()<6)
                {
                    Toast.makeText(SettingsActivity.this, "New password must be of minimum length 6", Toast.LENGTH_SHORT).show();
                }
                else if(newpassstr.compareTo(repassstr)!=0)
                {
                    currpass.setText("");
                    newpass.setText("");
                    repass.setText("");
                    Toast.makeText(SettingsActivity.this, "Passwords didn't match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    updatepass(currpassstr,newpassstr);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogue.dismiss();
            }
        });

    }
    public void updatepass(String oldpass,String newpass)
    {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential cred= EmailAuthProvider.getCredential(user.getEmail(),oldpass);

        user.reauthenticate(cred).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                   user.updatePassword(newpass).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                           Dialog dgue=new Dialog(SettingsActivity.this);
                           dgue.setContentView(R.layout.updated);
                           dgue.show();
                           Window window=dgue.getWindow();
                           window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                       }
                   });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
            }
        });
    }


}