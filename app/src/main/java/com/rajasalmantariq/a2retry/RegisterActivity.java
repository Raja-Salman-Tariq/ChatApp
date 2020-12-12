package com.rajasalmantariq.a2retry;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    AppCompatEditText uname, pwd, email, phno;
    String url="http://192.168.1.2/chatapp/insert.php";
    AppCompatButton registerButton;

    FirebaseAuth authentication;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        authentication=FirebaseAuth.getInstance();

        registerButton=findViewById(R.id.registerButton);
        uname=findViewById(R.id.username);
        pwd=findViewById(R.id.password);
        email=findViewById(R.id.email);
        phno=findViewById(R.id.registerPhno);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest req=new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(RegisterActivity.this,
                                        "OK resp: "+response, Toast.LENGTH_LONG).show();

                                if (response.equals("registration successful !")){
                                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                    i.putExtra("MyNumFromRegisterActivity", phno.getText().toString().trim());
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this,
                                "ERROR: "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                )
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map=new HashMap<>();
                        map.put(
                                "name", uname.getText().toString().trim());
                        map.put("img","default");

                        map.put("status","Hi ! Update me please !");
                        map.put("number", phno.getText().toString().trim());
                        map.put("thumbnail", "default");
                        map.put("pwd", pwd.getText().toString().trim());
                        map.put("dev", Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID).trim());

                        return map;
                    }
                };

                RequestQueue requestQueue= Volley.newRequestQueue(RegisterActivity.this);

                requestQueue.add(req);
            }
        });

//        registerButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                authentication.createUserWithEmailAndPassword(
//                        email.getText().toString(),
//                        pwd.getText().toString()
//                )
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//                            Toast.makeText(
//                                    RegisterActivity.this,
//                                    "Registration Successfull !",
//                                    Toast.LENGTH_LONG
//                            ).show();
//                            Toast.makeText(
//                                    RegisterActivity.this,
//                                    authentication.getCurrentUser().getUid(),
//                                    Toast.LENGTH_LONG
//                            ).show();
//
//
//
//                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//                            String uid=user.getUid();
//                            dbRef=FirebaseDatabase.getInstance().getReference().child("users").child(uid);
//                            HashMap<String, String> map=new HashMap<>();
//                            map.put("name", uname.getText().toString());
////                            map.put("email", email);
//                            map.put("status", "Hi there ! Update me please !");
//                            map.put("image", "default");
//                            map.put("thumbnail", "default");
//                            map.put("number", phno.getText().toString());
//
//                            dbRef.setValue(map);
//
//                            FirebaseDatabase.getInstance().getReference().child("number-user-map").
//                                    child(phno.getText().toString()).setValue(uid);
//
//                            Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                })
//
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(
//                                RegisterActivity.this,
//                                "Failed to create user: "+e.getMessage().toString(),
//                                Toast.LENGTH_LONG
//                        ).show();
//                        Log.e("LoginActivity", "Failed Registration"+e.getMessage(), e);
//
//                    }
//                });
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}