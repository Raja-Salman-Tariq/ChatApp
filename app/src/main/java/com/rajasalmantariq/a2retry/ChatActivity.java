package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    public String uid;
    public String myid;

    ImageView sendBtn, mediaBtn;
    EditText chatMsg;

    DatabaseReference dbRef;

    RecyclerView rv;
    List<Msg> msgs=new ArrayList<>();
    LinearLayoutManager llm;
    MsgAdapter msgAda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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


        mediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(i,"Selecti an Image..."),12345);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode==12345){
            Uri imgUri=data.getData();

            final String myRef="messages/"+myid+"/"+uid,
                    otherRef="messages/"+uid+"/"+myid;

            final DatabaseReference dbR=dbRef.child("messages").child(myid)
                    .child(uid).push();

            final String pushId= dbR.getKey();

            StorageReference stoRef= FirebaseStorage.getInstance().getReference()
                    .child("msg_imgs").child(pushId+".jpg");

            stoRef.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String msg=uri.toString();
                                Map msgMap = new HashMap();
                                msgMap.put("msg", msg);
                                msgMap.put("time", ServerValue.TIMESTAMP);
                                msgMap.put("seen", false);
                                msgMap.put("type", "img");
                                msgMap.put("from", myid);

                                Map msgUsrMap = new HashMap();
                                msgUsrMap.put(myRef + "/" + pushId, msgMap);
                                msgUsrMap.put(otherRef + "/" + pushId, msgMap);


                                dbRef.updateChildren(msgUsrMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    }
                                });
                            }
                        });

                    }

                }
            });
        }
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