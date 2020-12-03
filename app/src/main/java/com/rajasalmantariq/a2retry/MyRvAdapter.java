package com.rajasalmantariq.a2retry;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyRvAdapter extends RecyclerView.Adapter<MyRvAdapter.MyViewHolder> {
    List<Users> ls;
    Context c;
    String usr, email;

    public MyRvAdapter(List<Users> ls, Context c, String email) {
        this.c=c;
        this.ls=ls;
        this.email=email;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemrow= LayoutInflater.from(c).inflate(R.layout.row,parent,false);
        return new  MyViewHolder(itemrow);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.name.setText(ls.get(position).getName());
        holder.phno.setText(ls.get(position).getStatus());
        holder.email.setText(ls.get(position).getImage());
        Picasso.get().load(ls.get(position).getImage()).into(holder.rowDp);

//        holder.email.getText().toString()
        if (this.usr==null && holder.email.getText().toString().contentEquals(this.email)){
            usr=holder.name.getText().toString();
            Log.d("ABC", holder.email.getText().toString()+"--"+this.email);

        }
        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Toast.makeText(
//                        "Deleting Chat: "+holder.name.getText().toString(),
//                        Toast.LENGTH_LONG
//                ).show();
                return false;
            }
        });
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i=new Intent(c, ChatActivity.class);
//                i.putExtra("rname", holder.name.getText());
//                i.putExtra("uname",usr);
//                c.startActivity(i);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return ls.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,phno,email;
        CircleImageView rowDp;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rowDp=itemView.findViewById(R.id.rowDp);
            name=itemView.findViewById(R.id.namee);
            phno=itemView.findViewById(R.id.phno);
            email=itemView.findViewById(R.id.email);
        }

        public TextView getName() {
            return name;
        }

        public TextView getPhno() {
            return phno;
        }

        public TextView getEmail() {
            return email;
        }

        public CircleImageView getRowDp() {
            return rowDp;
        }
    }
}
