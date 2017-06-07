package com.javgon.wakeme.Services;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

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


    public interface UploadCallBack{
        void onSuccess();
        void onFail(String msg);
    }
}
