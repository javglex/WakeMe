package com.javgon.wakeme.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.javgon.wakeme.Adapters.UserListAdapter;
import com.javgon.wakeme.Model.Alarm;
import com.javgon.wakeme.Model.PostServices;
import com.javgon.wakeme.Model.UserSlot;
import com.javgon.wakeme.Other.MyUserData;
import com.javgon.wakeme.Other.ScreenUtils;
import com.javgon.wakeme.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by javgon on 5/22/2017.
 */

public class UserListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    private List<UserSlot> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserListAdapter mAdapter;
    private Button btnRefresh;
    SwipeRefreshLayout swipeRefreshLayout;
    MyUserData mUserData;
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
        mAdapter = new UserListAdapter(userList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setDistanceToTriggerSync(200);// in dips
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                super.onScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
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
            Log.d("MyFragment", "Fragment is visible.");

        }
        else {
            Log.d("MyFragment", "Fragment is not visible.");
        }
    }

    @Override
    protected void getUserSlots(){

        ArrayList<Alarm> alarms=mUserData.getOthersAlarm();
        PostServices.getInstance(getActivity()).getUserInfoFromAlarm(alarms,new PostServices.UsersCallBack(){
            @Override
            public void onSuccess(ArrayList<UserSlot> result){
                swipeRefreshLayout.setRefreshing(false);
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
    public void onRefresh() {
        readAlarms();
    }



}
