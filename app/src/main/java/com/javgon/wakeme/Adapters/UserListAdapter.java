package com.javgon.wakeme.Adapters;

import android.app.Activity;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.javgon.wakeme.Model.UserSlot;
import com.javgon.wakeme.Other.CircleTransform;
import com.javgon.wakeme.R;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by javier gonzalez on 5/22/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private List<UserSlot> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvHoursUntil, tvLocation;
        public ConstraintLayout linearInfo;
        public Button btnSendMessage;
        public ImageView imgProfilePic;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvHoursUntil = (TextView) view.findViewById(R.id.tv_timeUntilAlarm);
            tvLocation = (TextView) view.findViewById(R.id.tv_location);
            linearInfo = (ConstraintLayout) view.findViewById(R.id.layout_user_slot);
            btnSendMessage = (Button) view.findViewById(R.id.btn_send_alarm);
            imgProfilePic = (ImageView) view.findViewById(R.id.img_profile_pic);
        }
    }


    public UserListAdapter(List<UserSlot> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_slot_row, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        holder.linearInfo.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // showDetailAtm(view, list.get(position));
            }
        }));

        holder.btnSendMessage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d("VIEWHOLDER", "CLICKED SEND ON " + holder.getAdapterPosition());
            }
        }));


        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        UserSlot userSlot = list.get(position);
        holder.tvHoursUntil.setText(userSlot.getHoursUntilAlarm() + " hours until alarm rings");
        holder.tvLocation.setText(userSlot.getlCoordinates().toString());
        holder.tvName.setText(userSlot.getUsername());
        // Loading profile image
        Glide.with(holder.itemView.getContext()).load(userSlot.getProfilePic())
                .thumbnail(0.5f)
                .bitmapTransform(new CropCircleTransformation(holder.itemView.getContext())) //https://github.com/wasabeef/glide-transformations
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgProfilePic);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }






    /**
     Showing atm details when its row is clicked
     */

    /*private void showDetailAtm(View view, userSlot info){
        //inflate card page

        AtmDetailsFragment nextFrag= AtmDetailsFragment.newInstance(info);
        if (((Activity)view.getContext()).getFragmentManager().findFragmentByTag("ATMDETAILS")==null) { //if fragment does not exist
            ((Activity)view.getContext()).getFragmentManager().beginTransaction()
                    .add(R.id.frag_container, nextFrag, "ATMDETAILS")
                    .addToBackStack(null)
                    .commit();
        }


    }*/
}
