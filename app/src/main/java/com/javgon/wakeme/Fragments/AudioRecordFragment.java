package com.javgon.wakeme.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.javgon.wakeme.Activities.BaseActivity;
import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.AudioMessage;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.Services.DatabaseServices;
import com.javgon.wakeme.Services.StorageServices;
import com.javgon.wakeme.R;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by javgon on 5/29/2017.
 */

public class AudioRecordFragment extends DialogFragment implements View.OnClickListener{

    String AudioSavePathInDevice = null;
    String mAudioFileName;
    final String audioFileBase="user_recording_";
    ImageButton btnRecord, btnSend;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer ;
    boolean isRecording=false;
    StorageServices mStorageRef;
    DatabaseServices mDatabaseRef;
    String mOpponentUID;

    public static AudioRecordFragment newInstance(String opponentUserId){
        AudioRecordFragment newFrag = new AudioRecordFragment();
        Bundle args = new Bundle();
        args.putString("opponentUserId",opponentUserId);
        newFrag.setArguments(args);

        return newFrag;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.frag_audio_record,null);

        btnRecord=(ImageButton) v.findViewById(R.id.btn_record);
        btnSend = (ImageButton) v.findViewById(R.id.btn_send);
        btnRecord.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        mStorageRef=StorageServices.getInstance(getActivity());
        mDatabaseRef=DatabaseServices.getInstance(getActivity());

        mOpponentUID=getArguments().getString("opponentUserId");


        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_send:
                stopListenRecording();
                uploadAudio();
                break;
            case R.id.btn_record:
                if (!isRecording) {
                    stopListenRecording();
                    recordAudio();
                }
                else
                    stopRecordAudio();
                break;
            default:
                break;
        }
    }

    private void recordAudio(){

        mAudioFileName=CreateRandomAudioFileName()+ ".3gp";
        AudioSavePathInDevice =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        mAudioFileName ;
        MediaRecorderReady();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            showStopButton(true);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    private void stopRecordAudio(){

        mediaRecorder.stop();
        showStopButton(false);
        listenRecording();
    }

    private void listenRecording(){

        mediaPlayer = new MediaPlayer();
        try {

            mediaPlayer.setDataSource(AudioSavePathInDevice);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            Toast.makeText(getActivity(), "duration: "+mediaPlayer.getDuration(),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

    }

    private void stopListenRecording(){

        if(mediaPlayer != null){
            if ( mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                MediaRecorderReady();
            }
        }
    }

    private void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }


    private String CreateRandomAudioFileName(){
        StringBuilder stringBuilder = new StringBuilder( audioFileBase );

        String randomID= UUID.randomUUID().toString();
        stringBuilder.append(randomID);

        return stringBuilder.toString();
    }


    private void showStopButton(boolean show){
        if (show)
            btnRecord.setImageResource(R.mipmap.ic_record_stop);
        else btnRecord.setImageResource(R.mipmap.ic_manual_record);

        isRecording=show;

    }


    private void uploadAudio(){

        mStorageRef.uploadAudio(AudioSavePathInDevice, mAudioFileName, new StorageServices.UploadCallBack(){

            @Override
            public void onSuccess() {
                sendMessage();
                ((BaseActivity)getContext()).showSnack("Message sent!");

            }

            @Override
            public void onFail(String msg) {
                ((BaseActivity)getContext()).showSnack("Message could not be sent!");
            }
        });

    }


    private void sendMessage(){

        String fromUID= MyUserData.getInstance(getActivity()).getUserID();
        String messageID="message_"+ UUID.randomUUID().toString();
        AudioMessage message = new AudioMessage(messageID,mAudioFileName,fromUID,mOpponentUID);

        mDatabaseRef.sendUserMessage(message);

    }



}
