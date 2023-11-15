package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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


public class DiscussionFragment extends Fragment {

    RecyclerView recview;

    ArrayList<postsClass> discussions=new ArrayList<>();
    ArrayList<User> Users=new ArrayList<>();

    FirebaseStorage frstorage;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;

    String subject,topic,content,picuri;

    Integer postType;
    String userid;

    User user;
    DiscussionsRecAdapter adapter;
    public DiscussionFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_discussion, container, false);
//ID FINDS
        recview=view.findViewById(R.id.discussionsrecview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
//FIREBASE INITIALIZATIONS
        fstore=FirebaseFirestore.getInstance();
        fauth=FirebaseAuth.getInstance();
        frstorage=FirebaseStorage.getInstance();
//FETCHING DATA FROM DATABASE
        String uid=fauth.getCurrentUser().getUid();
        CollectionReference postref=fstore.collection("posts");
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
                    userid=documentSnapshot.getString("uid");
                    postType=Math.toIntExact(documentSnapshot.getLong("postType"));
                    postsClass post=new postsClass(content,subject,topic,picuri,postType);
                    discussions.add(post);
                    DocumentReference dref=fstore.collection("users").document(userid);
                    dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            user=new User(value.getString("Name"),value.getString("Country"),value.getString("Institution"),value.getString("profilepic"));
                            Users.add(user);
                            adapter.notifyDataSetChanged();
                        }
                    });

                }

            }
        });

        adapter=new DiscussionsRecAdapter(getContext(),discussions,Users);
        recview.setAdapter(adapter);
        return view;
    }
}