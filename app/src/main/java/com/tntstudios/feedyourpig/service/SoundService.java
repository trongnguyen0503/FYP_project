package com.tntstudios.feedyourpig.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tntstudios.feedyourpig.R;
import com.tntstudios.feedyourpig.data.AppKey;

public class SoundService extends Service {
    private AppKey appKey;
    MediaPlayer mediaPlayer;
    SoundPool soundPool;
    int sound_click,sound_swipe, sound_eating, sound_win, sound_lose, sound_explosion, sound_star1,sound_star2,sound_star3;
    private final IBinder binder = new SoundBinder();
    @Override
    public void onCreate() {
        super.onCreate();
        appKey = new AppKey(this);
        Toast.makeText(this,"Service: Create",Toast.LENGTH_SHORT).show();
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.setLooping(true);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        sound_click = soundPool.load(this, R.raw.click,1);
        sound_swipe = soundPool.load(this,R.raw.swipe,1);
        sound_eating = soundPool.load(this,R.raw.eatting,1);
        sound_win = soundPool.load(this,R.raw.win,1);
        sound_lose = soundPool.load(this,R.raw.lose,1);
        sound_explosion = soundPool.load(this,R.raw.explosion,1);
        sound_star1 = soundPool.load(this,R.raw.star1,1);
        sound_star2 = soundPool.load(this,R.raw.star2,1);
        sound_star3 = soundPool.load(this,R.raw.star3,1);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this,"Service: Bind",Toast.LENGTH_SHORT).show();
        if(appKey.getMusic()){
            mediaPlayer.start();
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this,"Service: Create",Toast.LENGTH_SHORT).show();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Service: Destroy",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    public void stopMusic(){
        mediaPlayer.pause();
    }
    public void playMusic(){
        mediaPlayer.start();
    }
    public void click(){
        if (appKey.getSound())
        soundPool.play(sound_click,1f,1f,0,0,1);
    }
    public void swipe(){
        if (appKey.getSound())
            soundPool.play(sound_swipe,1f,1f,0,0,1);
    }
    public  void  eating() {
        if (appKey.getSound())
            soundPool.play(sound_eating,1f,1f,0,0,1);
    }
    public void win(){
        if (appKey.getSound())
            soundPool.play(sound_win,1f,1f,0,0,1);
    }
    public  void lose(){
        if (appKey.getSound())
            soundPool.play(sound_lose,1f,1f,0,0,1);
    }
    public void explosion(){
        if (appKey.getSound())
            soundPool.play(sound_explosion,1f,1f,0,0,1);
    }
    public void star1(){
        if (appKey.getSound())
            soundPool.play(sound_star1,1f,1f,0,0,1);
    }
    public void star2(){
        if (appKey.getSound())
            soundPool.play(sound_star2,1f,1f,0,0,1);
    }
    public void star3(){
        if (appKey.getSound())
            soundPool.play(sound_star3,1f,1f,0,0,1);
    }


    public class SoundBinder extends Binder {
        SoundService getService(){
            return SoundService.this;
        }
        public void stopMusic(){
            getService().stopMusic();
        }
        public void playMusic(){
            getService().playMusic();
        }
        public void click(){
            getService().click();
        }
        public void swipe(){
            getService().swipe();
        }
        public  void eating(){
            getService().eating();
        }
        public void win(){
            getService().win();
        }
        public void lose(){
            getService().lose();
        }
        public void explosion(){
            getService().explosion();
        }
        public void star1(){
            getService().star1();
        }
        public void star2(){
            getService().star2();
        }
        public void star3(){
            getService().star3();
        }
    }
}
