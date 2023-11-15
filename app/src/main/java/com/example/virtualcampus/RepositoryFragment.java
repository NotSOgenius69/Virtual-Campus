package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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


public class RepositoryFragment extends Fragment {

    RecyclerView recview;

    ArrayList<postsClass> repository=new ArrayList<>();
    ArrayList<User> Users=new ArrayList<>();
    FirebaseStorage frstorage;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    SearchView searchView;
    RecyclerView recView;

    LottieAnimationView ltview;
    TextView textView;
    String subject,topic,content,picuri;

    Integer postType;
    String userid;

    User user;
    RepositoryRecAdapter adapter;

    public RepositoryFragment() {
        // Required empty public constructor
    }



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_repository, container, false);

//ID FINDS
        searchView=view.findViewById(R.id.search);
        searchView.clearFocus();
        recview=view.findViewById(R.id.reporecview);
        ltview=view.findViewById(R.id.lottieview);
        textView=view.findViewById(R.id.nodatatext);
//FIREBASE INITIALIZATIONS
        fstore= FirebaseFirestore.getInstance();
        fauth= FirebaseAuth.getInstance();
        frstorage= FirebaseStorage.getInstance();



//FETCHING DATA FROM DATABASE
        String uid=fauth.getCurrentUser().getUid();
        CollectionReference postref=fstore.collection("notes");
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
                    //postType=Math.toIntExact(documentSnapshot.getLong("notesType"));
                    postsClass post=new postsClass(content,subject,topic,picuri,1);
                    repository.add(post);
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
//SEARCH BAR
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return true;
            }
        });

        adapter=new RepositoryRecAdapter(getContext(),repository,Users);
        recview.setAdapter(adapter);

        return view;
    }
    public void filterList(String text)
    {
        ArrayList<postsClass>filteredList=new ArrayList<>();
        for(postsClass post:repository)
        {
            if(post.subject.toLowerCase().contains(text.toLowerCase())||post.topic.toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(post);
            }
        }
        adapter.setFilteredList(filteredList);
        if(filteredList.isEmpty())
        {
            recview.setVisibility(View.GONE);
            ltview.setVisibility(View.VISIBLE);
            ltview.setAnimation(R.raw.nodatafound);
            ltview.playAnimation();
            textView.setVisibility(View.VISIBLE);
        }
        else
        {
            ltview.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            recview.setVisibility(View.VISIBLE);
        }
    }
}