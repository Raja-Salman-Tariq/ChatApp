package com.rajasalmantariq.a2retry;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class ChatApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder bilder= new Picasso.Builder(this);
        bilder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso bilt=bilder.build();
        bilt.setIndicatorsEnabled(true);
        bilt.setLoggingEnabled(true);
        Picasso.setSingletonInstance(bilt);
    }
}
