package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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

import de.hdodenhof.circleimageview.CircleImageView;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;


public class CreateNewFragment extends Fragment {

    private final int GALLERY_REQ_CODE=100;
    ImageView dp,postpic;
    EditText sub,topic,postcontent;

    ProgressBar pbar;

    TextView usrname,usrinst,usrcont;

    FirebaseFirestore frstore;
    FirebaseAuth frauth;
    FirebaseStorage frstorage;

    de.hdodenhof.circleimageview.CircleImageView   attach;

    ActivityResultLauncher<String> igallery;
    ActivityResultLauncher<String> ipdf;

    Uri puri;
    Button post;
    Boolean attachmentase=false;
    Integer postNo;
    public CreateNewFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_create_new, container, false);

// ID FINDS
        sub=view.findViewById(R.id.subjectinput);
        topic=view.findViewById(R.id.topicinput);
        postcontent=view.findViewById(R.id.contentbox);
        dp=view.findViewById(R.id.profilepicture);
        usrname=view.findViewById(R.id.profilename);
        usrinst=view.findViewById(R.id.profileinst);
        usrcont=view.findViewById(R.id.profilecont);
        attach=view.findViewById(R.id.attachment);
        postpic=view.findViewById(R.id.postimage);
        post=view.findViewById(R.id.postbtn);
        pbar=view.findViewById(R.id.progbar);
//FIREBASE INITIALIZATIONS
        frstore=FirebaseFirestore.getInstance();
        frauth=FirebaseAuth.getInstance();
        frstorage=FirebaseStorage.getInstance();

//READING USER DATA FROM FIRESTORE
        String uid=frauth.getCurrentUser().getUid();
        DocumentReference dref=frstore.collection("users").document(uid);
        dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                usrname.setText(value.getString("Name"));
                usrinst.setText(value.getString("Institution"));
                usrcont.setText(value.getString("Country"));
                postNo= Math.toIntExact(value.getLong("postNo"));
            }
        });

//OPEN IMAGE OR PDF ACTIVITY LAUNCHER
        igallery = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        if (result != null) {
                            postpic.setImageURI(result);
                            puri=result;
                            attachmentase=true;
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
                               postpic.setImageResource(R.drawable.pdf2);
                               puri=result;
                               attachmentase=true;
                            }
                    }
                }
        );
//END OF LAUNCHERS

//LISTENER FOR ATTACH BUTTON
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           //CUSTOM DIALOGUE BOX
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
//END OF LISTENER FOR ATTACH

//LISTENER FOR POST BUTTON
       post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postNo++;
                pbar.setVisibility(view.VISIBLE);
                dref.update("postNo",postNo);
                String subject=sub.getText().toString().trim();
                String Topic=topic.getText().toString().trim();
                String Content=postcontent.getText().toString().trim();
                StorageReference ref=frstorage.getReference().child(uid+"post"+postNo);


                Map<String,Object>userpost=new HashMap<>();
                if(attachmentase) {

                 //UPLOADING IMAGE IN STORAGE AND FIRESTORE
                    ref.putFile(puri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userpost.put("Subject", subject);
                                    userpost.put("Topic", Topic);
                                    userpost.put("Content", Content);
                                    userpost.put("uri", uri);
                                    DocumentReference dref1 = frstore.collection("users").document(uid);
                                    DocumentReference dref2 = frstore.collection("posts").document(uid+"_postNo"+String.valueOf(postNo));
                                    dref1.collection("posts").document("postNo"+String.valueOf(postNo)).set(userpost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dref2.set(userpost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Tag", "SUCCESS");
                                                    pbar.setVisibility(view.GONE);
                                                    sub.setText("");
                                                    topic.setText("");
                                                    postcontent.setText("");
                                                    postpic.setImageDrawable(null);
                                                }
                                            });
                                        }
                                    });


                                    Toast.makeText(getContext(), "Post Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                else {

                    userpost.put("Subject", subject);
                    userpost.put("Topic", Topic);
                    userpost.put("Content", Content);
                    DocumentReference dref = frstore.collection("posts").document(uid);
                    dref.collection("postNo"+String.valueOf(postNo)).add(userpost).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Tag", "SUCCESS");
                            pbar.setVisibility(view.GONE);
                        }
                    });
                    Toast.makeText(getContext(), "Post Uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
//END OF LISTENER FOR POST BUTTON
        return view;

    }



}