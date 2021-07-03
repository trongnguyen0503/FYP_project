package com.tntstudios.feedyourpig.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class SoundInterface {
    private SoundService.SoundBinder binder;
    public SoundInterface(Context context) {
        Intent intent = new Intent(context, SoundService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
               binder = (SoundService.SoundBinder) iBinder;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        context.bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
    }
    public void playMusic(){
        binder.playMusic();
    }
    public void stopMusic(){
        binder.stopMusic();
    }
    public void click(){
        binder.click();
    }
    public void swipe(){
        binder.swipe();
    }
    public void eating(){
        binder.eating();
    }
    public void win(){
        binder.win();
    }
    public void lose(){
        binder.lose();
    }
    public void explosion(){
        binder.explosion();
    }
    public void star(int i){
        switch (i) {
            case 1:{
                binder.star1();
                break;
            }
            case 2:{
                binder.star2();
                break;
            }
            case 3:{
                binder.star3();
                break;
            }

        }
    }
}
