package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    AppCompatEditText email, pwd;
    AppCompatButton loginBtn;
    FirebaseUser usr;
    FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.loginUsername);
        email.setText("Abc@nu.edu.pk");
        pwd=findViewById(R.id.loginPassword);
        pwd.setText("Password ");
        loginBtn=findViewById(R.id.loginBtn);

        authentication=FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authentication.signInWithEmailAndPassword(
                        email.getText().toString(),
                        pwd.getText().toString()
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            usr=authentication.getCurrentUser();
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login successful !!",
                                    Toast.LENGTH_LONG
                            ).show();
                            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                LoginActivity.this,
                                "Login failure...",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            }
        });
    }
}