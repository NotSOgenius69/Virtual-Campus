package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class mynotes extends Fragment {
    ArrayList<postsClass> usernotes=new ArrayList<>();
    FirebaseStorage frstorage;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    RecyclerView recview;
    ImageView addnote;
    Toolbar tb;
    TextView tbtitle;
    Integer postNo,notesType;

    String subject,topic,description,picuri;
    RecyclerAdapterMynotes adapter;

    User user;
    public mynotes() {
        // Required empty public constructor
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_mynotes, container, false);
        addnote=view.findViewById(R.id.add_note_clickable);

        tb= ((MainActivity) requireActivity()).getMainToolbar();
        tbtitle=tb.findViewById(R.id.toolbartitle);
        addnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadfrag(new addnewnote_fragment(),false,"Upload Note");
            }
        });
//FIREBASE INITIALIZATION
        fstore= FirebaseFirestore.getInstance();
        fauth= FirebaseAuth.getInstance();
        frstorage= FirebaseStorage.getInstance();
//ID FINDS
        recview=view.findViewById(R.id.mynotesrecview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));

        String uid=fauth.getCurrentUser().getUid();
        DocumentReference dref=fstore.collection("users").document(uid);
        CollectionReference notesref=fstore.collection("users").document(uid).collection("notes");

        dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                user=new User(value.getString("Name"),value.getString("Country"),value.getString("Institution"),value.getString("profilepic"));
            }
        });
        notesref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    return;
                }
                for(QueryDocumentSnapshot documentSnapshot:value)
                {

                    subject= documentSnapshot.getString("Subject");
                    topic= documentSnapshot.getString("Topic");
                    description=documentSnapshot.getString("Description");
                    picuri=documentSnapshot.getString("uri");
                    //notesType=Math.toIntExact(documentSnapshot.getLong("notesType"));
                    postsClass post=new postsClass(description,subject,topic,picuri,1);
                    usernotes.add(post);
                    adapter=new RecyclerAdapterMynotes(getContext(),usernotes,user);
                    recview.setAdapter(adapter);

                }

            }
        });



        return view;
    }
    public void loadfrag(Fragment fragment,Boolean flg,String title)
    {
        tbtitle.setText(title);

        FragmentManager fragman=requireActivity().getSupportFragmentManager();
        FragmentTransaction fragtrn=fragman.beginTransaction();
        if(flg)
            fragtrn.add(R.id.container,fragment);
        else
            fragtrn.replace(R.id.container,fragment);



        fragtrn.commit();

    }
}