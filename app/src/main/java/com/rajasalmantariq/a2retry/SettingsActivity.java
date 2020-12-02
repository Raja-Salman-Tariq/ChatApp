package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    DatabaseReference dbRef;
    FirebaseUser user;

    CircleImageView uImage;
    TextView uName, uStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        uName=findViewById(R.id.nameTV);
        uStatus=findViewById(R.id.statusTV);
        uImage=findViewById(R.id.settings_image);

        user= FirebaseAuth.getInstance().getCurrentUser();
        dbRef= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name= dataSnapshot.child("name").getValue().toString();
                String img= dataSnapshot.child("image").getValue().toString();
                String status= dataSnapshot.child("status").getValue().toString();
                String thumb= dataSnapshot.child("thumbnail").getValue().toString();

                uName.setText(name);
                uStatus.setText(status);
//                uImage.set
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}