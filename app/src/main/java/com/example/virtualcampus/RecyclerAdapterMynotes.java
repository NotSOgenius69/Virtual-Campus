package com.example.virtualcampus;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapterMynotes extends RecyclerView.Adapter<RecyclerAdapterMynotes.ViewHolder> {

    Context context;
    ArrayList<postsClass> arrlist;
    User userinfo;

    RecyclerAdapterMynotes(Context context, ArrayList<postsClass>arrlist,User userinfo)
    {
        this.context=context;
        this.arrlist=arrlist;
        this.userinfo=userinfo;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.mynotes_row,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterMynotes.ViewHolder holder, int position) {
        holder.Name.setText(userinfo.Name);
        holder.Institute.setText(userinfo.Institute);
        holder.Country.setText(userinfo.Country);
        if(!(userinfo.Profilepic.equals("")))
        {
            Uri dpuri=Uri.parse(userinfo.Profilepic);
            Picasso.get().load(dpuri).placeholder(R.drawable.addimage).into(holder.image);
        }
        holder.Subject.setText(arrlist.get(position).subject);
        holder.Topic.setText(arrlist.get(position).topic);
        holder.Content.setText(arrlist.get(position).content);
        if(!(arrlist.get(position).picuri.equals("")))
        {
            if(arrlist.get(position).postType.equals(1)) {
                Uri dpuri=Uri.parse(arrlist.get(position).picuri);
                Picasso.get().load(dpuri).placeholder(R.drawable.addimage).into(holder.Postimage);
            }
            else {
                holder.Postimage.setImageResource(R.drawable.pdf2);
            }

        }
    }

    @Override
    public int getItemCount() {
        return arrlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView Name,Institute,Country,Subject,Topic,Content;
        ImageView Postimage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.profilepicture);
            Name=itemView.findViewById(R.id.profilename);
            Institute=itemView.findViewById(R.id.profileinst);
            Country=itemView.findViewById(R.id.profilecont);
            Subject=itemView.findViewById(R.id.subject);
            Topic=itemView.findViewById(R.id.topic);
            Content=itemView.findViewById(R.id.content);
            Postimage=itemView.findViewById(R.id.postimage);
        }
    }
}
