package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button logOutBtn, settingsButton, usersButton, addFrnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("abc", "main  act");
        mAuth = FirebaseAuth.getInstance();
        logOutBtn=findViewById(R.id.logOutButton);
        settingsButton=findViewById(R.id.settingsButton);
        usersButton=findViewById(R.id.usersButton);
        addFrnBtn=findViewById(R.id.addFriend);


        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i=new Intent(MainActivity.this,HomeActivity.class);
                startActivity(i);
                finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
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

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (currentUser==null){
            Intent i=new Intent(MainActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
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
                                FirebaseDatabase.getInstance().getReference().child("friend-lists")
                                        .child(mAuth.getCurrentUser().getUid()).child(otherUid).setValue(otherUid);

                                String uID=mAuth.getCurrentUser().getUid();
                                FirebaseDatabase.getInstance().getReference().child("friend-lists")
                                        .child(otherUid).child(uID).setValue(uID);
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
}