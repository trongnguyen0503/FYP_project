package com.tntstudios.feedyourpig.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.tntstudios.feedyourpig.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class AppKey {
    private Context context;
    private SharedPreferences.Editor keys_edit;
    private SharedPreferences keys;
    public AppKey(Context context) {
        this.context=context;
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
    public void checkVersionMaze(){
        if(keys.getInt(context.getString(R.string.version_maze),-1)==-1){
            keys_edit.putInt(context.getString(R.string.version_maze),0);
            keys_edit.putBoolean(context.getString(R.string.music),true);
            keys_edit.putBoolean(context.getString(R.string.sound),true);
            try {
                InputStream is = context.getAssets().open("game.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                keys_edit.putString(context.getString(R.string.maze),new String(buffer, StandardCharsets.UTF_8));
                keys_edit.apply();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean getMusic(){
        return keys.getBoolean(context.getString(R.string.music),true);
    }
    public void setMusic(boolean i){
        keys_edit.putBoolean(context.getString(R.string.music),i);
        keys_edit.apply();
    }
    public boolean getSound(){
        return keys.getBoolean(context.getString(R.string.sound),true);
    }
    public void setSound(boolean i){
        keys_edit.putBoolean(context.getString(R.string.sound),i);
        keys_edit.apply();
    }
}
