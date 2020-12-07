package com.rajasalmantariq.a2retry;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    List<Msg> msgs;
    String myid;
    Context c;

    public MsgAdapter(List<Msg> msgs) {
        this.msgs = msgs;
        myid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.c=c;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leftchat, parent, false);
        return new MsgViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {

        if (msgs.get(position).getFrom().equals(myid)){
//            holder.msg.setBackgroundColor(#00D664);
//            holder.msg.setTextColor(Color.BLACK);
            holder.msg.setBackgroundResource(R.drawable.rightmsg);
            holder.msg.setTextColor(Color.WHITE);
            holder.dp.setVisibility(View.INVISIBLE);
        }

        else{
            holder.msg.setBackgroundResource(R.drawable.leftmsg);
            holder.msg.setTextColor(Color.WHITE);

            final ImageView i=holder.dp;
            FirebaseStorage.getInstance().getReference()
                    .child("thumbs").child(msgs.get(position).getFrom()+".jpg")
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("uri", "onSuccess: "+uri.toString());
                    Picasso.get().load(uri.toString())
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .into(i);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("uri", "onFailure: ");
                }
            })
            ;

        }

        if (msgs.get(position).getType().equals("txt")) {
            holder.msg.setVisibility(View.VISIBLE);
            holder.msg.setText(msgs.get(position).getMsg());
            holder.img.setVisibility(View.INVISIBLE);
        }
        else{
            holder.img.setVisibility(View.VISIBLE);
            holder.msg.setVisibility(View.INVISIBLE);

            Picasso.get().load(msgs.get(position).getMsg()).placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.img);
        }


    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public class MsgViewHolder extends RecyclerView.ViewHolder{
        public TextView msg;
        public CircleImageView dp;
        public ImageView img;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            this.msg = itemView.findViewById(R.id.leftChatMsg);
            this.dp = itemView.findViewById(R.id.leftChatPic);
            this.img=itemView.findViewById(R.id.imgMsg);
        }
    }
}
