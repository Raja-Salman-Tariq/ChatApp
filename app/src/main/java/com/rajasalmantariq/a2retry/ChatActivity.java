package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    public String uid;
    public String myid;

    Button sendBtn, mediaBtn;
    EditText chatMsg;

    DatabaseReference dbRef;

    RecyclerView rv;
    List<Msg> msgs=new ArrayList<>();
    LinearLayoutManager llm;
    MsgAdapter msgAda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uid=getIntent().getStringExtra("uid");
        dbRef=FirebaseDatabase.getInstance().getReference();
        myid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        msgs=new ArrayList<>();
        msgAda=new MsgAdapter(msgs);
        rv=findViewById(R.id.msgsRv);
        llm=new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(msgAda);

        loadMsgs(myid, uid);


        sendBtn=findViewById(R.id.sendBtn);
        mediaBtn=findViewById(R.id.mediaBtn);
        chatMsg=findViewById(R.id.chatMsg);

        dbRef.child("chats").child(myid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(uid)){
                    Map m=new HashMap<>();
                    m.put("seen", false);
                    m.put("timestamp", ServerValue.TIMESTAMP);

                    Map map=new HashMap();
                    map.put("chat/"+myid+"/"+uid, m);
                    map.put("chat/"+uid+"/"+myid, m);

                    dbRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
    }

    public void loadMsgs(String myid, String uid){
        Log.d("Chat", "My Id: "+myid+", his id: "+uid);
        dbRef.child("messages").child(myid).child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Msg msg=dataSnapshot.getValue(Msg.class);
                Log.d("Chat", "onChildAdded: "+msgs.size());
                if (msg!=null) {
                    msgs.add(msg);
                    msgAda.notifyDataSetChanged();
                    Log.d("chat", "msg: "+msg.getMsg()+", seen: "+msg.isSeen()+", time: "+msg.getTime()+", type: "+msg.getType());
                }
                else{
                    Log.d("chat", "msg was null...........");
                }
//                Intent i=new Intent(ChatActivity.this, UsersActivity.class);
//                startActivity(i);

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
    }


    public void sendMsg(){
        String msg=chatMsg.getText().toString();

        if (!TextUtils.isEmpty(msg)){

            String myRef="messages/"+myid+"/"+uid,
                    otherRef="messages/"+uid+"/"+myid;

            DatabaseReference dbR=dbRef.child("messages").child(myid)
                    .child(uid).push();

            String pushId= dbR.getKey();

            Map msgMap=new HashMap();
            msgMap.put("msg", msg);
            msgMap.put("time", ServerValue.TIMESTAMP);
            msgMap.put("seen", false);
            msgMap.put("type","txt");
            msgMap.put("from", myid);

            Map msgUsrMap=new HashMap();
            msgUsrMap.put(myRef+"/"+pushId, msgMap);
            msgUsrMap.put(otherRef+"/"+pushId,msgMap);

            chatMsg.setText("");

            dbRef.updateChildren(msgUsrMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });
;        }
    }
}