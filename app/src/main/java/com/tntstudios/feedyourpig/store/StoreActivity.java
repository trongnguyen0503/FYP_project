package com.tntstudios.feedyourpig.store;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tntstudios.feedyourpig.R;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Glide.with(this)
                .load(R.drawable.bg_swipe)
                .into((ImageView)findViewById(R.id.bg));
    }
}