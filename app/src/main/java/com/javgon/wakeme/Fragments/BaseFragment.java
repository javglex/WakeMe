package com.javgon.wakeme.Fragments;


import android.app.Fragment;
import android.app.ProgressDialog;

import com.javgon.wakeme.R;

public class BaseFragment extends Fragment {

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public void closeFragment(){
        //fragCallInfo.setVisibility(GONE);
        if (getFragmentManager().beginTransaction() != null)
            getActivity().getFragmentManager().popBackStack();

    }

}