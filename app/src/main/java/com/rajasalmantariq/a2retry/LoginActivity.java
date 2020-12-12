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
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    AppCompatEditText email, pwd;
    AppCompatButton loginBtn;
    FirebaseUser usr;
    FirebaseAuth authentication;

    String url="http://192.168.1.2/chatapp/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        email=findViewById(R.id.loginUsername);
        email.setText("Def");
        pwd=findViewById(R.id.loginPassword);
        pwd.setText("Def");
        loginBtn=findViewById(R.id.loginBtn);

        authentication=FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest req=new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if (response.equals("login successful !")){
                                    Intent i=new Intent(LoginActivity.this,
                                            MainActivity.class);
                                    startActivity(i);
                                }
                                Toast.makeText(LoginActivity.this,
                                        response, Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,
                                "ERROR: "+error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                )
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map=new HashMap<>();
                        map.put("number", /*phno*/email.getText().toString().trim());
                        map.put("pwd", pwd.getText().toString().trim());
                        map.put("id", Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID).trim());

                        return map;
                    }
                };

                RequestQueue requestQueue= Volley.newRequestQueue(LoginActivity.this);

                requestQueue.add(req);
            }
        });

    }
}