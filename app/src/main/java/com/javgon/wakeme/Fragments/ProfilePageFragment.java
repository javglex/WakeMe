package com.javgon.wakeme.Fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.InputType;
import android.text.method.ArrowKeyMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.javgon.wakeme.CustomAnimation.Ease;
import com.javgon.wakeme.CustomAnimation.EasingInterpolator;
import com.javgon.wakeme.CustomAnimation.JAnimations;
import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.Model.MyUserMessages;
import com.javgon.wakeme.R;

import org.w3c.dom.Text;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by javier gonzalez on 6/29/2017.
 */

public class ProfilePageFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG="PROFILEPAGEFRAG";
    TextView tvProfileName, tvCity, tvCountry, tvDescription;
    EditText etProfileName, etCity, etCountry, etDescription;
    ImageView imgProfilePic, imgEditProfile, imgOk, imgCancel;
    ConstraintLayout mProfileLayout;
    boolean isEditingProfile;

    public static ProfilePageFragment newInstance(){
        ProfilePageFragment newFrag = new ProfilePageFragment();
        //Bundle args = new Bundle();
        //newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.frag_profile_page, container, false);
        isEditingProfile=false;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mProfileLayout=(ConstraintLayout)view.findViewById(R.id.layout_profile);
        tvProfileName=(TextView) mProfileLayout.findViewById(R.id.tv_name);
        tvCity=(TextView) mProfileLayout.findViewById(R.id.tv_city);
        tvCountry=(TextView) mProfileLayout.findViewById(R.id.tv_country);
        tvDescription=(TextView) mProfileLayout.findViewById(R.id.tv_description);
        etProfileName=(EditText)mProfileLayout.findViewById(R.id.et_name);
        etCity=(EditText)mProfileLayout.findViewById(R.id.et_city);
        etCountry=(EditText)mProfileLayout.findViewById(R.id.et_country);
        etDescription=(EditText)mProfileLayout.findViewById(R.id.et_description);
        imgProfilePic = (ImageView)mProfileLayout.findViewById(R.id.img_profile_pic);
        imgEditProfile=(ImageView)mProfileLayout.findViewById(R.id.img_edit_info);
        imgOk= (ImageView)view.findViewById(R.id.img_ok);
        imgCancel=(ImageView)view.findViewById(R.id.img_cancel);

        initViews();


    }


    @Override
    public void onResume(){
        super.onResume();
        loadProfilePic();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d(TAG, "Fragment is visible.");
            loadProfilePic();
        }
        else {
            Log.d(TAG, "Fragment is not visible.");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_edit_info:
                if(!isEditingProfile)
                    editInfo(true);
                break;
            case R.id.img_ok:
                saveInfo();
                editInfo(false);
                break;
            case R.id.img_cancel:
                editInfo(false);
                break;
            default:
                break;
        }

    }

    private void initViews(){
        //hide edit views
        etProfileName.setVisibility(View.GONE);
        etCity.setVisibility(View.GONE);
        etCountry.setVisibility(View.GONE);
        etDescription.setVisibility(View.GONE);

        tvProfileName.setText(MyUserData.getInstance(getActivity()).getUserName());
        imgEditProfile.setOnClickListener(this);
        imgOk.setOnClickListener(this);
        imgCancel.setOnClickListener(this);
        imgOk.setVisibility(View.GONE);
        imgCancel.setVisibility(View.GONE);
        loadProfilePic();

    }

    private void loadProfilePic(){

        Glide.with(this).load(MyUserData.getInstance(getActivity()).getUriProfile())
                .thumbnail(0.5f)
                .bitmapTransform(new CropCircleTransformation(getContext())) //https://github.com/wasabeef/glide-transformations
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfilePic);
        Log.d(TAG, "uri: "+MyUserData.getInstance(getContext()).getUriProfile());
        Log.d(TAG,"myUserData "+MyUserData.getInstance(getActivity()).toString());

    }

    private void editInfo(boolean edit){

        if (edit) {
            isEditingProfile=true;
            //hide text views
            JAnimations.doBounceOutAnimation(tvProfileName,0);
            JAnimations.doBounceOutAnimation(tvCity,0);
            JAnimations.doBounceOutAnimation(tvCountry,0);
            JAnimations.doBounceOutAnimation(tvDescription,0);
            JAnimations.doBounceOutAnimation(imgEditProfile,0);

            //populate edittext views with user info
            etProfileName.setText(tvProfileName.getText());
            etCity.setText(tvCity.getText());
            etCountry.setText(tvCountry.getText());
            etDescription.setText(tvDescription.getText());
            //request focus so that user can type on first editbox
            etProfileName.requestFocus();

            //display edittext views so user can EDIT info
            JAnimations.doBounceInAnimation(imgOk, 250);
            JAnimations.doBounceInAnimation(imgCancel, 300);
            JAnimations.doBounceInAnimation(etCity, 100);
            JAnimations.doBounceInAnimation(etDescription, 200);
            JAnimations.doBounceInAnimation(etCountry , 50);
            JAnimations.doBounceInAnimation(etProfileName, 10);



        }else
        {
            isEditingProfile=false;
            //hide edittext views
            JAnimations.doBounceOutAnimation(etProfileName,0);
            JAnimations.doBounceOutAnimation(etCity,0);
            JAnimations.doBounceOutAnimation(etCountry,0);
            JAnimations.doBounceOutAnimation(etDescription,0);
            JAnimations.doBounceOutAnimation(imgOk,0);
            JAnimations.doBounceOutAnimation(imgCancel,0);

            //display views views so user can SEE info
            JAnimations.doBounceInAnimation(tvCity, 1100);
            JAnimations.doBounceInAnimation(tvDescription, 1200);
            JAnimations.doBounceInAnimation(tvCountry , 1050);
            JAnimations.doBounceInAnimation(tvProfileName, 1010);
            JAnimations.doBounceInAnimation(imgEditProfile, 1010);

        }
    }

    /**
     * Save information that user has edited.
     */
    private void saveInfo(){
        tvProfileName.setText(etProfileName.getText());
        tvCity.setText(etCity.getText());
        tvCountry.setText(etCountry.getText());
        tvDescription.setText(etDescription.getText());
    }




}
