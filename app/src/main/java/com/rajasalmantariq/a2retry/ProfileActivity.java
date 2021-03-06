package com.rajasalmantariq.a2retry;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    Intent intent;

    ImageView img;
    TextView name,status, other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_profile);

        intent=getIntent();

        name=findViewById(R.id.profileName);
        name.setText(intent.getStringExtra("name"));

        status=findViewById(R.id.profileStatus);
        status.setText(intent.getStringExtra("Phno"));

        other=findViewById(R.id.profileOther);
        other.setText(intent.getStringExtra("Status"));

        img=findViewById(R.id.profileImage);
        Picasso.get().load(intent.getStringExtra("img")).into(img);
        


    }
}