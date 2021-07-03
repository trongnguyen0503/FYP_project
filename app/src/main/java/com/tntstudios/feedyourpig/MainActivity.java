package com.tntstudios.feedyourpig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.tntstudios.feedyourpig.data.AppKey;
import com.tntstudios.feedyourpig.gameplay.GameActivity;
import com.tntstudios.feedyourpig.service.SoundInterface;
import com.tntstudios.feedyourpig.service.SoundService;

public class MainActivity extends AppCompatActivity {
    private Button btn_main_play,btn_main_challenge;
    private ImageView btn_main_setting;
    private AppKey appKey;
    SoundInterface soundInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        soundInterface = new SoundInterface(this);
        appKey = new AppKey(this);
        initId();
        appKey.checkVersionMaze();
        Glide.with(this)
                .load(R.drawable.backgroundmain)
                .centerCrop()
                .into((ImageView) findViewById(R.id.bg));
        btn_main_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
        btn_main_play.setOnTouchListener(new OnTouchAnimated());
        btn_main_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImageView dl_btn_music,dl_btn_sound;
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_setting);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dl_btn_music=dialog.findViewById(R.id.dl_btn_music);
                dl_btn_sound=dialog.findViewById(R.id.dl_btn_sound);
                if(appKey.getMusic()){
                    dl_btn_music.setImageResource(R.drawable.music_on);
                }else{
                    dl_btn_music.setImageResource(R.drawable.music_off);
                }
                if(appKey.getSound()){
                    dl_btn_sound.setImageResource(R.drawable.sound_on);
                }else{
                    dl_btn_sound.setImageResource(R.drawable.sound_off);
                }
                dialog.show();
                dl_btn_music.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(appKey.getMusic()){
                            soundInterface.stopMusic();
                            appKey.setMusic(false);
                            dl_btn_music.setImageResource(R.drawable.music_off);
                        }else{
                            soundInterface.playMusic();
                            appKey.setMusic(true);
                            dl_btn_music.setImageResource(R.drawable.music_on);
                        }
                    }
                });
                dl_btn_sound.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(appKey.getSound()){
                            appKey.setSound(false);
                            dl_btn_sound.setImageResource(R.drawable.sound_off);
                        }else{
                            appKey.setSound(true);
                            dl_btn_sound.setImageResource(R.drawable.sound_on);
                        }
                    }
                });
            }
        });
        btn_main_setting.setOnTouchListener(new OnTouchAnimated());
        Glide.with(this)
                .load(R.drawable.btnstylenext)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        btn_main_play.setBackground(resource);
                        btn_main_challenge.setBackground(resource);
                    }
                });
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void initId() {
        btn_main_play = findViewById(R.id.btn_main_play);
        btn_main_challenge = findViewById(R.id.btn_main_challenge);
        btn_main_setting = findViewById(R.id.btn_main_setting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }
}
