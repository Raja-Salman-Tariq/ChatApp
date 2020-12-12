package com.rajasalmantariq.a2retry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    DatabaseReference dbRef;
    FirebaseUser user;
    StorageReference storageRef;

    CircleImageView uImage;
    EditText uName, uStatus;

    String url = "http://192.168.1.2/chatapp/";

    String img = "default", thumb = "default";
    String encImg;

    Button updateButn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        updateButn=(Button) findViewById(R.id.updateBtn);

        Log.d("UPDBTN", "onCreate: "+findViewById(R.id.updateBtn).toString());
        updateButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        encImg="";
        uName = findViewById(R.id.nameTV);
        uStatus = findViewById(R.id.statusTV);
        uImage = findViewById(R.id.settings_image);


        getUserInfo();

        if (img.equals("default")) {
            uImage.setImageResource(R.mipmap.contact_icon);
//            uImage.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
//            Picasso.get().load(img).placeholder(R.drawable.ic_launcher_foreground).networkPolicy(NetworkPolicy.OFFLINE)
//                    .into(uImage, new Callback() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            Picasso.get().load(R.drawable.ic_launcher_foreground).into(uImage);
//                        }
//                    });
            Log.d("SettingsActivity:uImg", "onCreate: Load data now");
        }



        uImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, 200);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("abc2", "onActivityResult: ");

        if (resultCode == RESULT_OK && requestCode == 200) {
            Uri imgUri = data.getData();

//            Bitmap myBmp= BitmapFactory.decodeFile(imgUri.getEncodedPath());
            Bitmap myBmp = null;
            try {
                myBmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
            } catch (Exception e) {
            }

            myBmp = Bitmap.createScaledBitmap(myBmp, 200, 200, true);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            myBmp.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] myBmpArr = bos.toByteArray();

            encImg = android.util.Base64.encodeToString(myBmpArr, Base64.DEFAULT);

            uImage.setImageURI(imgUri);

        }
    }


    void getUserInfo() {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                url + "getUserInfo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("settings:loadpic", "onResponse: "+response);
                        Toast.makeText(SettingsActivity.this,
                                "The echoed responce was: " + response,
                                Toast.LENGTH_LONG).show();

                        String tId="";

                        if (response.charAt(0) != '*') {

                            StringTokenizer stok = new StringTokenizer(response, ",");

                            if (stok.hasMoreTokens())
                                uName.setText(stok.nextToken());
                            if (stok.hasMoreTokens())
                                thumb = stok.nextToken();
                            if (stok.hasMoreTokens())
                                uStatus.setText(stok.nextToken());
                            if (stok.hasMoreTokens()) {
                                img = stok.nextToken();
                                Log.d("imgtok", "onResponse: "+img);
                            }
                            if (stok.hasMoreTokens()) {
                                tId = stok.nextToken();
                            }
                            else {
                                Log.d("getUserInfo", "onResponse: reading request, failed when trying to read tokens.");
                            }
                        } else {
                            Log.d("getUserInfo", "onResponse: reading request, responce was null.");
                        }

                        if (img.equals("1")){
                            Log.d("settings:loadpic", "onResponse: entered");
//                            Picasso.get().load(url+"imgs/"+tId+".jpeg");
                            Glide.with(SettingsActivity.this).load(url+"imgs/"+tId+".jpeg")
                                    .apply(RequestOptions.skipMemoryCacheOf(true))
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .into(uImage);
                        }
                        else{
                            Log.d("settings:loadpic", "onResponse: nope");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error != null && error.networkResponse != null) {
                    Toast.makeText(SettingsActivity.this,
                            "ERROR: " + error.getMessage() +
                                    ", \nResponce: " + error.networkResponse.statusCode +
                                    ",\nData: " + new String(error.networkResponse.data),
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(SettingsActivity.this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", getIntent().getStringExtra("id").trim());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SettingsActivity.this);

        requestQueue.add(req);
    }



    void updateProfile(){

        Log.d("updprof", "updateProfile: called");
        StringRequest req = new StringRequest(
                Request.Method.POST,
                url + "updateProfile.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("updprof", "updateProfile:on responce called: "+response);

                        Toast.makeText(SettingsActivity.this,
                                "The echoed responce was: " + response,
                                Toast.LENGTH_LONG).show();

                        if (response.charAt(0) != '*') {
                            Log.d("profileUpdate", "onResponse: Succeeded update");
                        } else {
                            Log.d("profileUpdate", "onResponse: Profile update failed.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                if (error != null && error.networkResponse != null) {
                    Toast.makeText(SettingsActivity.this,
                            "ERROR: " + error.getMessage() +
                                    ", \nResponce: " + error.networkResponse.statusCode +
                                    ",\nData: " + new String(error.networkResponse.data),
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    Toast.makeText(SettingsActivity.this, "ERROR: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", getIntent().getStringExtra("id").trim());
                map.put("status", uStatus.getText().toString().trim());
                map.put("name", uName.getText().toString().trim());

                if (!encImg.equals("")) {
                    map.put("thumb", "1");
                    map.put("img", "1");
                    map.put("data", encImg);
                }

                else{
                    map.put("thumb", "default");
                    map.put("img", "default");
                    map.put("data", "default");
                }
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SettingsActivity.this);

        requestQueue.add(req);    }
}