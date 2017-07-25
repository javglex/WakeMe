package com.javgon.wakeme.Services;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.javgon.wakeme.Model.AudioMessage;

import java.io.File;
import java.io.IOException;

/**
 * Created by javier gonzalez on 5/31/2017.
 */

public class StorageServices{

    public static StorageServices storageServices;
    StorageReference mStorage;
    static Context mContext;

    public static StorageServices getInstance(Context context){
        if (storageServices == null) {
            storageServices = new StorageServices(context);
        }
        return storageServices;
    }

    private StorageServices(Context context){
        mStorage= FirebaseStorage.getInstance().getReference();
        this.mContext = context;

    }


    public void uploadAudio(String audioPathInDevice, String audioFileName, final UploadCallBack callback){

        StorageReference filePath = mStorage.child("Audio").child(audioFileName);
        Uri uri = Uri.fromFile(new File(audioPathInDevice));

        filePath.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onFail(exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                callback.onSuccess();
            }
        });

    }


    public void downloadAudio(AudioMessage msg, final DownloadCallBack callback){

        StorageReference filePath = mStorage.child("Audio");

        File storagePath = new File(Environment.getExternalStorageDirectory(), "WakeMe_Audio_Files");
        // Create direcorty if not exists
        if(!storagePath.exists()) {
            storagePath.mkdirs();
        }

        final File myFile = new File(storagePath,msg.getUriString());

        filePath.child(msg.getUriString()).getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                callback.onSuccess();
                Log.d("STORAGESERVICE","DOWNLOAD COMPLETE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                callback.onFail(exception.toString());
            }
        });
    }


    public interface UploadCallBack{
        void onSuccess();
        void onFail(String msg);
    }
    public interface DownloadCallBack{
        void onSuccess();
        void onFail(String msg);
    }
}
