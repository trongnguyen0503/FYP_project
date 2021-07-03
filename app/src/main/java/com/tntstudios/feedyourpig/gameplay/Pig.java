package com.tntstudios.feedyourpig.gameplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tntstudios.feedyourpig.R;


public class Pig {
    private Bitmap[] stand = new Bitmap[5];
    private int[] stand_id ={R.drawable.pig_stand1,R.drawable.pig_stand2,R.drawable.pig_stand3,R.drawable.pig_stand4,R.drawable.pig_stand5};
    private Bitmap[] eat = new Bitmap[9];
    private int[] eat_id={R.drawable.pig_eat1,R.drawable.pig_eat2,R.drawable.pig_eat3,R.drawable.pig_eat4,R.drawable.pig_eat5,R.drawable.pig_eat6,R.drawable.pig_eat7,R.drawable.pig_eat8,R.drawable.pig_eat9};
    private Bitmap[] sad = new Bitmap[6];
    private int [] sad_id = {R.drawable.pigsad1,R.drawable.pigsad2,R.drawable.pigsad3,R.drawable.pigsad4,R.drawable.pigsad5,R.drawable.pigsad6};
    private int frame_stand =0;
    private int frame_wait_stand =0;
    private int frame_eat =0;
    private int frame_wait_eat =0;
    private int frame_w =4;
    private int frame_wait_w =0;
    private int frame_sad =0;
    public Pig(Context context, int s) {
        for(int i=0;i<=4;i++){
            final int finalI = i;
            Glide.with(context)
                    .asBitmap()
                    .load(stand_id[i])
                    .override(s,s)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            stand[finalI]=resource;
                        }
                    });
        }
        for(int i=0;i<=8;i++){
            final int finalI = i;
            Glide.with(context)
                    .asBitmap()
                    .load(eat_id[i])
                    .override(s,s)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            eat[finalI]=resource;
                        }
                    });
        }
        for(int i=0;i<=5;i++){
            final int finalI = i;
            Glide.with(context)
                    .asBitmap()
                    .load(sad_id[i])
                    .override(s,s)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            sad[finalI]=resource;
                        }
                    });
        }
    }
    public Bitmap stand(){
        if(frame_wait_stand<2){
            frame_wait_stand++;
        }else{
            frame_wait_stand=0;
            if(frame_stand<6){
                frame_stand++;
            }else{
                frame_stand=0;
            }
        }
        if(frame_stand<=4){
            return stand[frame_stand];
        }else{
            return stand[8-frame_stand];
        }
    }
    public Bitmap eat_prepare(){
        if(frame_wait_eat<3){
            frame_wait_eat++;
        }else{
            frame_wait_eat=0;
            if(frame_eat<6){
                frame_eat++;
            }else{
                frame_eat=0;
            }
        }
        if(frame_eat<=4){
            return eat[frame_eat];
        }else{
            return eat[8-frame_eat];
        }
    }
    public Bitmap eat_prepare_win(){
        if(frame_wait_eat<6){
            frame_wait_eat++;
        }else{
            frame_wait_eat=0;
            frame_eat++;
        }
        if(frame_eat<=4){
            return eat[frame_eat];
        }else{
            frame_eat=3;
            return eat[frame_eat];
        }
    }
    public Bitmap eat_win(){
        if(frame_wait_w<6){
            frame_wait_w++;
        }else{
            frame_wait_w=0;
            frame_w++;
        }
        if(frame_w<=8){
            return eat[frame_w];
        }else{
            frame_w=5;
            return eat[frame_w];
        }
    }
    public Bitmap sad(){
        if(frame_wait_w<6){
            frame_wait_w++;
        }else {
            frame_wait_w=0;
            frame_sad++;
            if (frame_sad>5)frame_sad=5;
        }
        return sad[frame_sad];
    }
    public void reset(){
        frame_stand =0;
        frame_wait_stand =0;
        frame_eat =0;
        frame_wait_eat =0;
        frame_w =4;
        frame_wait_w =0;
        frame_sad =0;
    }
}
