package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    /*dummy comment for dummy new branch commit*/
//    private FirebaseAuth mAuth;
    Button logOutBtn, settingsButton, usersButton, addFrnBtn;
    String url="http://192.168.1.3/chatapp/isLoggedIn.php";

    String myNum;

//    boolean noCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("abc", "main  act");
//        mAuth = FirebaseAuth.getInstance();
        logOutBtn=findViewById(R.id.logOutButton);
        settingsButton=findViewById(R.id.settingsButton);
        usersButton=findViewById(R.id.usersButton);
        addFrnBtn=findViewById(R.id.addFriend);

        myNum="";

        try{
            myNum=getIntent().getStringExtra("MyNumFromRegisterActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("myNum", "onCreate: setting it to empty");
//        checkIfLoggedIn();

        Log.d("myNum2", "onCreate: "+myNum);

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                Intent i=new Intent(MainActivity.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfLoggedIn();
//                Intent i=new Intent(MainActivity.this,SettingsActivity.class);
//                startActivity(i);
//                finish();
            }
        });

        usersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, UsersActivity.class);
                startActivity(i);
            }
        });

        addFrnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, 700);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkIfLoggedIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==700 && resultCode==RESULT_OK){
            CursorLoader loader = new CursorLoader(this, data.getData(), null, null, null, null);
            Cursor c=loader.loadInBackground();

            if (c.moveToFirst()){
                final String numba=c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                final String otherUid;

                FirebaseDatabase.getInstance().getReference().child("number-user-map")
                        .child(numba).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String otherUid=dataSnapshot.getValue(String.class);
                                Log.d("Main:AddFriend:Contact", "received numba: "+numba+", otter uid: "+otherUid);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }else{
                Log.d("Main:AddFriend:Contact:", "Failed to read contact...");
            }

        }
    }


    void checkIfLoggedIn(){

        StringRequest req=new StringRequest(
                Request.Method.POST,
//                JsonRequest.Method.POST,
                url,
//                null,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("responce", "onResponse: "+response);

                        if (!response.equals("")){
                            Toast.makeText(
                                    MainActivity.this,
                                    "Logged in as user with number: "+response,
                                    Toast.LENGTH_LONG
                                    ).show();

//                            noCurrentUser=false;
                            myNum=response;
                            if (myNum.equals("")){
                                Log.d("myNum", "onResponse: mynum tuurned to empty !");
                            }
                            Log.d("num3", "onResponse: "+myNum);
                        }
                        else {
                            Toast.makeText(MainActivity.this,
                                    "False Responce: "+response, Toast.LENGTH_LONG).show();

//                            noCurrentUser=true;
                            myNum="";
                            Log.d("mynum", "onResponse: setting to null");

//                            Intent
                        }

                        if (myNum.equals("")){
                            Log.d("myNum", "onStart: Begun activity change");
                            Intent i=new Intent(MainActivity.this, HomeActivity.class);
                            Log.d("myNum", "onStart: "+myNum);
                            startActivity(i);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error != null && error.networkResponse!=null){
                    Toast.makeText(MainActivity.this,
                            "ERROR: " + error.getMessage() +
                                    ", \nResponce: " + error.networkResponse.statusCode +
                                    ",\nData: " + new String(error.networkResponse.data),
                            Toast.LENGTH_LONG)
                            .show();
                }

                else{
                    Toast.makeText(MainActivity.this, "ERROR: " + error.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                map.put("id",
                        Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID).trim()
                );
                return map;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);

        requestQueue.add(req);
    }


    void logout(){

        StringRequest req=new StringRequest(
                Request.Method.POST,
                "http://192.168.1.3/chatapp/logout.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String state="";
                        state=response;

                        Toast.makeText(MainActivity.this,
                                "The echoed responce was: "+response,
                                Toast.LENGTH_LONG);

                        if (state.equals("1")){
                            Toast.makeText(
                                    MainActivity.this,
                                    "Logged out.",
                                    Toast.LENGTH_LONG
                            ).show();

//                            noCurrentUser=true;
                            myNum="";
                            Log.d("mynum", "onResponse in logout: ");
                        }
                        else {
                            Toast.makeText(MainActivity.this,
                                    "Failure logging out..."+response, Toast.LENGTH_LONG).show();

//                            noCurrentUser=false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error != null && error.networkResponse!=null){
                    Toast.makeText(MainActivity.this,
                            "ERROR: " + error.getMessage() +
                                    ", \nResponce: " + error.networkResponse.statusCode +
                                    ",\nData: " + new String(error.networkResponse.data),
                            Toast.LENGTH_LONG)
                            .show();
                }

                else{
                    Toast.makeText(MainActivity.this, "ERROR: " + error.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }
        )
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                map.put("id",myNum.trim());
                return map;
            }
        };

        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);

        requestQueue.add(req);
    }
}