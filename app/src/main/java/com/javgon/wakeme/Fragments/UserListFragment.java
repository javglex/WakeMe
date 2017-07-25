package com.javgon.wakeme.Fragments;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.javgon.wakeme.Adapters.UserListAdapter;
import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Services.DatabaseServices;
import com.javgon.wakeme.Model.UserSlot;
import com.javgon.wakeme.Model.MyUserData;
import com.javgon.wakeme.R;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.OVER_SCROLL_ALWAYS;

/**
 * Created by javgon on 5/22/2017.
 */

public class UserListFragment extends BaseFragment implements  View.OnTouchListener{


    private List<UserSlot> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout layoutListContainer;
    private UserListAdapter mAdapter;
    private Button btnRefresh;
    MyUserData mUserData;
    float dY, dragDistance;  //dY = initial touch position, dragDistance = distance dragged from initial touch position
    boolean recycleViewCanScroll=false;

    public static UserListFragment newInstance(){

        UserListFragment newFrag = new UserListFragment();
        Bundle args = new Bundle();

        newFrag.setArguments(args);
        return newFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUserData=mUserData.getInstance(getActivity());
        readAlarms();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_user_slot_list, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        recyclerView = (RecyclerView)view.findViewById(R.id.slot_recycler_view);
        btnRefresh=(Button) view.findViewById(R.id.btn_refresh);
        btnRefresh.setVisibility(View.GONE);  //if succeed, hide refresh button
        mAdapter = new UserListAdapter(userList,getActivity());
        layoutListContainer=(LinearLayout) view.findViewById(R.id.layout_list_container);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnTouchListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {          //refresh only when on top of list

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                super.onScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                recycleViewCanScroll=!(topRowVerticalPosition >= 0);

            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            Log.d("USERLIST", "Fragment is visible.");
            layoutListContainer.requestFocus();

        }
        else {
            Log.d("USERLIST", "Fragment is not visible.");
        }
    }


    @Override
    protected void getUserSlots(){

        ArrayList<Alarm> alarms=mUserData.getOthersAlarm();
        DatabaseServices.getInstance(getActivity()).getUserInfoFromAlarm(alarms,new DatabaseServices.UsersCallBack(){
            @Override
            public void onSuccess(ArrayList<UserSlot> result){
                prepareUserListData(result);
            }
            @Override
            public void onFail(String msg){
                Log.e("USERLIST", msg);
            }
        });
    }

    private void prepareUserListData(ArrayList<UserSlot> list) {
        userList.clear();
        userList.addAll(list);
        Log.d("prepareAtmListData"," "+list.size());
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (v.getId()==R.id.slot_recycler_view)     //get initial touch point
                    dY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                    if (v.getId()==R.id.slot_recycler_view && !recycleViewCanScroll) {      //if recycler view is at top..
                        dragDistance = event.getRawY() - dY;        //calculate distance between initial touch point and current touch point
                        if (dragDistance > 0) {                       //only pull down if user is scrolling down (current touch > init touch point)
                            v.setTranslationY(dragDistance / 4);
                            return true; //so that recycler view doesn't scroll if user moves the layout around
                        }
                    }
                break;
            default:
                if (v.getId()==R.id.slot_recycler_view) {
                    v.animate().y(v.getTop()).setDuration(250).start();      //in any other even (cancel/up) animate the view back into place
                    if (dragDistance/v.getHeight() > .5){
                        readAlarms();
                        dragDistance=0;
                    }
                }
                break;
        }
        return false;
    }

}
