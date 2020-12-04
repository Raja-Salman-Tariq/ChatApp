package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    AppCompatEditText uname, pwd, email, phno;
    AppCompatButton registerButton;

    FirebaseAuth authentication;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authentication=FirebaseAuth.getInstance();

        registerButton=findViewById(R.id.registerButton);
        uname=findViewById(R.id.username);
        pwd=findViewById(R.id.password);
        email=findViewById(R.id.email);
        phno=findViewById(R.id.registerPhno);

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                authentication.createUserWithEmailAndPassword(
                        email.getText().toString(),
                        pwd.getText().toString()
                )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration Successfull !",
                                    Toast.LENGTH_LONG
                            ).show();
                            Toast.makeText(
                                    RegisterActivity.this,
                                    authentication.getCurrentUser().getUid(),
                                    Toast.LENGTH_LONG
                            ).show();



                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=user.getUid();
                            dbRef=FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String, String> map=new HashMap<>();
                            map.put("name", uname.getText().toString());
//                            map.put("email", email);
                            map.put("status", "Hi there ! Update me please !");
                            map.put("image", "default");
                            map.put("thumbnail", "default");

                            dbRef.setValue(map);

                            FirebaseDatabase.getInstance().getReference().child("number-user-map").
                                    child(phno.getText().toString()).setValue(uid);

                            Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })


                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Failed to create user: "+e.getMessage().toString(),
                                Toast.LENGTH_LONG
                        ).show();
                        Log.e("LoginActivity", "Failed Registration"+e.getMessage(), e);

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=authentication.getCurrentUser();

        if (user!=null){
            Toast.makeText(
                    RegisterActivity.this,
                    "Logged In With User: "+user.getUid(),
                    Toast.LENGTH_SHORT
            ).show();
        }

    }
}