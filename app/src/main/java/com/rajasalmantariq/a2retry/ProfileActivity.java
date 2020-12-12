package com.rajasalmantariq.a2retry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    Intent intent;

    ImageView img;
    TextView name,status, other;
    String url="http://192.168.1.2/chatapp/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        intent=getIntent();

        name=findViewById(R.id.profileName);
        name.setText(intent.getStringExtra("name"));

        status=findViewById(R.id.profileStatus);
        status.setText(intent.getStringExtra("status"));

        other=findViewById(R.id.profileOther);
        other.setText("Any other profile informations/details.");

        img=findViewById(R.id.profileImage);
//        Picasso.get().load(intent.getStringExtra("img")).into(img);

        if (intent.getStringExtra("img").equals("1")) {
            Glide.with(ProfileActivity.this).load(url + "imgs/" + status.getText() + ".jpeg")
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(img);

            Log.d("pic", "onCreate: "+status.getText());
        }

        else{
            Glide.with(ProfileActivity.this).load(url + "imgs/backup.jpg")
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(img);
        }


    }
}