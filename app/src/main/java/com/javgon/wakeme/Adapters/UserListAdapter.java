package com.javgon.wakeme.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.javgon.wakeme.Fragments.AudioRecordFragment;
import com.javgon.wakeme.Fragments.UserListFragment;
import com.javgon.wakeme.Model.UserSlot;
import com.javgon.wakeme.R;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by javier gonzalez on 5/22/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private List<UserSlot> list;
    private Context mContext;
    public static final String TAG = "USERLISTADAPTER";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvHoursUntil, tvLocation;
        public ConstraintLayout linearInfo;
        public ImageButton btnSendMessage;
        public ImageView imgProfilePic;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tv_user_name);
            tvHoursUntil = (TextView) view.findViewById(R.id.tv_timeUntilAlarm);
            tvLocation = (TextView) view.findViewById(R.id.tv_location);
            linearInfo = (ConstraintLayout) view.findViewById(R.id.layout_user_slot);
            btnSendMessage = (ImageButton) view.findViewById(R.id.btn_send_alarm);
            imgProfilePic = (ImageView) view.findViewById(R.id.img_profile_pic);
        }
    }


    public UserListAdapter(List<UserSlot> list, Context context) {
        this.list = list;
        mContext=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_slot_row, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        /*holder.linearInfo.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view){
                // showDetailAtm(view, list.get(position));
            }
        }));*/
        holder.btnSendMessage.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Log.d("VIEWHOLDER", "CLICKED SEND ON " + holder.getAdapterPosition());
                String opponentUserId=list.get(holder.getAdapterPosition()).getuID();
                FragmentManager manager = ((FragmentActivity)mContext).getSupportFragmentManager();
                AudioRecordFragment recDialog = AudioRecordFragment.newInstance(opponentUserId);
                recDialog.show(manager,"RecordDialog");
            }
        }));


        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        UserSlot userSlot = list.get(position);
        holder.tvHoursUntil.setText(userSlot.getHoursUntilAlarm() + " hours until alarm");
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





    RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN && rv.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                Log.d(TAG, "onInterceptTouchEvent: click performed");
                rv.findChildViewUnder(e.getX(), e.getY()).performClick();
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    };
}
