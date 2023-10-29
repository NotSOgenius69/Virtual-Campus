package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class CreateNewFragment extends Fragment {

    ImageView dp,postpic;
    EditText sub,topic,postcontent;

    TextView usrname,usrinst,usrcont;

    FirebaseFirestore frstore;
    FirebaseAuth frauth;
    public CreateNewFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_create_new, container, false);
        dp=view.findViewById(R.id.profilepicture);
        usrname=view.findViewById(R.id.profilename);
        usrinst=view.findViewById(R.id.profileinst);
        usrcont=view.findViewById(R.id.profilecont);
        frstore=FirebaseFirestore.getInstance();
        frauth=FirebaseAuth.getInstance();

        String uid=frauth.getCurrentUser().getUid();
        DocumentReference dref=frstore.collection("users").document(uid);
        dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                usrname.setText(value.getString("Name"));
                usrinst.setText(value.getString("Institution"));
                usrcont.setText(value.getString("Country"));
            }
        });
        return view;

    }
}