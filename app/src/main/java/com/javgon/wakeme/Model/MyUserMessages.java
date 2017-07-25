package com.javgon.wakeme.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.javgon.wakeme.Tools.ObjectSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by javier gonzalez on 6/16/2017.
 * Will store received messages into disk (shared prefs), for later retrieval.
 */

public class MyUserMessages {

    private final String SHARED_PREFS_FILE="AudioMessages";
    private static MyUserMessages myUserMessages;
    ArrayList<AudioMessage> mAudioMessages = new ArrayList<>();
    private Context sContext;

    public static MyUserMessages getInstance(Context context){

        if (myUserMessages==null){
            myUserMessages=new MyUserMessages(context);
        }

        return myUserMessages;
    }

    private MyUserMessages(Context context){
        sContext=context;
        loadFromPrefs();

    }

    public void setAudioMessages(ArrayList<AudioMessage> msgs){
        mAudioMessages=msgs;
        saveToPrefs();

    }


    public ArrayList<AudioMessage> getAudioMessages(){
        loadFromPrefs();
        return mAudioMessages;
    }


    public void addAudioMessage(AudioMessage msg){
        mAudioMessages.add(msg);
        saveToPrefs();
    }

    public void deleteAudioMessage(AudioMessage msg){

        //remove msg
        for(Iterator<AudioMessage> iterator = mAudioMessages.iterator(); iterator.hasNext(); ) {
            if(iterator.next().getMessageId()==msg.getMessageId())
                iterator.remove();
        }
        saveToPrefs();

    }

    private void saveToPrefs(){

        // save the message list to preference
        SharedPreferences prefs = sContext.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mAudioMessages);
        editor.putString("AudioMessages", json);
        editor.commit();
        editor.apply();

    }


    private void loadFromPrefs(){
        try{
            // load messages from preference
            SharedPreferences prefs = sContext.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            JsonReader json = new JsonReader(new StringReader(prefs.getString("AudioMessages", "")));
            json.setLenient(true);
            Type type = new TypeToken< ArrayList < AudioMessage >>() {}.getType();
            mAudioMessages=gson.fromJson(json, type);
        }catch (NullPointerException e){
            e.printStackTrace();

        }
    }


}
