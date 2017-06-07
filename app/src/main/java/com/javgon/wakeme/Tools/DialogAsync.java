package com.javgon.wakeme.Tools;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by javgon on 4/12/2017.
 */

public class DialogAsync extends AsyncTask<Void, Void, Void> {

    public DialogAsync(ListActivity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }

    /** progress dialog to show user that the backup is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private ListActivity activity;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setMessage("loading");
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Do your request

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
