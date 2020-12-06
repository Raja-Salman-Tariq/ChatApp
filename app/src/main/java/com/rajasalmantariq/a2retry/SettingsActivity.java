package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    DatabaseReference dbRef;
    FirebaseUser user;
    StorageReference storageRef;

    CircleImageView uImage;
    TextView uName, uStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_settings);

        uName=findViewById(R.id.nameTV);
        uStatus=findViewById(R.id.statusTV);
        uImage=findViewById(R.id.settings_image);

        user= FirebaseAuth.getInstance().getCurrentUser();
        dbRef= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        dbRef.keepSynced(true);
        storageRef= FirebaseStorage.getInstance().getReference().child("profile_images").child(user.getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name= dataSnapshot.child("name").getValue(String.class);
                Log.d("Setting name", name);
                final String img= dataSnapshot.child("image").getValue(String.class);
                Log.d("Setting img", img);
                String status= dataSnapshot.child("status").getValue(String.class);
                Log.d("Setting status", status);
                String thumb= dataSnapshot.child("thumbnail").getValue(String.class);
                Log.d("Setting thumb", thumb+"was EMPTY !");


                uName.setText(name);
                uStatus.setText(status);
//                uImage.set
                if (img.equals("default")){
                    Log.d("abc2", "entered if: ");

                    StorageReference ref2 = FirebaseStorage.getInstance().getReference().child("profile_images").child("icon.png");
                    ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String dnldUrl1 = uri.toString();
                            Log.d("abc2", "url rcvd in if: " + dnldUrl1);
                            dbRef.child("image").setValue(dnldUrl1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Picasso.get().load(dnldUrl1).into(uImage);
                                }
                            });
                        }

                    });
//                    Picasso.get().load(img).into(uImage);
                }

                else{
                    Picasso.get().load(img).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(uImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(img).into(uImage);
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        uImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i,200);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("abc2", "onActivityResult: ");

        if (resultCode==RESULT_OK && requestCode==200){
            Uri imgUri=data.getData();

//            Bitmap myBmp= BitmapFactory.decodeFile(imgUri.getEncodedPath());
            Bitmap myBmp=null;
            try {
                myBmp= MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            }
            catch (Exception e){}

            myBmp= Bitmap.createScaledBitmap(myBmp, 200, 200, true);
            ByteArrayOutputStream bos= new ByteArrayOutputStream();
            myBmp.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] myBmpArr=bos.toByteArray();
//            Bitmap compressedImageFile = Compressor.compress(SettingsActivity.this);//;{
//                resolution(1280, 720);
//                quality(80);
//                format(Bitmap.CompressFormat.WEBP);
//                size(2_097_152); // 2 MB
//            };
//            ByteArrayOutputStream baos= new ByteArrayOutputStream();
//            compressedImageFile.compress()

            StorageReference thumbRef=FirebaseStorage.getInstance().getReference().child("thumbs").child(user.getUid()+".jpg");
            UploadTask uT=thumbRef.putBytes(myBmpArr);
            uT.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("abc2", "onFailure: thumb uploading");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    final String thumbUrl;
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            dbRef.child("thumbnail").setValue(uri.toString());
                        }
                    });
                }
            });
//            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                    String thumbUrl=task.getResult().getStorage().getDownloadUrl().toString();
//                }
//            });

            storageRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    final String dnldUrl;
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String dnldUrl = uri.toString();

                            if (dnldUrl.equals( "default")) {
                                StorageReference ref2 = FirebaseStorage.getInstance().getReference().child("profile_images").child("icon.png");
                                ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String dnldUrl1 = uri.toString();

                                        Log.d("abc2", "url rcvd in if: " + dnldUrl1);
                                        dbRef.child("image").setValue(dnldUrl1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Picasso.get().load(dnldUrl1).into(uImage);
                                            }
                                        });
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("abc2", "We have failed...: ");
                                    }
                                });
                            }

                            else {

                                Log.d("abc2", "url rcvd: " + dnldUrl);
                                dbRef.child("image").setValue(dnldUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                Picasso.with(SettingsActivity.this).load(dnldUrl).into(uImage);
                                Picasso.get().load(dnldUrl).into(uImage);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("abc2", "url failed: ");
                }
            });
//            uImage.setImageURI(imgUri);
        }
    }
}