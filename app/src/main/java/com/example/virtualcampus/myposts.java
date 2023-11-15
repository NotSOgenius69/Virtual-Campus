package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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


public class myposts extends Fragment {

    ArrayList<postsClass>userposts=new ArrayList<>();

    FirebaseStorage frstorage;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    RecyclerView recview;
    Integer postNo,postType;
    User user;
    String subject,topic,content,picuri;
    RecyclerAdapterMyposts adapter;
    public myposts() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_myposts, container, false);
//FIREBASE INITIALIZATION
        fstore=FirebaseFirestore.getInstance();
        fauth=FirebaseAuth.getInstance();
        frstorage=FirebaseStorage.getInstance();
//ID FINDS
        recview=view.findViewById(R.id.mypostsrecview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));

        String uid=fauth.getCurrentUser().getUid();
        DocumentReference dref=fstore.collection("users").document(uid);
        CollectionReference postref=fstore.collection("users").document(uid).collection("posts");

        dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                user=new User(value.getString("Name"),value.getString("Country"),value.getString("Institution"),value.getString("profilepic"));
            }
        });
        postref.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    content=documentSnapshot.getString("Content");
                    picuri=documentSnapshot.getString("uri");
                    postType=Math.toIntExact(documentSnapshot.getLong("postType"));
                    postsClass post=new postsClass(content,subject,topic,picuri,postType);
                    userposts.add(post);
                    adapter=new RecyclerAdapterMyposts(getContext(),userposts,user);
                    recview.setAdapter(adapter);

                }

            }
        });




        return view;
    }
}