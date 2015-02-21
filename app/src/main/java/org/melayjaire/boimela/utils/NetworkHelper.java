package org.melayjaire.boimela.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {

    private NetworkHelper() {
    }

    public static final boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectionManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectionManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            //showNetworkErrorMessage(context);
            return false;
        }
    }

    public static final void showNetworkErrorMessage(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Attention")
                .setMessage(
                        "Your internet connection is unavailable at this moment. So you will get limited access to this app. Please, connect your device to internet.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        }).show();

    }
}
