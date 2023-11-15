package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class addnewnote_fragment extends Fragment {

    EditText subject,topic,description;
    ImageView note,hintimg;
    TextView hinttext;
    Button uploadbtn;
    ProgressBar pbar;

    FirebaseFirestore frstore;
    FirebaseAuth frauth;
    FirebaseStorage frstorage;

    ActivityResultLauncher<String> igallery;
    ActivityResultLauncher<String> ipdf;

    String sub,top,des,img;

    Uri puri;
    Integer notesNo,notesType;
    public addnewnote_fragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view= inflater.inflate(R.layout.fragment_addnewnote_fragment, container, false);
//ID FINDS
       subject=view.findViewById(R.id.subject);
       topic=view.findViewById(R.id.topic);
       description=view.findViewById(R.id.description);
       note=view.findViewById(R.id.postimage);
       uploadbtn=view.findViewById(R.id.uploadbtn);
       pbar=view.findViewById(R.id.progbar);
       hintimg=view.findViewById(R.id.imgplaceholder);
       hinttext=view.findViewById(R.id.hinttext);
//FIREBASE INITIALIZATIONS
        frstore=FirebaseFirestore.getInstance();
        frauth=FirebaseAuth.getInstance();
        frstorage=FirebaseStorage.getInstance();
//READING USER DATA FROM DATABASE
        String uid=frauth.getCurrentUser().getUid();
        DocumentReference dref=frstore.collection("users").document(uid);
        dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                notesNo=Math.toIntExact(value.getLong("notesNo"));
            }
        });

//LAUNCHER ACTIVITY FOR IMAGE AND PDF
        igallery = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        if (result != null) {
                            hintimg.setVisibility(View.GONE);
                            hinttext.setVisibility(View.GONE);
                            note.setImageURI(result);
                            puri=result;
                            notesType=1;
                        }
                    }
                }
        );
        ipdf = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        if (result != null) {
                            hintimg.setVisibility(View.GONE);
                            hinttext.setVisibility(View.GONE);
                            note.setImageResource(R.drawable.pdf2);
                            puri=result;
                            notesType=2;
                        }
                    }
                }
        );
//LISTENER FOR IMAGE VIEW
        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog=new Dialog(getContext());
                dialog.setContentView(R.layout.attach_layout);
                dialog.show();
                Window window=dialog.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                CardView photocard=dialog.findViewById(R.id.photoadd);
                photocard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        igallery.launch("image/*");

                    }
                });
                CardView pdfcard=dialog.findViewById(R.id.pdfadd);
                pdfcard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        ipdf.launch("application/pdf");

                    }
                });
            }
        });
//LISTENER FOR UPLOAD BUTTON
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub=subject.getText().toString().trim();
                top=topic.getText().toString().trim();
                des=description.getText().toString().trim();
                pbar.setVisibility(View.VISIBLE);
                dref.update("notesNo",++notesNo);
                Map<String,Object> notes=new HashMap<>();

                StorageReference ref=frstorage.getReference().child(uid+"Note"+notesNo);
                ref.putFile(puri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                notes.put("Subject", sub);
                                notes.put("Topic", top);
                                notes.put("Description", des);
                                notes.put("uri", uri);
                                notes.put("notesType",notesType);
                                DocumentReference dref1 = frstore.collection("users").document(uid);
                                DocumentReference dref2 = frstore.collection("notes").document(uid+"_noteNo"+String.valueOf(notesNo));
                                dref1.collection("notes").document("noteNo"+String.valueOf(notesNo)).set(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        notes.put("uid",uid);
                                        dref2.set(notes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Tag", "SUCCESS");
                                                pbar.setVisibility(View.GONE);
                                                subject.setText("");
                                                topic.setText("");
                                                description.setText("");
                                                note.setImageDrawable(null);
                                                hintimg.setVisibility(View.VISIBLE);
                                                hinttext.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                });


                                Toast.makeText(getContext(), "Note Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });


       return view;
    }
}