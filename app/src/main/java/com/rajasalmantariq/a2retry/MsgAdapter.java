package com.rajasalmantariq.a2retry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder> {

    List<Msg> msgs;

    public MsgAdapter(List<Msg> msgs) {
        this.msgs = msgs;
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
