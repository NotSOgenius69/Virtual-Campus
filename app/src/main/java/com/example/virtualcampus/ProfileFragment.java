package com.example.virtualcampus;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;





public class ProfileFragment extends Fragment {

    CircleImageView editpen,dp;
    ImageView plusbtn;
    ActivityResultLauncher<String> igallery;
    FirebaseStorage frstorage;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    Uri puri;
    TextView usrname,usrinst,usrcont,postscnt,notescnt,gcoinscnt,ycoinscnt;

    Toolbar tb;
    TextView tbtitle;
    CardView myposts,mynotes;

    Integer SpendCoins;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        editpen=view.findViewById(R.id.pen);
        usrname=view.findViewById(R.id.textView2);
        usrinst=view.findViewById(R.id.profileinst);
        usrcont=view.findViewById(R.id.profilecont);
        postscnt=view.findViewById(R.id.postcount);
        notescnt=view.findViewById(R.id.notescount);
        gcoinscnt=view.findViewById(R.id.gcoincount);
        ycoinscnt=view.findViewById(R.id.ycoincount);
        plusbtn=view.findViewById(R.id.plusbtn);
        myposts=view.findViewById(R.id.mypostscard);
        mynotes=view.findViewById(R.id.mynotescard);
        dp=view.findViewById(R.id.dp);

        tb= ((MainActivity) requireActivity()).getMainToolbar();
        tbtitle=tb.findViewById(R.id.toolbartitle);

//FIREBASE INITIALIZATIONS
        fstore=FirebaseFirestore.getInstance();
        fauth=FirebaseAuth.getInstance();
        frstorage=FirebaseStorage.getInstance();

//READING USER DATA FROM FIRESTORE
        String uid=fauth.getCurrentUser().getUid();
        DocumentReference dref=fstore.collection("users").document(uid);
        StorageReference ref=frstorage.getReference().child(uid+"profilepic");
        dref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.getString("profilepic")!="")
                {
                    Uri dpuri=Uri.parse(value.getString("profilepic"));
                    Picasso.get().load(dpuri).placeholder(R.drawable.useravatar).into(dp);
                }
                else
                {
                   dp.setImageResource(R.drawable.useravatar);
                }
                usrname.setText(value.getString("Name"));
                usrinst.setText(value.getString("Institution"));
                usrcont.setText(value.getString("Country"));
                postscnt.setText(value.getLong("postNo").toString());
                notescnt.setText(value.getLong("notesNo").toString());
                gcoinscnt.setText(value.getLong("earnedCoins").toString());
                ycoinscnt.setText(value.getLong("spendCoins").toString());
                SpendCoins=Math.toIntExact(value.getLong("spendCoins"));
            }
        });

 //CHOOSING PROFILE IMAGE

        igallery = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        if (result != null) {
                            ref.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            dref.update("profilepic",uri.toString());
                                            dp.setImageURI(uri);
                                            Toast.makeText(getContext(), "Profile Picture Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            puri=result;
                        }
                    }
                }
        );
        editpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                igallery.launch("image/*");
            }
        });

//LISTENER FOR ADD COIN BUTTON
        plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialogue=new Dialog(getContext());
                dialogue.setContentView(R.layout.add_coins);
                dialogue.show();
                Window window=dialogue.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                EditText edcoins=dialogue.findViewById(R.id.edcoins);
                Button buybtn=dialogue.findViewById(R.id.buybtn);


                buybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String coinsamount=edcoins.getText().toString();
                        Integer extra=Integer.parseInt(coinsamount);
                        SpendCoins+=extra;
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setTitle("Confirm Purchase");
                        builder.setMessage("Are you sure you want to buy "+coinsamount+" coins?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dref.update("spendCoins",SpendCoins).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Coins added", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.dismiss();
                                dialogue.dismiss();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialogue.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }
        });
//END OF LISTENER FOR ADD COINS BUTTON

//LISTENER FOR MY POSTS
        myposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 loadfrag(new myposts(),false,"My Posts");
            }
        });
        mynotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadfrag(new mynotes(),false,"My Notes");
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