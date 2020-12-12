package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.StringTokenizer;

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
    String url="http://192.168.1.2/chatapp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        uid=getIntent().getStringExtra("uid");
//        dbRef=FirebaseDatabase.getInstance().getReference();
//        myid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        myid=getIntent().getStringExtra("myid");

        msgs=new ArrayList<>();
        msgAda=new MsgAdapter(msgs, myid);
        rv=findViewById(R.id.msgsRv);
        llm=new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(msgAda);

        loadMsgs();


        sendBtn=findViewById(R.id.sendBtn);
        mediaBtn=findViewById(R.id.mediaBtn);
        chatMsg=findViewById(R.id.chatMsg);

//        dbRef.child("chats").child(myid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if (!dataSnapshot.hasChild(uid)){
//                    Map m=new HashMap<>();
//                    m.put("seen", false);
//                    m.put("timestamp", ServerValue.TIMESTAMP);
//
//                    Map map=new HashMap();
//                    map.put("chat/"+myid+"/"+uid, m);
//                    map.put("chat/"+uid+"/"+myid, m);
//
//                    dbRef.updateChildren(map, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


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
//                msgs=new ArrayList<>();
//                loadMsgs();
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

    void handleMsgs(String str){
        StringTokenizer stok1= new StringTokenizer(str, "#");
        String holder1="";
        Log.d("get msg", "handleMsgs: before while"+str);

//        msgs=new ArrayList<>();

        while (stok1.hasMoreTokens()){
            Log.d("get msg", "handleMsgs: in while");
            holder1=stok1.nextToken();

            if (!holder1.equals("")){
                Msg m=new Msg(holder1);
                msgs.add(m);
                msgAda.notifyDataSetChanged();
            }
        }

    }

    public void loadMsgs(){
        Log.d("Chat", "My Id: "+myid+", his id: "+uid);

        StringRequest req=new StringRequest(
                Request.Method.POST,
                url+"getMsgs.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        handleMsgs(response);

                        Log.d("responceChatAct", "onResponse get msg: "+response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("responceChatAct", "onError: "+error.getMessage());


                if (error != null && error.networkResponse!=null){
                    Toast.makeText(ChatActivity.this,
                            "ERROR: " + error.getMessage() +
                                    ", \nResponce: " + error.networkResponse.statusCode +
                                    ",\nData: " + new String(error.networkResponse.data),
                            Toast.LENGTH_LONG)
                            .show();
                }

                else{
                    Toast.makeText(ChatActivity.this, "ERROR: " + error.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                map.put("id",uid);
                map.put("myId", myid);
                return map;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(ChatActivity.this);

        requestQueue.add(req);

//        dbRef.child("messages").child(myid).child(uid).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Msg msg=dataSnapshot.getValue(Msg.class);
//                Log.d("Chat", "onChildAdded: "+msgs.size());
//                if (msg!=null) {
//                    msgs.add(msg);
//                    msgAda.notifyDataSetChanged();
//                    Log.d("chat", "msg: "+msg.getMsg()+", seen: "+msg.isSeen()+", time: "+msg.getTime()+", type: "+msg.getType());
//                }
//                else{
//                    Log.d("chat", "msg was null...........");
//                }
////                Intent i=new Intent(ChatActivity.this, UsersActivity.class);
////                startActivity(i);
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


    public void sendMsg(){
        final String msg=chatMsg.getText().toString();


        if (!TextUtils.isEmpty(msg)){
            Log.d("chatAct", "sendMsg: "+msg);

            StringRequest req=new StringRequest(
                    Request.Method.POST,
                    url+"sendMsg.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("responceChatAct", "onResponse: "+response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("responceChatAct", "onError: "+error.getMessage());


                    if (error != null && error.networkResponse!=null){
                        Toast.makeText(ChatActivity.this,
                                "ERROR: " + error.getMessage() +
                                        ", \nResponce: " + error.networkResponse.statusCode +
                                        ",\nData: " + new String(error.networkResponse.data),
                                Toast.LENGTH_LONG)
                                .show();
                    }

                    else{
                        Toast.makeText(ChatActivity.this, "ERROR: " + error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            )
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map=new HashMap<>();
                    map.put("id",uid);
                    map.put("myId", myid);
//                    Log.d(TAG, "getParams: ");
                    map.put("msg", msg.trim());
                    map.put("type", "txt");
                    return map;
                }
            };

            RequestQueue requestQueue= Volley.newRequestQueue(ChatActivity.this);

            requestQueue.add(req);

            Msg m=new Msg(msg, "txt",false,0,myid);
            msgs.add(m);
            msgAda.notifyDataSetChanged();
            Log.d("ada", "sendMsg: "+msgs.size());


            chatMsg.setText("");

;        }
    }
}