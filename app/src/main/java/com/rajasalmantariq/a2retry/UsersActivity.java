package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    RecyclerView rv;
    DatabaseReference dbRef;
    List<Users> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        dbRef= FirebaseDatabase.getInstance().getReference().child("users");

        users=new ArrayList<>();
        final MyRvAdapter adapter=new MyRvAdapter(users,this,
                FirebaseAuth.getInstance().getCurrentUser().getEmail());

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                users.add(
                        dataSnapshot.getValue(Users.class)
                );
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rv=findViewById(R.id.usersList);
//        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

//        FirebaseRecyclerOptions<Users> options =
//                new FirebaseRecyclerOptions.Builder<Users>()
//                        .setQuery(dbRef, Users.class)
//                        .build();

//        FirebaseRecyclerAdapter<Users,UsersViewHolder > ada=
//                new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
//                        holder.name.setText(/*ls.get(position)*/model.getName());
////                        holder.phno.setText(ls.get(position).getPhno());
////                        holder.email.setText(ls.get(position).getEmail());
//                        holder.status.setText(model.getStatus());
//                        Picasso.get().load(/*ls.get(position)*/model.getImage()).into(holder.rowDp);
//                    }
//
//                    @NonNull
//                    @Override
//                    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View itemrow= LayoutInflater.from(UsersActivity.this).inflate(R.layout.row,parent,false);
//                        return new UsersViewHolder(itemrow);
//
////                        return null;
//                    }
//                };

//        rv.setAdapter(ada);
    }

//    public static class UsersViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//        CircleImageView rowDp;
//        TextView name, status;
//
//        public UsersViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            mView=itemView;
//            rowDp=itemView.findViewById(R.id.rowDp);
//            name=itemView.findViewById(R.id.namee);
////            phno=itemView.findViewById(R.id.phno);
////            email=itemView.findViewById(R.id.email);
//            status=itemView.findViewById(R.id.status);
//        }
//
//
//        public void setmView(View mView) {
//            this.mView = mView;
//        }
//
//        public void setRowDp(CircleImageView rowDp) {
//            this.rowDp = rowDp;
//        }
//
//        public void setName(TextView name) {
//            this.name = name;
//        }
//
//        public void setStatus(TextView status) {
//            this.status = status;
//        }
//    }

}