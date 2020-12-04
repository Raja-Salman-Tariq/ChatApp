package com.rajasalmantariq.a2retry;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    List<Msg> msgs;
    String myid;

    public MsgAdapter(List<Msg> msgs) {
        this.msgs = msgs;
        myid=FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            holder.msg.setBackgroundColor(Color.WHITE);
            holder.msg.setTextColor(Color.BLACK);
        }

        else{
            holder.msg.setBackgroundResource(R.drawable.leftmsg);
            holder.msg.setTextColor(Color.WHITE);
        }
        holder.msg.setText(msgs.get(position).getMsg());


    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public class MsgViewHolder extends RecyclerView.ViewHolder{
        public TextView msg;
        public CircleImageView dp;

        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            this.msg = itemView.findViewById(R.id.leftChatMsg);
            this.dp = itemView.findViewById(R.id.leftChatPic);
        }
    }
}
