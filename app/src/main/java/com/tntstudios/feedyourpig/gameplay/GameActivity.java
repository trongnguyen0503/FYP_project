package com.tntstudios.feedyourpig.gameplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.tntstudios.feedyourpig.OnTouchAnimated;
import com.tntstudios.feedyourpig.R;
import com.tntstudios.feedyourpig.adapters.BoxAdapter;
import com.tntstudios.feedyourpig.adapters.MapAdapter;
import com.tntstudios.feedyourpig.service.SoundInterface;
import com.tntstudios.feedyourpig.store.StoreActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class GameActivity extends AppCompatActivity implements GameInterface {
    private int width_screen,height_screen;
    private GameView gameView;
    private ConstraintLayout view;
    private View view_black;
    private ImageView bg,pig,chair,btn_replay,btn_pause,btn_help,btn_store,btn_redeem,img_star;
    private TextView txt_count_star;
    private RecyclerView rv_box,rv_map;
    private BoxAdapter boxAdapter;
    private ScrollingPagerIndicator recyclerIndicator;
    private MapAdapter mapAdapter;
    private int game_array[][] = new int[11][19];
    private int game_prepare[] = new int[4];
    private List<Integer> game_help = new ArrayList<>();
    private JSONObject json_main;
    private List<String> box_name= new ArrayList<>();
    private int[] bg_id = {R.drawable.bg_iron,R.drawable.bg_wood,R.drawable.bg_ice};
    private int choose_bg_id =0;
    private Context context = this;
    private String choose_box = "";
    private int choose_map = 0;
    private int lastPos;
    private AnimationDrawable anim_pig;
    private SharedPreferences.Editor keys_edit;
    private SharedPreferences keys;
    private int[] list_star_map = new int[25];
    private int step =0;
    private Handler handler = new Handler();
    private Runnable runnable_choose_box,runnable_back_box,runnable_choose_map,runnable_back_map,runnable_replay_game,runnable_help_game,runnable_scroll_box;
    private GameInterface gameInterface = this;
    private InterstitialAd mInterstitialAd;
    private int count_play =0;
    private SoundInterface soundInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        soundInterface = new SoundInterface(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("loadAdError",loadAdError.toString());
            }
        });
        initKeys();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width_screen = displayMetrics.widthPixels;
        height_screen = displayMetrics.heightPixels;
        initId();
        initRunnable();

        try {
            json_main = new JSONObject(keys.getString(getString(R.string.maze),""));
            JSONArray json_box = json_main.getJSONArray("box");
            for (int i =0; i< json_box.length();i++) {
                box_name.add(json_box.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        boxAdapter= new BoxAdapter(context,box_name);
        pig.setImageResource(R.drawable.pig_stand_anim);
        anim_pig = (AnimationDrawable)pig.getDrawable();
        anim_pig.start();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        final LinearSnapHelper linearSnapHelper = new LinearSnapHelper(){
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                View centerView = findSnapView(layoutManager);
                if (centerView == null)
                    return RecyclerView.NO_POSITION;

                int position = layoutManager.getPosition(centerView);
                int targetPosition = -1;
                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));
                return targetPosition;
            }
        };
        linearSnapHelper.attachToRecyclerView(rv_box);;
        rv_box.setLayoutManager(linearLayoutManager);
        rv_box.setAdapter(boxAdapter);
        rv_box.smoothScrollToPosition(0);
        recyclerIndicator = findViewById(R.id.indicator);
        recyclerIndicator.attachToRecyclerView(rv_box);
        lastPos = -1;
        rv_box.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View centerView = linearSnapHelper.findSnapView(linearLayoutManager);
                    int pos = linearLayoutManager.getPosition(centerView);
                    if (lastPos!=pos){
                        centerView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.box));
                        lastPos=pos;
                    }
                }
            }
        });
        initGameView();
    }
    private void initId(){
        view        =findViewById(R.id.game_layout_view);
        view_black  =findViewById(R.id.view_black);
        bg          =findViewById(R.id.gv_bg);
        rv_box=findViewById(R.id.rv_box);
        rv_map=findViewById(R.id.rv_map);
        pig=findViewById(R.id.pig);
        chair=findViewById(R.id.chair);
        mapAdapter = new MapAdapter(context,list_star_map);
        rv_map.setLayoutManager((new GridLayoutManager(context, 5)));
        rv_map.setAdapter(mapAdapter);
        btn_replay=findViewById(R.id.btn_replay);
        btn_pause=findViewById(R.id.btn_pause);
        btn_help=findViewById(R.id.btn_help);
        btn_store=findViewById(R.id.btn_store);
        btn_redeem=findViewById(R.id.btn_redeem);
        img_star=findViewById(R.id.img_star);
        txt_count_star=findViewById(R.id.txt_count_star);
        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(bg);
        Glide.with(this)
                .load(R.drawable.chair)
                .into(chair);
        Glide.with(context)
                .load(R.drawable.btncirclestyle)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        btn_replay.setBackground(resource);
                        btn_pause.setBackground(resource);
                        btn_help.setBackground(resource);
                        btn_store.setBackground(resource);
                        btn_redeem.setBackground(resource);
                    }
                });
        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                checkAdInterstitial();
                view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(200).start();
                handler.postDelayed(runnable_replay_game,200);
            }
        });
        btn_replay.setOnTouchListener(new OnTouchAnimated());

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                onGamePause();
            }
        });
        btn_pause.setOnTouchListener(new OnTouchAnimated());

        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                game_help.clear();
                try {
                    JSONArray json_box = json_main.getJSONArray(choose_box);
                    JSONObject json_map = json_box.getJSONObject(choose_map);
                    JSONArray json_help = json_map.getJSONArray("help");
                    for(int i =0 ; i<json_help.length();i++){
                        game_help.add(json_help.getInt(i));
                    }
                    Collections.reverse(game_help);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(200).start();
                handler.postDelayed(runnable_help_game,200);
            }
        });
        btn_help.setOnTouchListener(new OnTouchAnimated());
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this, StoreActivity.class);
                startActivity(intent);
            }
        });
        btn_store.setOnTouchListener(new OnTouchAnimated());
    }
    private void initGameView() {
        gameView = new GameView(GameActivity.this, width_screen, height_screen);
        view.addView(gameView);
    }
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
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
    @Override
    public void ChooseBox(int box) {
        soundInterface.click();
        step=1;
        view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(500).start();
        choose_box = box_name.get(box);
        choose_bg_id = box;
        for (int i =0 ;i<=24;i++){
            list_star_map[i]=keys.getInt(String.valueOf(choose_bg_id*25+i+1),-1);
        }
        if(choose_box.equals("iron")){
            list_star_map[0]=keys.getInt(String.valueOf(1),0);
        }
        mapAdapter.notifyDataSetChanged();
        handler.postDelayed(runnable_choose_box,500);
    }
    private void initRunnable(){
        runnable_scroll_box = new Runnable() {
            @Override
            public void run() {
                if(choose_bg_id<bg_id.length){
                    rv_box.smoothScrollToPosition(choose_bg_id+1);
                }
            }
        };
        runnable_choose_box = new Runnable() {
            @Override
            public void run() {
                rv_map.setVisibility(View.VISIBLE);
                rv_box.setVisibility(View.GONE);
                recyclerIndicator.setVisibility(View.GONE);
                pig.setVisibility(View.GONE);
                chair.setVisibility(View.GONE);
                anim_pig.stop();
                Glide.with(context)
                        .load(bg_id[choose_bg_id])
                        .centerCrop()
                        .into(bg);
                view_black.animate().alphaBy(1f).alpha(0f).setDuration(500).start();
            }
        };
        runnable_back_box = new Runnable() {
            @Override
            public void run() {
                step=0;
                rv_box.setVisibility(View.VISIBLE);
                recyclerIndicator.setVisibility(View.VISIBLE);
                pig.setVisibility(View.VISIBLE);
                anim_pig.start();
                chair.setVisibility(View.VISIBLE);
                rv_map.setVisibility(View.GONE);
                button_gameVisibility(false);
                gameView.setOnTouchListener(null);
                view_black.animate().alphaBy(1f).alpha(0f).setDuration(500).start();
                Glide.with(context)
                        .load(R.drawable.background)
                        .centerCrop()
                        .into(bg);
            }
        };
        runnable_choose_map = new Runnable() {
            @Override
            public void run() {
                view_black.animate().alphaBy(1f).alpha(0f).setDuration(500).start();
                startGame();
                button_gameVisibility(true);
            }
        };
        runnable_back_map = new Runnable() {
            @Override
            public void run() {
                step=1;
                rv_map.setVisibility(View.VISIBLE);
                view_black.animate().alphaBy(1f).alpha(0f).setDuration(500).start();
                button_gameVisibility(false);
                gameView.setOnTouchListener(null);
                gameView.isPlaying = false;
            }
        };
        runnable_replay_game = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray json_box = json_main.getJSONArray(choose_box);
                    JSONObject json_map = json_box.getJSONObject(choose_map);
                    for(int i = 0;i<=3;i++){
                        game_prepare[i]=json_map.getJSONArray("prepare").getInt(i);
                    }
                    for(int i = 0;i<=10;i++)
                        for(int j =0;j<=18;j++){
                            game_array[i][j]=json_map.getJSONArray("game").getInt(11*j+i);
                        }
                    startGame();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view_black.animate().alphaBy(1f).alpha(0f).setDuration(200).start();
            }
        };
        runnable_help_game = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray json_box = json_main.getJSONArray(choose_box);
                    JSONObject json_map = json_box.getJSONObject(choose_map);
                    for(int i = 0;i<=3;i++){
                        game_prepare[i]=json_map.getJSONArray("prepare").getInt(i);
                    }
                    for(int i = 0;i<=10;i++)
                        for(int j =0;j<=18;j++){
                            game_array[i][j]=json_map.getJSONArray("game").getInt(11*j+i);
                        }
                    startGame();
                    gameView.help(game_help);
                    gameView.isHelp = game_help.size();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                view_black.animate().alphaBy(1f).alpha(0f).setDuration(200).start();
            }
        };
    }
    @Override
    public void ChooseMap(int map) {
        soundInterface.click();
        step=2;
        choose_map = map;
        view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(500).start();
        try {
            JSONArray json_box = json_main.getJSONArray(choose_box);
            JSONObject json_map = json_box.getJSONObject(choose_map);
            for(int i = 0;i<=3;i++){
                game_prepare[i]=json_map.getJSONArray("prepare").getInt(i);
            }
            for(int i = 0;i<=10;i++)
                for(int j =0;j<=18;j++){
                    game_array[i][j]=json_map.getJSONArray("game").getInt(11*j+i);
                }
            handler.postDelayed(runnable_choose_map,500);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Win() {
        soundInterface.win();
        checkAdInterstitial();
        if(keys.getInt(String.valueOf(choose_bg_id*25+choose_map+1),0)<gameView.star_count){
            keys_edit.putInt(String.valueOf(choose_bg_id*25+choose_map+1),gameView.star_count);
            list_star_map[choose_map]=gameView.star_count;
        }
        if((choose_bg_id*25+choose_map+2)<(25*bg_id.length)){
            if(keys.getInt(String.valueOf(choose_bg_id*25+choose_map+2),-1)==-1){
                keys_edit.putInt(String.valueOf(choose_bg_id*25+choose_map+2),0);
            }
        }
        if (choose_map+1<24&&list_star_map[choose_map+1]==-1){
            list_star_map[choose_map+1]=0;
        }
        keys_edit.apply();
        mapAdapter.notifyDataSetChanged();
        gameView.setOnTouchListener(null);
        final Dialog dialog = new Dialog(GameActivity.this);
        ImageView star1,star2,star3,dl_btn_replay,dl_btn_home,dl_btn_list_map,dl_btn_next;
        dialog.setContentView(R.layout.dialog_win);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        star1=dialog.findViewById(R.id.dl_star_1);
        star2=dialog.findViewById(R.id.dl_star_2);
        star3=dialog.findViewById(R.id.dl_star_3);
        dl_btn_replay=dialog.findViewById(R.id.dl_btn_replay);
        dl_btn_home=dialog.findViewById(R.id.dl_btn_home);
        dl_btn_list_map=dialog.findViewById(R.id.dl_btn_list_map);
        dl_btn_next=dialog.findViewById(R.id.dl_btn_next);
        switch (gameView.star_count){
            case 0:{
                Glide.with(context)
                        .load(R.drawable.star_lose)
                        .into(star1);
                Glide.with(context)
                        .load(R.drawable.star_lose)
                        .into(star2);
                Glide.with(context)
                        .load(R.drawable.star_lose)
                        .into(star3);
                break;
            }
            case 1:{
                star1.setImageResource(R.drawable.win_star_1);
                AnimationDrawable anim_star_1 = (AnimationDrawable) star1.getDrawable();
                anim_star_1.start();
                Glide.with(context)
                        .load(R.drawable.star_lose)
                        .into(star2);
                Glide.with(context)
                        .load(R.drawable.star_lose)
                        .into(star3);
                break;
            }
            case 2:{
                star1.setImageResource(R.drawable.win_star_1);
                AnimationDrawable anim_star_1 = (AnimationDrawable) star1.getDrawable();
                anim_star_1.start();
                star2.setImageResource(R.drawable.win_star_1);
                AnimationDrawable anim_star_2 = (AnimationDrawable) star2.getDrawable();
                anim_star_2.start();
                Glide.with(context)
                        .load(R.drawable.star_lose)
                        .into(star3);
                break;
            }
            case 3:{
                star1.setImageResource(R.drawable.win_star_1);
                AnimationDrawable anim_star_1 = (AnimationDrawable) star1.getDrawable();
                anim_star_1.start();
                star2.setImageResource(R.drawable.win_star_1);
                AnimationDrawable anim_star_2 = (AnimationDrawable) star2.getDrawable();
                anim_star_2.start();
                star3.setImageResource(R.drawable.win_star_1);
                AnimationDrawable anim_star_3 = (AnimationDrawable) star3.getDrawable();
                anim_star_3.start();
                break;
            }
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(500).start();
                handler.postDelayed(runnable_back_map,500);
            }
        });
        dl_btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(200).start();
                handler.postDelayed(runnable_replay_game,200);
                dialog.dismiss();
            }
        });
        dl_btn_replay.setOnTouchListener(new OnTouchAnimated());

        dl_btn_list_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(500).start();
                handler.postDelayed(runnable_back_map,500);
                dialog.dismiss();
            }
        });
        dl_btn_list_map.setOnTouchListener(new OnTouchAnimated());

        dl_btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                view_black.animate().alphaBy(0f).alpha(1f).setDuration(500).start();
                handler.postDelayed(runnable_back_box,500);
                dialog.dismiss();
            }
        });
        dl_btn_home.setOnTouchListener(new OnTouchAnimated());

        dl_btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                if(choose_map<24){
                    gameInterface.ChooseMap(choose_map+1);
                }else{
                    view_black.animate().alphaBy(0f).alpha(1f).setDuration(500).start();
                    handler.postDelayed(runnable_back_box,500);
                    handler.postDelayed(runnable_scroll_box,1100);
                }
                dialog.dismiss();
            }
        });
        dl_btn_next.setOnTouchListener(new OnTouchAnimated());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideSystemUI();
            }
        });
    }

    @Override
    public void Lose() {
        checkAdInterstitial();
        view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(200).start();
        handler.postDelayed(runnable_replay_game,200);
    }

    private void startGame(){
        rv_map.setVisibility(View.GONE);
        gameView.startGame(game_prepare,game_array);
        gameView.setOnTouchListener(new OnSwipeTouchListener(GameActivity.this){
            @Override
            public void onSwipeRight() {
                if (gameView.candy_animation==0){
                    if(gameView.isHelp==0){
                        gameView.onSwipeRight();

                    }else{
                        if(game_help.get(gameView.isHelp-1)==2){
                            gameView.help_position=0;
                            gameView.onSwipeRight();

                            gameView.isHelp--;
                        }else{
//                            RUNG
                        }
                    }
                }
            }

            @Override
            public void onSwipeLeft() {
                if (gameView.candy_animation==0){
                    if(gameView.isHelp==0){
                        gameView.onSwipeLeft();

                    }else{
                        if(game_help.get(gameView.isHelp-1)==4){
                            gameView.help_position=0;
                            gameView.onSwipeLeft();

                            gameView.isHelp--;
                        }else{
//                            RUNG
                        }
                    }
                }
            }

            @Override
            public void onSwipeTop() {
                if (gameView.candy_animation==0){
                    if(gameView.isHelp==0){
                        gameView.onSwipeTop();

                    }else{
                        if(game_help.get(gameView.isHelp-1)==1){
                            gameView.help_position=0;
                            gameView.onSwipeTop();

                            gameView.isHelp--;
                        }else{
//                            RUNG
                        }
                    }
                }
            }

            @Override
            public void onSwipeBottom() {
                if (gameView.candy_animation==0){
                    if(gameView.isHelp==0){
                        gameView.onSwipeBottom();

                    }else{
                        if(game_help.get(gameView.isHelp-1)==3){
                            gameView.help_position=0;
                            gameView.onSwipeBottom();

                            gameView.isHelp--;
                        }else{
//                            RUNG
                        }
                    }
                }
            }
        });
    }
    private void initKeys(){
        Arrays.fill(list_star_map,-1);
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
            keys = EncryptedSharedPreferences
                    .create(
                            "app_key",
                            masterKeyAlias,
                            context,
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );
            keys_edit = keys.edit();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
    private void button_gameVisibility(boolean i){
        if(i){
            btn_pause.setVisibility(View.VISIBLE);
            btn_replay.setVisibility(View.VISIBLE);
            btn_help.setVisibility(View.VISIBLE);
            btn_store.setVisibility(View.GONE);
            btn_redeem.setVisibility(View.GONE);
            txt_count_star.setVisibility(View.GONE);
            img_star.setVisibility(View.GONE);
        }else{
            btn_pause.setVisibility(View.GONE);
            btn_replay.setVisibility(View.GONE);
            btn_help.setVisibility(View.GONE);
            btn_store.setVisibility(View.VISIBLE);
            btn_redeem.setVisibility(View.VISIBLE);
            txt_count_star.setVisibility(View.VISIBLE);
            img_star.setVisibility(View.VISIBLE);
        }
    }
    private void onGamePause(){
        gameView.isPlaying=false;
        final Dialog dialog = new Dialog(GameActivity.this);
        ImageView dl_btn_home,dl_btn_play,dl_btn_list_map;
        dialog.setContentView(R.layout.dialog_pause);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dl_btn_home=dialog.findViewById(R.id.dl_btn_home);
        dl_btn_play=dialog.findViewById(R.id.dl_btn_play);
        dl_btn_list_map=dialog.findViewById(R.id.dl_btn_list_map);
        dl_btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                gameView.isPlaying=true;
                dialog.dismiss();
            }
        });
        dl_btn_play.setOnTouchListener(new OnTouchAnimated());

        dl_btn_list_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundInterface.click();
                view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(500).start();
                handler.postDelayed(runnable_back_map,500);
                dialog.dismiss();
            }
        });
        dl_btn_list_map.setOnTouchListener(new OnTouchAnimated());

        dl_btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_black.animate().alphaBy(0f).alpha(1f).setDuration(500).start();
                handler.postDelayed(runnable_back_box,500);
                dialog.dismiss();
            }
        });
        dl_btn_home.setOnTouchListener(new OnTouchAnimated());

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                gameView.isPlaying=true;
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideSystemUI();
            }
        });
    }
    private void checkAdInterstitial(){
        count_play++;
        if(count_play >=keys.getInt(getString(R.string.number_of_ad_impressions),5)){
            if (mInterstitialAd.isLoaded()) {
                count_play=0;
                mInterstitialAd.show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if(step==1){
            step=0;
            view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(500).start();
            handler.postDelayed(runnable_back_box,500);
            return;
        }
        if(step==2){
//            step=1;
//            view_black.animate().alphaBy(0.0f).alpha(1f).setDuration(500).start();
//            handler.postDelayed(runnable_back_map,500);
            onGamePause();
            return;
        }
        super.onBackPressed();
    }
}
