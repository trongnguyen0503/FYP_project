    package com.tntstudios.feedyourpig.gameplay;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tntstudios.feedyourpig.R;

import java.util.Timer;
import java.util.TimerTask;

public class Box {
    private Bitmap iron, candy,star,wood, thom, icestar,icestareffect,candy_help;
    private Bitmap[] candy_explosion = new Bitmap[10];
    private int[] candy_explosion_id = {R.drawable.candy_explosion1,R.drawable.candy_explosion2,R.drawable.candy_explosion3,R.drawable.candy_explosion4,R.drawable.candy_explosion5,R.drawable.candy_explosion6,R.drawable.candy_explosion7,R.drawable.candy_explosion8,R.drawable.candy_explosion9,R.drawable.candy_explosion10};

    private Bitmap[] ice = new Bitmap[4];
    private int[] ice_id = {R.drawable.icebox1,R.drawable.icebox2,R.drawable.icebox3,R.drawable.icebox4};
    private Bitmap [] ice_break = new Bitmap[9];
    private int[] ice_break_id = {R.drawable.icebreak1,R.drawable.icebreak2,R.drawable.icebreak3,R.drawable.icebreak4,R.drawable.icebreak5,R.drawable.icebreak6,R.drawable.icebreak7,R.drawable.icebreak8,R.drawable.icebreak9};

    private boolean isLoaded =false;
    private int ice_prepare_break_frame = 1;
    private int ice_prepare_break_frame_wait = 0;
    private int candy_explosion_frame = 0;
    public Box(Context context, int s) {
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.candy)
                .override(s,s)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        candy =resource;
                    }
                });
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.btnstylehelp)
                .override(s,s)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        candy_help =resource;
                    }
                });
        for (int i = 0;i<=9;i++){
            final int finalI = i;
            Glide.with(context)
                    .asBitmap()
                    .load(candy_explosion_id[i])
                    .override(s,s)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            candy_explosion[finalI]= resource;
                        }
                    });
        }

        Glide.with(context)
                .asBitmap()
                .load(R.drawable.ironbox)
                .override(s,s)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        iron =resource;
                    }
                });
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.star)
                .override(s,s)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        star=resource;
                    }
                });
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.woodbox)
                .override(s,s)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        wood=resource;
                    }
                });
        Glide.with(context)
                .asBitmap()
                .load(R.drawable.thornbox)
                .override(s,s)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        thom =resource;
                    }
                });
        for(int i =0;i<=3;i++){
            final int finalI = i;
            Glide.with(context)
                    .asBitmap()
                    .load(ice_id[i])
                    .override(s,s)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ice[finalI]=resource;
                        }
                    });
        }
        for(int i =0;i<=8;i++){
            final int finalI = i;
            Glide.with(context)
                    .asBitmap()
                    .load(ice_break_id[i])
                    .override(s,s)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            ice_break[finalI]=resource;
                        }
                    });
        }

    }
    public boolean isLoaded(){
        if (isLoaded)return true;
        if (iron==null)return false;
        if (candy ==null)return false;
        if (wood ==null)return false;
        if (thom ==null)return false;
        if (ice ==null)return false;
        isLoaded=true;
        return true;
    }
    public Bitmap Iron() {
        return iron;
    }
    public Bitmap Candy() {
        return candy;
    }
    public Bitmap Help() {
        return candy_help;
    }
    public Bitmap Star(){
        return star;
    }
    public Bitmap Wood(){
        return wood;
    }
    public Bitmap Hole(){
        return thom;
    }
    public Bitmap Ice(){
        return ice[0];
    }
    public Bitmap IcePrepareBreak(){
        if(ice_prepare_break_frame_wait ==3){
            ice_prepare_break_frame_wait=0;
            ice_prepare_break_frame++;
            if(ice_prepare_break_frame==4){
                ice_prepare_break_frame=1;
            }
        }else{
            ice_prepare_break_frame_wait++;
        }
        return ice[ice_prepare_break_frame];
    }
    public Bitmap IceStar(){
        return icestar;
    }
    public Bitmap IceStarPrepareBreak(){
        return icestareffect;
    }
    public Bitmap IceBreak(int frame){
        return ice_break[frame];
    }
    public void reset(){
        ice_prepare_break_frame = 1;
        ice_prepare_break_frame_wait = 0;

        candy_explosion_frame = 0;
    }
}